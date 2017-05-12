package com.ratata.nativeQueryRest.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.nativeQueryRest.pojo.NativeQueryParam;
import com.ratata.nativeQueryRest.utils.Const;
import com.ratata.nativeQueryRest.utils.Mapper;

@Component
public class CoreDAOimpl implements CoreDAO {
  @PersistenceContext
  private EntityManager em;

  @Transactional
  @SuppressWarnings("unchecked")
  public Object nativeQuery(NativeQueryParam nativeQueryParam) throws Exception {
    Object result = returnResult(nativeQueryParam);

    if (nativeQueryParam.getResultSet() == null || nativeQueryParam.getResultSet().size() == 0) {
      return result;
    }
    if (nativeQueryParam.getQueryMode().equals(Const.QUERYMODE_SINGLE)) {
      if (result == null) {
        return null;
      }
      Object[] record = (Object[]) result;
      Map<String, Object> resultMap = new HashMap<String, Object>();
      for (int i = 0; i < record.length; i++) {
        resultMap.put(nativeQueryParam.getResultSet().get(i).asText(), record[i]);
      }
      return resultMap;
    } else {
      List<Object> resultReturn = new ArrayList<Object>();
      for (Object[] record : (List<Object[]>) result) {
        int i = 0;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        while (i < record.length) {
          resultMap.put(nativeQueryParam.getResultSet().get(i).asText(), record[i]);
          i++;
        }
        resultReturn.add(resultMap);
      }
      return resultReturn;
    }
  }

  private Object returnResult(NativeQueryParam nativeQueryParam) throws Exception {
    Query queryObj;
    if (nativeQueryParam.getIsNative()) {
      if (!nativeQueryParam.getClassName().isEmpty()) {
        queryObj = em.createNativeQuery(nativeQueryParam.getQuery(),
            Class.forName(nativeQueryParam.getClassName()));
      } else {
        queryObj = em.createNativeQuery(nativeQueryParam.getQuery());
      }
    } else {
      if (!nativeQueryParam.getClassName().isEmpty()) {
        queryObj = em.createQuery(nativeQueryParam.getQuery(),
            Class.forName(nativeQueryParam.getClassName()));
      } else {
        queryObj = em.createQuery(nativeQueryParam.getQuery());
      }
    }

    // TODO: check no transation while lock
    // if (!lockModeType.equals(0)) {
    // queryObj.setLockMode(resolveLockMode(lockModeType));
    // }

    if (nativeQueryParam.getParam() != null && nativeQueryParam.getParam().isArray()) {
      for (int i = 0; i < nativeQueryParam.getParam().size(); i++) {
        queryObj.setParameter(i, resolveParam(nativeQueryParam.getParam().get(i)));
      }
    }

    if (nativeQueryParam.getParam() != null && nativeQueryParam.getParam().isObject()) {
      Iterator<Entry<String, JsonNode>> iter = nativeQueryParam.getParam().fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> item = iter.next();
        queryObj.setParameter(item.getKey(), resolveParam(item.getValue()));
      }
    }

    if (!nativeQueryParam.getOffset().equals(0)) {
      queryObj.setFirstResult(nativeQueryParam.getOffset());
    }

    if (!nativeQueryParam.getLimit().equals(0)) {
      queryObj.setMaxResults(nativeQueryParam.getLimit());
    }

    if (nativeQueryParam.getQueryMode().equals(Const.QUERYMODE_SINGLE)) {
      try {
        return queryObj.getSingleResult();
      } catch (NoResultException e) {
        return null;
      }
    }

    if (nativeQueryParam.getQueryMode().equals(Const.QUERYMODE_MODIFY)) {
      return queryObj.executeUpdate();
    }
    return queryObj.getResultList();
  }

  // private LockModeType resolveLockMode(Integer lockModeType) {
  // switch (lockModeType) {
  // case 1:
  // return LockModeType.OPTIMISTIC;
  // case 2:
  // return LockModeType.OPTIMISTIC_FORCE_INCREMENT;
  // case 3:
  // return LockModeType.PESSIMISTIC_FORCE_INCREMENT;
  // case 4:
  // return LockModeType.PESSIMISTIC_READ;
  // case 5:
  // return LockModeType.PESSIMISTIC_WRITE;
  // case 6:
  // return LockModeType.READ;
  // case 7:
  // return LockModeType.WRITE;
  // default:
  // return LockModeType.NONE;
  // }
  // }

  private Object resolveParam(JsonNode node) {
    if (node.isArray()) {
      LinkedList<Object> arrayData = new LinkedList<Object>();
      for (int i = 0; i < ((ArrayNode) node).size(); i++) {
        arrayData.addLast(resolveParam(node.get(i)));
      }
      return arrayData;
    }
    if (!node.isValueNode()) {
      return null;
    }
    if (node.isNumber()) {
      if (node.numberType().equals(NumberType.FLOAT)) {
        return node.floatValue();
      }
      if (node.numberType().equals(NumberType.BIG_DECIMAL)) {
        return node.decimalValue();
      }
      if (node.numberType().equals(NumberType.BIG_INTEGER)) {
        return node.bigIntegerValue();
      }
      if (node.numberType().equals(NumberType.DOUBLE)) {
        return node.asDouble();
      }
      if (node.numberType().equals(NumberType.INT)) {
        return node.asInt();
      }
      if (node.numberType().equals(NumberType.LONG)) {
        return node.asLong();
      }
    }
    if (node.isBoolean()) {
      return node.asBoolean();
    }
    if (node.isTextual()) {
      return node.asText();
    }
    return node;
  }

  public Object processQueryObject(JsonNode queryObject, JsonNode param) throws Exception {
    NativeQueryParam nativeQueryParam = new NativeQueryParam(queryObject, param);
    if (queryObject.has(Const.PARAM_INSIDEOBJECT)) {
      JsonNode insideObject = queryObject.get(Const.PARAM_INSIDEOBJECT);
      return nestedNativeQuery(nativeQueryParam, insideObject);
    }
    return nativeQuery(nativeQueryParam);
  }

  @SuppressWarnings("unchecked")
  private Object nestedNativeQuery(NativeQueryParam nativeQueryParam, JsonNode insideObject)
      throws Exception {
    if (nativeQueryParam.getQueryMode().equals(Const.QUERYMODE_SINGLE)) {
      Map<String, Object> rootResult = (Map<String, Object>) nativeQuery(nativeQueryParam);
      return processSingleNestedNode(rootResult, insideObject);
    }
    List<Map<String, Object>> rootResultList =
        (List<Map<String, Object>>) nativeQuery(nativeQueryParam);
    List<Object> result = new ArrayList<Object>();
    for (Map<String, Object> item : rootResultList) {
      result.add(processSingleNestedNode(item, insideObject));
    }
    return result;
  }

  public Object processSingleNestedNode(Map<String, Object> rootResult, JsonNode insideObject)
      throws Exception {
    Iterator<String> obj = ((ObjectNode) insideObject).fieldNames();
    while (obj.hasNext()) {
      String key = obj.next();
      Object result = null;
      try {
        result = processQueryObject(insideObject.get(key).get(Const.PARAM_DATA),
            resolvePassParam(rootResult, insideObject.get(key).get(Const.PARAM_PASSPARAM)));
      } catch (Exception e) {
        ObjectNode errorNode = Mapper.mapper.createObjectNode();
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errorNode.put("errorNode", errors.toString());
        result = errorNode;
      }
      rootResult.put(key, result);
    }
    return rootResult;
  }

  @SuppressWarnings("unchecked")
  private JsonNode resolvePassParam(Map<String, Object> rootResult, JsonNode arrayNodeConfig) {
    Object resultParam = null;
    if (arrayNodeConfig.isArray()) {
      resultParam = new LinkedList<Object>();
      for (JsonNode config : arrayNodeConfig) {
        ((LinkedList<Object>) resultParam).addLast(rootResult.get(config.asText()));
      }
    }
    if (arrayNodeConfig.isObject()) {
      resultParam = new HashMap<String, Object>();
      Iterator<Entry<String, JsonNode>> iter = arrayNodeConfig.fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> item = iter.next();
        ((Map<String, Object>) resultParam).put(item.getKey(),
            rootResult.get(item.getValue().asText()));
      }
    }
    return Mapper.mapper.valueToTree(resultParam);
  }

  @Transactional
  public void saveObject(JsonNode obj, String className) throws Exception {
    if (!obj.isArray()) {
      em.persist(Mapper.mapper.convertValue(obj, Class.forName(className)));
    } else {
      ArrayNode listNode = (ArrayNode) obj;
      for (JsonNode node : listNode) {
        em.persist(Mapper.mapper.convertValue(node, Class.forName(className)));
      }
    }
  }

  @Transactional
  public void saveLinkedObject(JsonNode node) throws Exception {
    if (node.isArray()) {
      node = (ArrayNode) node;
      for (JsonNode innerNode : node) {
        resolveLinkedObject(innerNode);
      }
    }
    if (node.isObject()) {
      resolveLinkedObject(node);
    }
  }

  private void resolveLinkedObject(JsonNode node) throws Exception {
    String className = node.get("className").asText();
    Object parent = em.find(Class.forName(className), node.get("id").asInt());
    ArrayNode childList = (ArrayNode) node.get("childList");
    for (JsonNode child : childList) {
      StringBuilder query = new StringBuilder();
      query.append("UPDATE ");
      query.append(child.get("className").asText());
      query.append(" a SET a.");
      query.append(child.get("parentKey").asText());
      query.append(" =:parent WHERE a.id IN (");
      query.append(child.get("idList").asText());
      query.append(")");
      em.createQuery(query.toString()).setParameter("parent", parent).executeUpdate();
    }
  }
}
