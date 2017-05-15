package com.ratata.nativeQueryRest.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.nativeQueryRest.pojo.QueryListHolder;
import com.ratata.nativeQueryRest.utils.Const;
import com.ratata.nativeQueryRest.utils.Mapper;

@Component
public class DynamicDTODAOimpl implements DynamicDTODAO {
  @Autowired
  private CoreDAO coreDAO;

  public Object nativeWithDynamicPojo(JsonNode pojo) {
    return nativeWithDynamicPojo(pojo.get(Const.PARAM_SINGLEREQUEST_DATA),
        pojo.get(Const.PARAM_SINGLEREQUEST_PARAM));
  }

  public Object nativeWithDynamicPojo(JsonNode node, JsonNode param) {
    if (node.has(Const.PARAM_QUERY)) {
      return coreDAO.processQueryObject(node, param);
    }
    checkNode(node, param);
    return node;
  }

  @Transactional
  public List<Object> transationNativeQuery(String query) throws Exception {
    return transationNativeQuery((ArrayNode) Mapper.mapper.readTree(query));
  }

  @Transactional
  public List<Object> transationNativeQuery(ArrayNode query) {
    List<Object> result = new ArrayList<Object>();
    for (JsonNode qr : query) {
      result.add(nativeWithDynamicPojo(qr));
    }
    return result;
  }

  private void checkNode(JsonNode node, JsonNode paramArray) {
    Iterator<Entry<String, JsonNode>> nodeIter = node.fields();
    while (nodeIter.hasNext()) {
      Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeIter.next();
      JsonNode innerNode = entry.getValue();
      switch (innerNode.getNodeType()) {
        case ARRAY:
          for (int i = 0; i < innerNode.size(); i++) {
            JsonNode tnode = innerNode.get(i);
            if (tnode.has(Const.LINK_QUERY)) {
              processMergeArray(tnode, innerNode, i,
                  processQueryLink(tnode, tnode.has(Const.PARAM_QUERYNAME)
                      ? paramArray.get(tnode.get(Const.PARAM_QUERYNAME).asText()) : null));
              continue;
            }
            if (tnode.has(Const.PARAM_QUERY)) {
              processMergeArray(tnode, innerNode, i,
                  processQueryNode(tnode, tnode.has(Const.PARAM_QUERYNAME)
                      ? paramArray.get(tnode.get(Const.PARAM_QUERYNAME).asText()) : null));
              continue;
            }
            checkNode(tnode, paramArray);
          }
          break;
        case OBJECT:
          if (innerNode.has(Const.LINK_QUERY)) {
            ((ObjectNode) node).replace(entry.getKey(),
                processQueryLink(innerNode, innerNode.has(Const.PARAM_QUERYNAME)
                    ? paramArray.get(innerNode.get(Const.PARAM_QUERYNAME).asText()) : null));
            break;
          }
          if (innerNode.has(Const.PARAM_QUERY)) {
            ((ObjectNode) node).replace(entry.getKey(),
                processQueryNode(innerNode, innerNode.has(Const.PARAM_QUERYNAME)
                    ? paramArray.get(innerNode.get(Const.PARAM_QUERYNAME).asText()) : null));
            break;
          }
          checkNode(innerNode, paramArray);
        default:
          break;
      }
    }
  }

  private void processMergeArray(JsonNode tnode, JsonNode innerNode, Integer i, JsonNode data) {
    if (tnode.has(Const.PARAM_MERGEARRAY) && tnode.get(Const.PARAM_MERGEARRAY).asBoolean()
        && data.isArray()) {
      ((ArrayNode) innerNode).remove(i);
      ((ArrayNode) innerNode).addAll((ArrayNode) data);
    } else {
      ((ArrayNode) innerNode).set(i, data);
    }
  }

  private JsonNode processQueryLink(JsonNode node, JsonNode param) {
    if (QueryListHolder.queryList.containsKey(node.get(Const.LINK_QUERY).asText())) {
      return Mapper.mapper.valueToTree(coreDAO.processQueryObject(
          QueryListHolder.queryList.get(node.get(Const.LINK_QUERY).asText()), param));
    }
    ObjectNode errorNode = Mapper.mapper.createObjectNode();
    errorNode.put(Const.ERRORNODE, Const.LINK_QUERY_QUERYNOTEXIST);
    return errorNode;
  }

  private JsonNode processQueryNode(JsonNode node, JsonNode param) {
    return Mapper.mapper.valueToTree(coreDAO.processQueryObject(node, param));
  }
}
