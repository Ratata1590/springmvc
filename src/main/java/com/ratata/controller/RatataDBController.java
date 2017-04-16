package com.ratata.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ratata.Util.RDataType;
import com.ratata.Util.UtilNativeQuery;
import com.ratata.model.RArray;
import com.ratata.model.RArrayItems;
import com.ratata.model.RNumber;
import com.ratata.model.RObject;
import com.ratata.model.RObjectKey;
import com.ratata.model.RString;
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
	public Object insertNode(@RequestBody JsonNode object, @RequestHeader(defaultValue = "") String tableName) {
		// scanNode(object);
		// return ratataRepo.insertData();
		return null;
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

	private void scanNode(JsonNode node, Object parent, Boolean isArray) throws Exception {

		if (node.isArray()) {
			RArray rArray;
			if (!node.has("id")) {
				rArray = rArrayRepo.save(new RArray());
			}
			rArray = rArrayRepo.findOne(node.get("id").asLong());
			if (rArray == null) {
				rArray = rArrayRepo.save(new RArray());
			}

			//ArrayNode arraynode = rArray.getSequenceMap();

			// rArray.getrArrayItems()
		}
		if (node.isObject()) {
			RObject rObject;
			if (!node.has("id")) {
				rObject = new RObject();
			} else {
				rObject = rObjectRepo.findOne(node.get("id").asLong());
			}
			if (rObject == null) {
				rObject = new RObject();
			}

			Set<String> objectkeys = rObjectKeyRepo.getAllKeyName();
			Iterator<Entry<String, JsonNode>> iter = node.fields();
			while (iter.hasNext()) {
				Entry<String, JsonNode> property = (Entry<String, JsonNode>) iter.next();

				if (objectkeys.contains(property.getKey())) {
					RObjectKey rObjectKey = rObjectKeyRepo.findbyValue(rObject.getId(), property.getKey());
					Long childId = null;
					Integer childType = null;
					resolveValueNode(property.getValue(), childId, childType);
					rObjectKey.setChildId(childId);
				}
				if (property.getValue().isValueNode()) {
					processValueNodeParent(property.getValue(), rObject, false, property.getKey());
					continue;
				} else {
					RObjectKey rObjectKey = rObjectKeyRepo.findbyValue(rObject.getId(), property.getKey());
					if (rObjectKey == null) {
						rObjectKey = new RObjectKey();
						rObjectKey.setObject(rObject);
						rObjectKey.setKeyName(property.getKey());
					}
					// if (rObjectKey.getChildId().equals(rObject.getId())
					// && rObjectKey.getChildType().equals(childType)) {
					// rObjectKey.setChildId(childId);
					// rObjectKey.setChildType(childType);
					// }
				}
				scanNode(property.getValue(), rObject, false);
			}

		}
	}

	private void processValueNodeParent(JsonNode node, Object parent, Boolean isArray, Object key) throws Exception {
		Long childId = null;
		Integer childType = null;
		resolveValueNode(node, childId, childType);
		if (isArray) {
			RArrayItems rArrayItems = rArrayItemsRepo.findbyValue(((RArray) parent).getId(), childType, childId);
			if (rArrayItems == null) {
				rArrayItems = new RArrayItems();
			}
			return;
		} else {
			RObjectKey rObjectKey = rObjectKeyRepo.findbyValue(((RObject) parent).getId(), (String) key);
			if (rObjectKey == null) {
				rObjectKey = new RObjectKey();
				rObjectKey.setObject((RObject) parent);
				rObjectKey.setKeyName((String) key);
			}
			if (rObjectKey.getChildId().equals(childId) && rObjectKey.getChildType().equals(childType)) {
				rObjectKey.setChildId(childId);
				rObjectKey.setChildType(childType);
			}
			rObjectKeyRepo.save(rObjectKey);
		}
	}

	private void resolveValueNode(JsonNode node, Long childId, Integer childType) throws Exception {
		if (node.isNumber()) {
			RNumber rNumber = rNumberRepo.findbyValue(node.asDouble());
			if (rNumber == null) {
				rNumber = new RNumber();
				rNumber.setData(node.asDouble());
				rNumber = rNumberRepo.save(rNumber);
			}
			childId = rNumber.getId();
			childType = RDataType.RNUMBER;
		}
		if (node.isTextual()) {
			RString rString = rStringRepo.findbyValue(node.asText());
			if (rString == null) {
				rString = new RString();
				rString.setData(node.asText());
				rString = rStringRepo.save(rString);
			}
			childId = rString.getId();
			childType = RDataType.RSTRING;
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
			childId = rObject.getId();
			childType = RDataType.ROBJECT;

			Set<String> objectkeys = rObjectKeyRepo.getAllKeyName();
			Iterator<Entry<String, JsonNode>> iter = node.fields();
			while (iter.hasNext()) {
				Entry<String, JsonNode> property = (Entry<String, JsonNode>) iter.next();
				RObjectKey rObjectKey;
				if (objectkeys.contains(property.getKey())) {
					rObjectKey = rObjectKeyRepo.findbyValue(rObject.getId(), property.getKey());
				} else {
					rObjectKey = new RObjectKey();
				}

				Long childIdin = null;
				Integer childTypein = null;
				resolveValueNode(property.getValue(), childIdin, childTypein);
				rObjectKey.setChildId(childIdin);
				rObjectKey.setChildType(childTypein);
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
				//rArray.setSequenceMap(new LinkedList<Long>());
				rArray = rArrayRepo.save(new RArray());
			}
			childId = rArray.getId();
			childType = RDataType.RARRAY;

			LinkedList<Long> sequenceMap = rArray.getSequenceMap();

			for (int i = 1; i < node.size(); i++) {
				RArrayItems rArrayItems;
				
				
				if (sequenceMap.get(i - 1).equals(node.get(i))) {
					rArrayItems = rObjectKeyRepo.findbyValue(rArrayItems.getId(), property.getKey());
				} else {
					rArrayItems = new RArrayItems();
				}
				
				
				Long childIdin = null;
				Integer childTypein = null;
				resolveValueNode(node.get(i), childIdin, childTypein);
				
			}
//			while (iter.hasNext()) {
//				Entry<String, JsonNode> property = (Entry<String, JsonNode>) iter.next();
//				RObjectKey rObjectKey;
//				if (objectkeys.contains(property.getKey())) {
//					rObjectKey = rObjectKeyRepo.findbyValue(rObject.getId(), property.getKey());
//				} else {
//					rObjectKey = new RObjectKey();
//				}
//
//				Long childIdin = null;
//				Integer childTypein = null;
//				resolveValueNode(property.getValue(), childIdin, childTypein);
//				rObjectKey.setChildId(childIdin);
//				rObjectKey.setChildType(childTypein);
//				rObjectKeyRepo.save(rObjectKey);
			}
		}
}
