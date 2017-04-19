package com.ratata.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.RDataType;
import com.ratata.Util.RatataDBConst;
import com.ratata.Util.UtilNativeQuery;
import com.ratata.model.RArray;
import com.ratata.model.RArrayItems;
import com.ratata.model.RBinary;
import com.ratata.model.RNumber;
import com.ratata.model.RObject;
import com.ratata.model.RObjectKey;
import com.ratata.model.RString;
import com.ratata.pojo.NodeInfo;
import com.ratata.repoRatataDB.RArrayRepo;
import com.ratata.repoRatataDB.RBinaryRepo;
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
  @Autowired
  private RBinaryRepo rBinaryRepo;

  @Override
  public Long saveNode(JsonNode object) throws Exception {
    NodeInfo nodeInfo = new NodeInfo();
    resolveValueNode(object, nodeInfo);
    return nodeInfo.getChildId();
  }

  @Override
  public JsonNode getNode(Long id, Integer type, Boolean showId, Boolean showData,
      Boolean showBinary) throws Exception {
    JsonNode jsonNode = UtilNativeQuery.mapper.createArrayNode();
    treeNode(jsonNode, id, type, null, showId, showData, showBinary);
    return jsonNode.get(0);
  }


  @Override
  public Object uploadFile(MultipartFile[] files, String[] names) throws Exception {
    if (files.length != names.length) {
      return RatataDBConst.UPLOADFILE_ERROR;
    }
    ObjectNode result = UtilNativeQuery.mapper.createObjectNode();
    for (int i = 0; i < files.length; i++) {
      if (files[i].isEmpty() || names[i].isEmpty()) {
        continue;
      }
      ObjectNode fileObj = UtilNativeQuery.mapper.createObjectNode();
      fileObj.put(names[i], files[i].getBytes());
      NodeInfo nodeInfo = new NodeInfo();
      resolveValueNode(fileObj, nodeInfo);
      result.put(names[i], nodeInfo.getChildId());
    }
    return result;
  }

  @Override
  public void downloadFileById(HttpServletResponse response, Long id) throws Exception {
    response.getOutputStream().write(rBinaryRepo.findOne(id).getData());
  }

  @Override
  public void downloadFileByHash(HttpServletResponse response, String hash) throws Exception {
    response.getOutputStream().write(rBinaryRepo.findbyhashMD5(hash).getData());
  }

  private void treeNode(JsonNode jsonNode, Long id, Integer type, String key, Boolean showId,
      Boolean showData, Boolean showBinary) {
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
      case RDataType.RBINARY:
        if ((!showData)) {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, RatataDBConst.PLACE_HOLDER_BINARY);
          } else {
            ((ArrayNode) jsonNode).add(RatataDBConst.PLACE_HOLDER_BINARY);
          }
          break;
        }
        if (!showBinary) {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, rBinaryRepo.findOne(id).getHashMD5());
          } else {
            ((ArrayNode) jsonNode).add(rBinaryRepo.findOne(id).getHashMD5());
          }
        } else {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, rBinaryRepo.findOne(id).getData());
          } else {
            ((ArrayNode) jsonNode).add(rBinaryRepo.findOne(id).getData());
          }
        }
        break;
      case RDataType.RSTRING:
        if (!showData) {
          if (jsonNode.isObject()) {
            ((ObjectNode) jsonNode).put(key, RatataDBConst.PLACE_HOLDER_STRING);
          } else {
            ((ArrayNode) jsonNode).add(RatataDBConst.PLACE_HOLDER_STRING);
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
            ((ObjectNode) jsonNode).put(key, RatataDBConst.PLACE_HOLDER_NUMBER);
          } else {
            ((ArrayNode) jsonNode).add(RatataDBConst.PLACE_HOLDER_NUMBER);
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
          treeNode(arrayNodein, item.getChildId(), item.getChildType(), null, showId, showData,
              showBinary);
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
          objectNodein.put(RatataDBConst.RATATA_ID_OBJECT_KEYNAME, robject.getId());
        }
        Iterator<RObjectKey> iter = robject.getrObjectKey().iterator();
        while (iter.hasNext()) {
          RObjectKey robjkey = iter.next();
          treeNode(objectNodein, robjkey.getChildId(), robjkey.getChildType(), robjkey.getKeyName(),
              showId, showData, showBinary);
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
    switch (node.getNodeType()) {
      case NULL:
        nodeInfo.setChildType(RDataType.RNULL);
        break;
      case BOOLEAN:
        if (node.asBoolean()) {
          nodeInfo.setChildType(RDataType.RTRUE);
        } else {
          nodeInfo.setChildType(RDataType.RFALSE);
        }
        break;
      case BINARY:
        byte[] data = node.binaryValue();
        String hashMD5 = DigestUtils.md5DigestAsHex(data);
        RBinary rbin = rBinaryRepo.findbyhashMD5(hashMD5);
        if (rbin == null) {
          rbin = new RBinary();
          rbin.setHashMD5(hashMD5);
          rbin.setData(data);
          rbin = rBinaryRepo.save(rbin);
        }
        nodeInfo.setChildId(rbin.getId());
        nodeInfo.setChildType(RDataType.RBINARY);
        break;
      case NUMBER:
        RNumber rNumber = rNumberRepo.findbyValue(node.asDouble());
        if (rNumber == null) {
          rNumber = new RNumber();
          rNumber.setData(node.asDouble());
          rNumber = rNumberRepo.save(rNumber);
        }
        nodeInfo.setChildId(rNumber.getId());
        nodeInfo.setChildType(RDataType.RNUMBER);
        break;
      case STRING:
        RString rString = rStringRepo.findbyValue(node.asText());
        if (rString == null) {
          rString = new RString();
          rString.setData(node.asText());
          rString = rStringRepo.save(rString);
        }
        nodeInfo.setChildId(rString.getId());
        nodeInfo.setChildType(RDataType.RSTRING);
        break;
      case OBJECT:
        RObject rObject = null;
        if (node.has(RatataDBConst.RATATA_ID_OBJECT_KEYNAME)
            && node.get(RatataDBConst.RATATA_ID_OBJECT_KEYNAME).isNumber()) {
          rObject = rObjectRepo.findOne(node.get(RatataDBConst.RATATA_ID_OBJECT_KEYNAME).asLong());
        }
        if (rObject == null) {
          rObject = new RObject();
          rObject = rObjectRepo.save(rObject);
        }
        nodeInfo.setChildId(rObject.getId());
        nodeInfo.setChildType(RDataType.ROBJECT);
        Set<String> objectkeys = rObjectKeyRepo.getAllKeyName(rObject.getId());
        ((ObjectNode) node).remove(RatataDBConst.RATATA_ID_OBJECT_KEYNAME);
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
        break;
      case ARRAY:
        RArray rArray = null;
        Boolean hasNodeId = false;
        if (!node.get(RatataDBConst.RATATA_ID_ARRAY_LOCATION).isNull()
            && node.get(RatataDBConst.RATATA_ID_ARRAY_LOCATION).isNumber()) {
          hasNodeId = true;
          rArray = rArrayRepo.findOne(node.get(RatataDBConst.RATATA_ID_ARRAY_LOCATION).asLong());
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
        ((ArrayNode) node).remove(RatataDBConst.RATATA_ID_ARRAY_LOCATION);
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
        break;
      default:
        break;
    }
  }
}
