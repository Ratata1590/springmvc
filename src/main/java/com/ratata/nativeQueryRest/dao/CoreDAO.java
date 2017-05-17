package com.ratata.nativeQueryRest.dao;

import java.lang.reflect.Field;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;

public interface CoreDAO {
  public Object nativeQuery(NativeQueryParam nativeQueryParam) throws Exception;

  public Field[] getEntityMapDetail(String className) throws Exception;

  public ObjectNode getEntityMap(String className) throws Exception;

  public Object processQueryObject(JsonNode queryObject, JsonNode param);

  public JsonNode initObject(String className, boolean showClassName, boolean showRelation)
      throws Exception;

  public JsonNode saveObject(JsonNode obj) throws Exception;

  public JsonNode updateObject(JsonNode node) throws Exception;

  public JsonNode deleteObject(JsonNode node) throws Exception;

  public Object linkObject(JsonNode obj);

  public String tableFieldsToResultset(String queryShow, Integer fieldNumber) throws Exception;
}
