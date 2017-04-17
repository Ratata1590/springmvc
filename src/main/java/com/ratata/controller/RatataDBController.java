package com.ratata.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.Util.RDataType;
import com.ratata.Util.UtilNativeQuery;
import com.ratata.model.RArray;
import com.ratata.model.RArrayItems;
import com.ratata.model.RNumber;
import com.ratata.model.RObject;
import com.ratata.model.RObjectKey;
import com.ratata.model.RString;
import com.ratata.pojo.NodeInfo;
import com.ratata.repoRatataDB.RArrayItemsRepo;
import com.ratata.repoRatataDB.RArrayRepo;
import com.ratata.repoRatataDB.RNumberRepo;
import com.ratata.repoRatataDB.RObjectKeyRepo;
import com.ratata.repoRatataDB.RObjectRepo;
import com.ratata.repoRatataDB.RStringRepo;

@RestController
public class RatataDBController {

  @Autowired
  private RObjectRepo rObjectRepo;
  @Autowired
  private RArrayItemsRepo rArrayItemsRepo;
  @Autowired
  private RArrayRepo rArrayRepo;
  @Autowired
  private RNumberRepo rNumberRepo;
  @Autowired
  private RObjectKeyRepo rObjectKeyRepo;
  @Autowired
  private RStringRepo rStringRepo;

  @RequestMapping(value = "/Rsave", method = RequestMethod.POST)
  public Object insertNode(@RequestBody JsonNode object) throws Exception {
    resolveValueNode(object, new NodeInfo());

    Map<String, Object> db = new HashMap<String, Object>();
    db.put("RObject", rObjectRepo.findAll());
    db.put("RObjectKey", rObjectKeyRepo.findAll());
    db.put("RArray", rArrayRepo.findAll());
    db.put("RArrayItems", rArrayItemsRepo.findAll());
    db.put("RNumber", rNumberRepo.findAll());
    db.put("RString", rStringRepo.findAll());
    return db;
  }

  @RequestMapping(value = "/test", method = RequestMethod.GET)
  public Object insertNode() {


    JsonNode sequenceMapString = UtilNativeQuery.mapper.valueToTree("{\"0\":[\"long\",1234]}");
    RObject rObject = rObjectRepo.save(new RObject());

    RObjectKey k1 = new RObjectKey();
    RObjectKey k2 = new RObjectKey();
    RObjectKey k3 = new RObjectKey();

    Set<RObjectKey> rObjectKey = new HashSet<RObjectKey>();
    rObjectKey.add(k1);
    rObjectKey.add(k2);
    rObjectKey.add(k3);
    k1.setObject(rObject);
    k2.setObject(rObject);
    k3.setObject(rObject);

    rObject.setrObjectKey(rObjectKey);

    rObject = rObjectRepo.save(rObject);
    // rObject.getrObjectKey().add(k1);
    RObjectKey rObjectKeyt = rObject.getrObjectKey().iterator().next();

    rObjectKeyt.setObject(null);
    rObject.getrObjectKey().remove(rObjectKeyt);
    // rObject.getrObjectKey().add(k3);
    // rObjectKey.add(k1);
    // rObjectKey.add(k2);
    // rObjectKey.add(k3);
    System.out.println("done");

    rObjectRepo.save(rObject);
    rObject.getrObjectKey().add(new RObjectKey());
    rObjectRepo.save(rObject);

    return null;
  }

  private void resolveValueNode(JsonNode node, NodeInfo nodeInfo) throws Exception {
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
      if (node.has("id")) {
        rObject = rObjectRepo.findOne(node.get("id").asLong());
      }
      if (rObject == null) {
        rObject = new RObject();
        rObject = rObjectRepo.save(rObject);
      }
      nodeInfo.setChildId(rObject.getId());
      nodeInfo.setChildType(RDataType.ROBJECT);

      Set<String> objectkeys = rObjectKeyRepo.getAllKeyName(rObject.getId());
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
      if (!node.get(0).isNull()) {
        rArray = rArrayRepo.findOne(node.get(0).asLong());
      }
      if (rArray == null) {
        rArray = new RArray();
        rArray.setSequenceMap(new LinkedList<RArrayItems>());
        rArray = rArrayRepo.save(rArray);
      }
      nodeInfo.setChildId(rArray.getId());
      nodeInfo.setChildType(RDataType.RARRAY);

      LinkedList<Long> sequenceMap = rArray.getSequenceMapAsListLong();

      for (int i = 1; i < node.size(); i++) {
        RArrayItems rArrayItems;
        NodeInfo nodeInfoIn = new NodeInfo();
        resolveValueNode(node.get(i), nodeInfoIn);

        if (sequenceMap.size() >= node.size() - 1
            && sequenceMap.get(i - 1).equals(nodeInfoIn.getChildId())) {
          continue;
        } else {
          rArrayItems = new RArrayItems();
          rArrayItems.setRarray(rArray);
          rArrayItems.setChildId(nodeInfoIn.getChildId());
          rArrayItems.setChildType(nodeInfoIn.getChildType());
          rArrayItems = rArrayItemsRepo.save(rArrayItems);
          if (i >= sequenceMap.size()) {
            sequenceMap.add(rArrayItems.getId());
          } else {
            sequenceMap.set(i - 1, rArrayItems.getId());
          }
        }
      }
      // rArray.setSequenceMap(rArrayRepo.fi);
    }
  }
}
