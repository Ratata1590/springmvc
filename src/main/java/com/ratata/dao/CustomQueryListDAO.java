package com.ratata.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class CustomQueryListDAO {

  public static final String LINK_QUERY = "linkquery";
  @Autowired
  private NativeQueryDynamicPojoDAO nativeQueryDynamicPojoDAO;

  @Autowired
  private NativeQueryDAO nativeQueryDAO;

  public static Map<String, JsonNode> queryList = new HashMap<String, JsonNode>();
  private TypeReference<HashMap<String, JsonNode>> typeRef =
      new TypeReference<HashMap<String, JsonNode>>() {};

  public void saveQueryList(Object query) {
    queryList = UtilNativeQuery.mapper.convertValue(query, typeRef);
  }

  public void updateQueryList(ObjectNode query) {
    Iterator<Entry<String, JsonNode>> nodeEntry = query.fields();
    while (nodeEntry.hasNext()) {
      Entry<String, JsonNode> entry = (Entry<String, JsonNode>) nodeEntry.next();
      if (entry.getValue().isNull() && queryList.containsKey(entry.getKey())) {
        CustomQueryListDAO.queryList.remove(entry.getKey());
        continue;
      }
      CustomQueryListDAO.queryList.put(entry.getKey(), entry.getValue());
    }
  }

  public Object processCustomQuery(String queryName, ArrayNode param)
      throws ClassNotFoundException, JsonProcessingException, IOException {
    JsonNode queryObject = CustomQueryListDAO.queryList.get(queryName);
    if (queryObject == null) {
      return "query not exist";
    }
    if (queryObject.has(NativeQueryDynamicPojoDAO.PARAM_QUERY)) {
      return nativeQueryDAO.processQueryObject(queryObject, param);
    }
    return nativeQueryDynamicPojoDAO.nativeWithDynamicPojo((ObjectNode) queryObject.deepCopy(),
        param);
  }
}
