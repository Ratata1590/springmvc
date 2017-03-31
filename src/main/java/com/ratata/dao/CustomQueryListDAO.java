package com.ratata.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public class CustomQueryListDAO {

	@Autowired
	NativeQueryDAO nativeQueryDAO;

	public static Map<String, JsonNode> queryList = new HashMap<String, JsonNode>();
	ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public void saveQueryList(ObjectNode query) {
		queryList = mapper.convertValue(query, Map.class);
	}

	public void updateQueryList(ObjectNode query) {
		Iterator<Entry<String, JsonNode>> nodeEntry = query.fields();
		while (nodeEntry.hasNext()) {
			Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeEntry.next();
			if (entry.getValue().isNull()) {
				if (queryList.containsKey(entry.getKey())) {
					CustomQueryListDAO.queryList.remove(entry.getKey());
				}
				continue;
			}
			CustomQueryListDAO.queryList.put(entry.getKey(), entry.getValue());
		}
	}

	public Object processCustomQuery(String queryName, List<String> param)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		JsonNode queryObject = CustomQueryListDAO.queryList.get(queryName);
		String query = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_QUERY)
				? queryObject.get(NativeQueryDynamicPojoDAO.PARAM_QUERY).asText() : null;
		String className = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_CLASSNAME)
				? queryObject.get(NativeQueryDynamicPojoDAO.PARAM_CLASSNAME).asText() : null;
		List<String> resultSet = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_RESULTSET) ? UtilNativeQuery
				.arrayNodeToListString((ArrayNode) queryObject.get(NativeQueryDynamicPojoDAO.PARAM_RESULTSET)) : null;
		boolean singleReturn = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_SINGLERETURN)
				? queryObject.get(NativeQueryDynamicPojoDAO.PARAM_SINGLERETURN).asBoolean() : false;
		return nativeQueryDAO.nativeQuery(query, className, resultSet, singleReturn, param);
	}
}
