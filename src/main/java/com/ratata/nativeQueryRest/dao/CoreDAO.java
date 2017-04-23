package com.ratata.nativeQueryRest.dao;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;

public interface CoreDAO {
	public Object nativeQuery(NativeQueryParam nativeQueryParam) throws Exception;

	public Object processQueryObject(JsonNode queryObject, JsonNode param) throws Exception;

	public Object processSingleNestedNode(Map<String, Object> rootResult, JsonNode insideObject) throws Exception;

	public void saveObject(JsonNode obj, String className) throws Exception;

	public void saveLinkedObject(JsonNode obj) throws Exception;
}
