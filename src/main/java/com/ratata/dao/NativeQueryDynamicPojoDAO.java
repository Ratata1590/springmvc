package com.ratata.dao;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class NativeQueryDynamicPojoDAO {
  public static final String PARAM_QUERY = "query";
  public static final String PARAM_CLASSNAME = "className";
  public static final String PARAM_RESULTSET = "resultSet";
  public static final String PARAM_QUERYMODE = "queryMode";

  public static final String PARAM_MERGEARRAY = "mergeArray";

  public static final String PARAM_SINGLEREQUEST_DATA = "data";
  public static final String PARAM_SINGLEREQUEST_PARAM = "param";

  @Autowired
  private NativeQueryDAO nativeQueryDAO;

  public Object nativeWithDynamicPojo(JsonNode node, ArrayNode param)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    if (node.has(PARAM_QUERY)) {
      return nativeQueryDAO.processQueryObject(node, param);
    }
    processDynamicPojo(node, param);
    return node;
  }

  public void processDynamicPojo(JsonNode node, ArrayNode param)
      throws JsonProcessingException, IOException, ClassNotFoundException {
    int objectNodeNumber = 0;
    checkNode(node, param, objectNodeNumber);

  }

  public void checkNode(JsonNode node, ArrayNode paramArray, int objectNodeNumber)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    Iterator<Entry<String, JsonNode>> nodeIter = node.fields();
    while (nodeIter.hasNext()) {
      Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeIter.next();
      JsonNode innerNode = entry.getValue();
      switch (innerNode.getNodeType()) {
        case ARRAY:
          for (int i = 0; i < innerNode.size(); i++) {
            JsonNode tnode = innerNode.get(i);
            if (tnode.has(CustomQueryListDAO.LINK_QUERY)) {
              processMergeArray(tnode, innerNode, i,
                  processQueryLink(tnode, paramArray, objectNodeNumber));
              objectNodeNumber++;
              continue;
            }
            if (tnode.has(PARAM_QUERY)) {
              processMergeArray(tnode, innerNode, i,
                  processQueryNode(tnode, paramArray, objectNodeNumber));
              objectNodeNumber++;
              continue;
            }

            checkNode(tnode, paramArray, objectNodeNumber);
          }
          break;
        case OBJECT:
          if (innerNode.has(CustomQueryListDAO.LINK_QUERY)) {
            ((ObjectNode) node).replace(entry.getKey(),
                processQueryLink(innerNode, paramArray, objectNodeNumber));
            objectNodeNumber++;
            break;
          }
          if (innerNode.has(PARAM_QUERY)) {
            ((ObjectNode) node).replace(entry.getKey(),
                processQueryNode(innerNode, paramArray, objectNodeNumber));
            objectNodeNumber++;
            break;
          }

          checkNode(innerNode, paramArray, objectNodeNumber);

        default:
          break;
      }
    }
  }

  private void processMergeArray(JsonNode tnode, JsonNode innerNode, int i, JsonNode data) {
    if (tnode.has(PARAM_MERGEARRAY) && tnode.get(PARAM_MERGEARRAY).asBoolean()) {
      ((ArrayNode) innerNode).remove(i);
      ((ArrayNode) innerNode).addAll((ArrayNode) data);
    } else {
      ((ArrayNode) innerNode).set(i, data);
    }
  }

  private JsonNode processQueryLink(JsonNode node, ArrayNode paramArray, int objectNodeNumber)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    return UtilNativeQuery.mapper.valueToTree(nativeQueryDAO.processQueryObject(
        CustomQueryListDAO.queryList.get(node.get(CustomQueryListDAO.LINK_QUERY).asText()),
        (ArrayNode) paramArray.get(objectNodeNumber)));
  }

  private JsonNode processQueryNode(JsonNode node, ArrayNode paramArray, int objectNodeNumber)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    return UtilNativeQuery.mapper.valueToTree(
        nativeQueryDAO.processQueryObject(node, (ArrayNode) paramArray.get(objectNodeNumber)));
  }
}
