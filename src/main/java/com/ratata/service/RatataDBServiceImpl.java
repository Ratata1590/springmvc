package com.ratata.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.RDataType;
import com.ratata.Util.UtilNativeQuery;
import com.ratata.model.RArray;
import com.ratata.model.RArrayItems;
import com.ratata.model.RNumber;
import com.ratata.model.RObject;
import com.ratata.model.RObjectKey;
import com.ratata.model.RString;
import com.ratata.pojo.NodeInfo;
import com.ratata.repoRatataDB.RArrayRepo;
import com.ratata.repoRatataDB.RNumberRepo;
import com.ratata.repoRatataDB.RObjectKeyRepo;
import com.ratata.repoRatataDB.RObjectRepo;
import com.ratata.repoRatataDB.RStringRepo;

@Component
public class RatataDBServiceImpl implements RatataDBService {

  @Autowired
  private RObjectRepo rObjectRepo;
  @Autowired
  private RArrayRepo rArrayRepo;
  @Autowired
  private RNumberRepo rNumberRepo;
  @Autowired
  private RObjectKeyRepo rObjectKeyRepo;
  @Autowired
  private RStringRepo rStringRepo;

  @Override
  public Long saveNode(JsonNode object) throws Exception {
    NodeInfo nodeInfo = new NodeInfo();
    resolveValueNode(object, nodeInfo);
    return nodeInfo.getChildId();
  }

  @Override
  public Object getNode(Long id, Integer type, Boolean showId, Boolean showData) throws Exception {
    JsonNode jsonNode = UtilNativeQuery.mapper.createArrayNode();
    treeNode(jsonNode, id, type, null, showId, showData);
    return jsonNode.get(0);
  }

  private void treeNode(JsonNode jsonNode, Long id, Integer type, String key, Boolean showId,
      Boolean showData) {
    switch (type.intValue()) {
      case RDataType.RTRUE:
        if (jsonNode.isObject()) {
          ((ObjectNode) jsonNode).put(key, true);
        } else {
          ((ArrayNode) jsonNode).add(true);
        }
        break;
      case RDataType.RFALSE:
        if (jsonNode.isObject()) {
          ((ObjectNode) jsonNode).put(key, false);
        } else {
          ((ArrayNode) jsonNode).add(false);
        }
        break;
      case RDataType.RNULL:
        if (jsonNode.isObject()) {
          ((ObjectNode) jsonNode).putNull(key);
        } else {
          ((ArrayNode) jsonNode).add(NullNode.getInstance());
        }
        break;
      case RDataType.RSTRING:
        if (!showData) {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, "String");
          } else {
            ((ArrayNode) jsonNode).add("String");
          }
        } else {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, rStringRepo.findOne(id).getData());
          } else {
            ((ArrayNode) jsonNode).add(rStringRepo.findOne(id).getData());
          }
        }
        break;
      case RDataType.RNUMBER:
        if (!showData) {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, "Number");
          } else {
            ((ArrayNode) jsonNode).add("Number");
          }
        } else {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, rNumberRepo.findOne(id).getData());
          } else {
            ((ArrayNode) jsonNode).add(rNumberRepo.findOne(id).getData());
          }
        }
        break;
      case RDataType.RARRAY:
        RArray rarray = rArrayRepo.findOne(id);
        if (rarray == null) {
          return;
        }
        ArrayNode arrayNodein = UtilNativeQuery.mapper.createArrayNode();
        if (showId) {
          arrayNodein.add(rarray.getId());
        }
        for (RArrayItems item : rarray.getrArrayItems()) {
          treeNode(arrayNodein, item.getChildId(), item.getChildType(), null, showId, showData);
        }

        if (jsonNode.isObject()) {
          ((ObjectNode) jsonNode).set(key, arrayNodein);
        } else {
          ((ArrayNode) jsonNode).add(arrayNodein);
        }
        break;
      case RDataType.ROBJECT:
        RObject robject = rObjectRepo.findOne(id);
        if (robject == null) {
          return;
        }
        ObjectNode objectNodein = UtilNativeQuery.mapper.createObjectNode();
        if (showId) {
          objectNodein.put("id", robject.getId());
        }
        Iterator<RObjectKey> iter = robject.getrObjectKey().iterator();
        while (iter.hasNext()) {
          RObjectKey robjkey = iter.next();
          treeNode(objectNodein, robjkey.getChildId(), robjkey.getChildType(), robjkey.getKeyName(),
              showId, showData);
        }
        if (jsonNode.isObject()) {
          ((ObjectNode) jsonNode).set(key, objectNodein);
        } else {
          ((ArrayNode) jsonNode).add(objectNodein);
        }
        break;
    }
  }

  private void resolveValueNode(JsonNode node, NodeInfo nodeInfo) throws Exception {
    if (node.isNull()) {
      nodeInfo.setChildType(RDataType.RNULL);
    }
    if (node.isBoolean()) {
      if (node.asBoolean()) {
        nodeInfo.setChildType(RDataType.RTRUE);
      } else {
        nodeInfo.setChildType(RDataType.RFALSE);
      }
    }
    if (node.isNumber()) {
      RNumber rNumber = rNumberRepo.findbyValue(node.asDouble());
      if (rNumber == null) {
        rNumber = new RNumber();
        rNumber.setData(node.asDouble());
        rNumber = rNumberRepo.save(rNumber);
      }
      nodeInfo.setChildId(rNumber.getId());
      nodeInfo.setChildType(RDataType.RNUMBER);
    }
    if (node.isTextual()) {
      RString rString = rStringRepo.findbyValue(node.asText());
      if (rString == null) {
        rString = new RString();
        rString.setData(node.asText());
        rString = rStringRepo.save(rString);
      }
      nodeInfo.setChildId(rString.getId());
      nodeInfo.setChildType(RDataType.RSTRING);
    }
    if (node.isObject()) {
      RObject rObject = null;
      if (node.has("id") && node.get("id").isNumber()) {
        rObject = rObjectRepo.findOne(node.get("id").asLong());
      }
      if (rObject == null) {
        rObject = new RObject();
        rObject = rObjectRepo.save(rObject);
      }
      nodeInfo.setChildId(rObject.getId());
      nodeInfo.setChildType(RDataType.ROBJECT);
      Set<String> objectkeys = rObjectKeyRepo.getAllKeyName(rObject.getId());
      ((ObjectNode) node).remove("id");
      Iterator<Entry<String, JsonNode>> iter = node.fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> property = (Entry<String, JsonNode>) iter.next();
        RObjectKey rObjectKey;
        if (objectkeys.contains(property.getKey())) {
          rObjectKey = rObjectKeyRepo.findbyValue(rObject.getId(), property.getKey());
        } else {
          rObjectKey = new RObjectKey();
          rObjectKey.setKeyName(property.getKey());
          rObjectKey.setObject(rObject);
        }
        NodeInfo nodeInfoIn = new NodeInfo();
        resolveValueNode(property.getValue(), nodeInfoIn);
        rObjectKey.setChildId(nodeInfoIn.getChildId());
        rObjectKey.setChildType(nodeInfoIn.getChildType());
        rObjectKeyRepo.save(rObjectKey);
      }
    }
    if (node.isArray()) {
      RArray rArray = null;
      Boolean hasNodeId = false;
      if (!node.get(0).isNull() && node.get(0).isNumber()) {
        hasNodeId = true;
        rArray = rArrayRepo.findOne(node.get(0).asLong());
      }
      if (rArray == null) {
        hasNodeId = false;
        rArray = new RArray();
        rArray = rArrayRepo.save(rArray);
      }
      nodeInfo.setChildId(rArray.getId());
      nodeInfo.setChildType(RDataType.RARRAY);
      if (rArray.getrArrayItems() == null) {
        rArray.setrArrayItems(new ArrayList<RArrayItems>());
      }
      ((ArrayNode) node).remove(0);
      for (int i = 0; i < node.size(); i++) {
        NodeInfo nodeInfoIn = new NodeInfo();
        resolveValueNode(node.get(i), nodeInfoIn);
        RArrayItems rArrayItems = null;
        if (!hasNodeId) {
          rArrayItems = new RArrayItems();
          rArrayItems.setRarray(rArray);
          rArrayItems.setChildId(nodeInfoIn.getChildId());
          rArrayItems.setChildType(nodeInfoIn.getChildType());
          rArray.getrArrayItems().add(rArrayItems);
        } else {
          if (i < rArray.getrArrayItems().size()) {
            rArray.getrArrayItems().get(i).setChildId(nodeInfoIn.getChildId());
            rArray.getrArrayItems().get(i).setChildType(nodeInfoIn.getChildType());
          } else {
            rArrayItems = new RArrayItems();
            rArrayItems.setRarray(rArray);
            rArrayItems.setChildId(nodeInfoIn.getChildId());
            rArrayItems.setChildType(nodeInfoIn.getChildType());
            rArray.getrArrayItems().add(rArrayItems);
          }
        }
      }
      rArray = rArrayRepo.save(rArray);
    }
  }
}
