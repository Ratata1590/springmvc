package com.ratata.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.Const;
import com.ratata.Util.UtilNativeQuery;

@Component
public class NativeQueryLinkQueryDAOImpl implements NativeQueryLinkQueryDAO {

	@Autowired
	private NativeQueryDynamicPojoDAO nativeQueryDynamicPojoDAO;

	@Autowired
	private NativeQueryDAO nativeQueryDAO;

	public Map<String, JsonNode> queryList = new HashMap<String, JsonNode>();

	public Map<String, JsonNode> getQueryList() {
		return queryList;
	}

	public void setQueryList(Map<String, JsonNode> queryList) {
		this.queryList = queryList;
	}

	public void saveQueryListFromFile() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(Const.LINK_QUERY_INITFILENAME).getFile());
		saveQueryList(UtilNativeQuery.mapper.readTree(file));
	}

	public void saveQueryList(Object query) {
		queryList = UtilNativeQuery.mapper.convertValue(query, UtilNativeQuery.typeRef);
	}

	public void updateQueryList(ObjectNode query) {
		Iterator<Entry<String, JsonNode>> nodeEntry = query.fields();
		while (nodeEntry.hasNext()) {
			Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeEntry.next();
			if (entry.getValue().isNull() && queryList.containsKey(entry.getKey())) {
				queryList.remove(entry.getKey());
				continue;
			}
			queryList.put(entry.getKey(), entry.getValue());
		}
	}

	public Object processCustomQuery(String queryName, String param) throws Exception {
		if (queryList.isEmpty()) {
			return Const.LINK_QUERY_QUERYLISTEMPTY;
		}
		ArrayNode paramNode = ((ArrayNode) UtilNativeQuery.mapper.readTree(param));
		return processCustomQuery(queryName, paramNode);
	}

	public Object processCustomQuery(String queryName, ArrayNode param) throws Exception {
		JsonNode queryObject = queryList.get(queryName);
		if (queryObject == null) {
			return Const.LINK_QUERY_QUERYNOTEXIST;
		}
		if (queryObject.has(Const.PARAM_QUERY)) {
			return nativeQueryDAO.processQueryObject(queryObject, param);
		}
		return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo((ObjectNode) queryObject.deepCopy(), param);
	}
}
