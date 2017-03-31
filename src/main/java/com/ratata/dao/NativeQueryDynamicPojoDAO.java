package com.ratata.dao;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class NativeQueryDynamicPojoDAO {
	public static final String PARAM_QUERY = "query";
	public static final String PARAM_CLASSNAME = "className";
	public static final String PARAM_RESULTSET = "resultSet";
	public static final String PARAM_SINGLERETURN = "singleReturn";

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	NativeQueryDAO nativeQueryDAO;

	public Object nativeWithDynamicPojo(ObjectNode node)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		if (node.has(PARAM_QUERY)) {
			return processQuery(node);
		}
		checkNode(node);
		return node;
	}

	public void checkNode(JsonNode node) throws ClassNotFoundException, JsonProcessingException, IOException {
		Iterator<Entry<String, JsonNode>> nodeEntry = node.fields();
		while (nodeEntry.hasNext()) {
			Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeEntry.next();
			switch (entry.getValue().getNodeType()) {
			case ARRAY:
				for (int i = 0; i < entry.getValue().size(); i++) {
					JsonNode tnode = entry.getValue().get(i);
					if (tnode.has(PARAM_QUERY)) {
						((ArrayNode) entry.getValue()).set(i, mapper.valueToTree(processQuery(tnode)));
					} else {
						checkNode(tnode);
					}
				}
				break;
			case OBJECT:
				if (entry.getValue().has(PARAM_QUERY)) {
					((ObjectNode) node).replace(entry.getKey(), mapper.valueToTree(processQuery(entry.getValue())));
				} else {
					checkNode(entry.getValue());
				}
			default:
				break;
			}
		}
	}

	private Object processQuery(JsonNode node) throws ClassNotFoundException, JsonProcessingException, IOException {
		boolean singleReturn = false;

		if (node.has(PARAM_SINGLERETURN)) {
			singleReturn = node.get(PARAM_SINGLERETURN).asBoolean();
		}
		if (node.has(PARAM_RESULTSET)) {
			return nativeQueryDAO.nativeQuery(node.get(PARAM_QUERY).asText(), null,
					UtilNativeQuery.arrayNodeToListString((ArrayNode) node.get(PARAM_RESULTSET)), singleReturn, null);
		}
		if (node.has(PARAM_CLASSNAME)) {
			return nativeQueryDAO.nativeQuery(node.get(PARAM_QUERY).asText(), node.get(PARAM_CLASSNAME).asText(), null,
					singleReturn, null);
		}
		return nativeQueryDAO.nativeQuery(node.get(PARAM_QUERY).asText(), null, null, singleReturn, null);
	}

}
