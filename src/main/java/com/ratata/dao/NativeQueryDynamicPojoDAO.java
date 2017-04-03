package com.ratata.dao;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	NativeQueryDAO nativeQueryDAO;

	public Object nativeWithDynamicPojo(JsonNode node, String param)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		if (node.has(PARAM_QUERY)) {
			return nativeQueryDAO.processQueryObject(node, param);
		}
		processDynamicPojo(node, param);
		return node;
	}

	public void processDynamicPojo(JsonNode node, String param)
			throws JsonProcessingException, IOException, ClassNotFoundException {
		ArrayNode paramArray = (ArrayNode) UtilNativeQuery.mapper.readTree(param);
		int objectNodeNumber = 0;
		checkNode(node, paramArray, objectNodeNumber);

	}

	@SuppressWarnings("unchecked")
	public void checkNode(JsonNode node, ArrayNode paramArray, int objectNodeNumber)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		Iterator<Entry<String, JsonNode>> nodeEntry = node.fields();
		while (nodeEntry.hasNext()) {
			Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeEntry.next();
			switch (entry.getValue().getNodeType()) {
			case ARRAY:
				for (int i = 0; i < entry.getValue().size(); i++) {
					JsonNode tnode = entry.getValue().get(i);
					if (tnode.has(CustomQueryListDAO.LINK_QUERY)) {
						if (tnode.has(PARAM_MERGEARRAY) && tnode.get(PARAM_MERGEARRAY).asBoolean()) {
							((ArrayNode) entry.getValue()).remove(i);
							List<Object> results = (List<Object>) nativeQueryDAO.processQueryObject(
									CustomQueryListDAO.queryList.get(tnode.get(CustomQueryListDAO.LINK_QUERY).asText()),
									UtilNativeQuery.mapper.writeValueAsString(paramArray.get(objectNodeNumber)));
							for (Object obj : results) {
								((ArrayNode) entry.getValue()).add(UtilNativeQuery.mapper.valueToTree(obj));
							}
						} else {
							((ArrayNode) entry.getValue()).set(i,
									UtilNativeQuery.mapper.valueToTree(nativeQueryDAO.processQueryObject(
											CustomQueryListDAO.queryList.get(tnode.get(CustomQueryListDAO.LINK_QUERY).asText()),
											UtilNativeQuery.mapper
													.writeValueAsString(paramArray.get(objectNodeNumber)))));
						}
						objectNodeNumber++;
						continue;
					}
					if (tnode.has(PARAM_QUERY)) {
						if (tnode.has(PARAM_MERGEARRAY) && tnode.get(PARAM_MERGEARRAY).asBoolean()) {
							((ArrayNode) entry.getValue()).remove(i);
							List<Object> results = (List<Object>) nativeQueryDAO.processQueryObject(tnode,
									UtilNativeQuery.mapper.writeValueAsString(paramArray.get(objectNodeNumber)));
							for (Object obj : results) {
								((ArrayNode) entry.getValue()).add(UtilNativeQuery.mapper.valueToTree(obj));
							}
						} else {
							((ArrayNode) entry.getValue()).set(i,
									UtilNativeQuery.mapper
											.valueToTree(nativeQueryDAO.processQueryObject(tnode, UtilNativeQuery.mapper
													.writeValueAsString(paramArray.get(objectNodeNumber)))));
						}
						objectNodeNumber++;
						continue;
					}
					checkNode(tnode, paramArray, objectNodeNumber);
				}
				break;
			case OBJECT:
				if (entry.getValue().has(CustomQueryListDAO.LINK_QUERY)) {
					((ObjectNode) node).replace(entry.getKey(),
							UtilNativeQuery.mapper.valueToTree(nativeQueryDAO.processQueryObject(
									CustomQueryListDAO.queryList
											.get(entry.getValue().get(CustomQueryListDAO.LINK_QUERY).asText()),
									UtilNativeQuery.mapper.writeValueAsString(paramArray.get(objectNodeNumber)))));
					objectNodeNumber++;
				}
				if (entry.getValue().has(PARAM_QUERY)) {
					((ObjectNode) node).replace(entry.getKey(),
							UtilNativeQuery.mapper.valueToTree(nativeQueryDAO.processQueryObject(entry.getValue(),
									UtilNativeQuery.mapper.writeValueAsString(paramArray.get(objectNodeNumber)))));
					objectNodeNumber++;
				} else {
					checkNode(entry.getValue(), paramArray, objectNodeNumber);
				}
			default:
				break;
			}
		}
	}

}
