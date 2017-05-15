package com.ratata.nativeQueryRest.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;

public interface CoreDAO {
	public Object nativeQuery(NativeQueryParam nativeQueryParam) throws Exception;

	public Object processQueryObject(JsonNode queryObject, JsonNode param);

	public void saveObject(JsonNode obj, String className) throws Exception;

	public Object linkObject(JsonNode obj);

	public JsonNode updateObject(JsonNode node) throws Exception;
}
