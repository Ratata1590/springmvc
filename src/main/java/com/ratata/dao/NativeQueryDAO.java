package com.ratata.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.Util.Const;
import com.ratata.Util.UtilNativeQuery;

@Component
public class NativeQueryDAO {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Transactional
	public Object nativeQuery(String query, String className, List<String> resultSet, String queryMode, ArrayNode param,
			int offset, int limit) throws Exception {
		Object result = returnResult(queryMode, returnQuery(query, className, param, offset, limit));
		if (resultSet == null || resultSet.size() == 0) {
			return result;
		}
		if (queryMode.equals(Const.QUERYMODE_SINGLE)) {
			Object[] record = (Object[]) result;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for (int i = 0; i < record.length; i++) {
				resultMap.put(resultSet.get(i), record[i]);
			}
			return resultMap;
		} else {
			List<Object> resultReturn = new ArrayList<Object>();
			for (Object[] record : (List<Object[]>) result) {
				int i = 0;
				Map<String, Object> resultMap = new HashMap<String, Object>();
				while (i < record.length) {
					resultMap.put(resultSet.get(i), record[i]);
					i++;
				}
				resultReturn.add(resultMap);
			}
			return resultReturn;
		}
	}

	private Object returnResult(String queryMode, Query queryOjb) {
		if (queryMode.equals(Const.QUERYMODE_SINGLE)) {
			try {
				return queryOjb.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}
		}
		if (queryMode.equals(Const.QUERYMODE_UPDATE)) {
			return queryOjb.executeUpdate();
		}
		return queryOjb.getResultList();
	}

	private Query returnQuery(String query, String className, ArrayNode param, int offset, int limit)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		Query queryObj;
		if (className != null && !className.isEmpty()) {
			queryObj = em.createNativeQuery(query, Class.forName(className));
		} else {
			queryObj = em.createNativeQuery(query);
		}
		if (param != null && !(param.size() == 0)) {
			for (int i = 0; i < param.size(); i++) {
				queryObj.setParameter(i, resolveParam(param.get(i)));
			}
		}
		if (offset != 0) {
			queryObj.setFirstResult(offset);
		}
		if (limit != 0) {
			queryObj.setMaxResults(limit);
		}
		return queryObj;
	}

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

	public Object processQueryObject(JsonNode queryObject, ArrayNode param) throws Exception {
		String query = queryObject.has(Const.PARAM_QUERY) ? queryObject.get(Const.PARAM_QUERY).asText() : null;
		String queryMode = queryObject.has(Const.PARAM_QUERYMODE) ? queryObject.get(Const.PARAM_QUERYMODE).asText()
				: "L";
		List<String> resultSet = queryObject.has(Const.PARAM_RESULTSET)
				? UtilNativeQuery.arrayNodeToListString((ArrayNode) queryObject.get(Const.PARAM_RESULTSET)) : null;

		if (queryObject.has(Const.PARAM_INSIDEOBJECT)) {
			JsonNode insideObject = queryObject.has(Const.PARAM_INSIDEOBJECT)
					? queryObject.get(Const.PARAM_INSIDEOBJECT) : null;
			return nestedNativeQuery(query, resultSet, queryMode, param, insideObject);
		}
		String className = queryObject.has(Const.PARAM_CLASSNAME) ? queryObject.get(Const.PARAM_CLASSNAME).asText()
				: null;
		int offset = queryObject.has(Const.PARAM_OFFSET) ? queryObject.get(Const.PARAM_OFFSET).asInt() : 0;
		int limit = queryObject.has(Const.PARAM_LIMIT) ? queryObject.get(Const.PARAM_LIMIT).asInt() : 0;
		return nativeQuery(query, className, resultSet, queryMode, param, offset, limit);
	}

	@SuppressWarnings("unchecked")
	private Object nestedNativeQuery(String query, List<String> resultSet, String queryMode, ArrayNode param,
			JsonNode insideObject) throws Exception {
		if (queryMode.equals(Const.QUERYMODE_SINGLE)) {
			Map<String, Object> rootResult = (Map<String, Object>) nativeQuery(query, null, resultSet, queryMode, param,
					0, 0);
			return processSingleNestedNode(rootResult, insideObject);
		}
		List<Map<String, Object>> rootResultList = (List<Map<String, Object>>) nativeQuery(query, null, resultSet,
				queryMode, param, 0, 0);
		List<Object> result = new ArrayList<Object>();
		for (Map<String, Object> item : rootResultList) {
			result.add(processSingleNestedNode(item, insideObject));
		}
		return result;
	}

	public Object processSingleNestedNode(Map<String, Object> rootResult, JsonNode insideObject) throws Exception {
		Iterator<String> obj = ((ObjectNode) insideObject).fieldNames();
		while (obj.hasNext()) {
			String key = obj.next();
			rootResult.put(key,
					processQueryObject(insideObject.get(key).get(Const.PARAM_SINGLEREQUEST_DATA), resolveParamArrayNode(
							rootResult, (ArrayNode) insideObject.get(key).get(Const.PARAM_SINGLEREQUEST_PARAM))));
		}
		return rootResult;
	}

	public ArrayNode resolveParamArrayNode(Map<String, Object> rootResult, ArrayNode arrayNodeConfig) {
		LinkedList<Object> resultParam = new LinkedList<Object>();
		for (JsonNode config : arrayNodeConfig) {
			resultParam.addLast(rootResult.get(config.asText()));
		}
		return (ArrayNode) UtilNativeQuery.mapper.valueToTree(resultParam);
	}

	@Transactional
	public void saveObject(Object obj, String className) throws IllegalArgumentException, ClassNotFoundException {
		em.persist(UtilNativeQuery.mapper.convertValue(obj, Class.forName(className)));
	}
}
