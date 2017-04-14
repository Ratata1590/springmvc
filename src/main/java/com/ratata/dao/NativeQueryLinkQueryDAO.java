package com.ratata.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.Const;
import com.ratata.Util.UtilNativeQuery;

@Component
public class NativeQueryLinkQueryDAO {

	@Autowired
	private NativeQueryDynamicPojoDAO nativeQueryDynamicPojoDAO;

	@Autowired
	private NativeQueryDAO nativeQueryDAO;

	public static Map<String, JsonNode> queryList = new HashMap<String, JsonNode>();
	private TypeReference<HashMap<String, JsonNode>> typeRef = new TypeReference<HashMap<String, JsonNode>>() {
	};

	public void saveQueryListFromFile() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(Const.LINK_QUERY_INITFILENAME).getFile());
		saveQueryList(UtilNativeQuery.mapper.readTree(file));
	}

	public void saveQueryList(Object query) {
		queryList = UtilNativeQuery.mapper.convertValue(query, typeRef);
	}

	public void updateQueryList(ObjectNode query) {
		Iterator<Entry<String, JsonNode>> nodeEntry = query.fields();
		while (nodeEntry.hasNext()) {
			Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeEntry.next();
			if (entry.getValue().isNull() && queryList.containsKey(entry.getKey())) {
				NativeQueryLinkQueryDAO.queryList.remove(entry.getKey());
				continue;
			}
			NativeQueryLinkQueryDAO.queryList.put(entry.getKey(), entry.getValue());
		}
	}

	public Object processCustomQuery(String queryName, String param) throws Exception {
		if (NativeQueryLinkQueryDAO.queryList.isEmpty()) {
			return Const.LINK_QUERY_QUERYLISTEMPTY;
		}
		ArrayNode paramNode = ((ArrayNode) UtilNativeQuery.mapper.readTree(param));
		return processCustomQuery(queryName, paramNode);
	}

	public Object processCustomQuery(String queryName, ArrayNode param) throws Exception {
		JsonNode queryObject = NativeQueryLinkQueryDAO.queryList.get(queryName);
		if (queryObject == null) {
			return Const.LINK_QUERY_QUERYNOTEXIST;
		}
		if (queryObject.has(Const.PARAM_QUERY)) {
			return nativeQueryDAO.processQueryObject(queryObject, param);
		}
		return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo((ObjectNode) queryObject.deepCopy(), param);
	}
}
