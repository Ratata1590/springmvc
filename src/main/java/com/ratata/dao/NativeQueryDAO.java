package com.ratata.dao;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface NativeQueryDAO {
	public Object nativeQuery(String query, String className, List<String> resultSet, String queryMode, ArrayNode param,
			Boolean isNative, Integer offset, Integer limit) throws Exception;

	public Object processQueryObject(JsonNode queryObject, ArrayNode param) throws Exception;

	public Object processSingleNestedNode(Map<String, Object> rootResult, JsonNode insideObject) throws Exception;

	public ArrayNode resolveParamArrayNode(Map<String, Object> rootResult, ArrayNode arrayNodeConfig);

	public void saveObject(JsonNode obj, String className) throws Exception;

	public void saveNestedObject(JsonNode obj) throws Exception;
}
