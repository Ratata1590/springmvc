package com.ratata.NativeQuery.dao;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ratata.NativeQuery.Util.Const;
import com.ratata.NativeQuery.Util.UtilNativeQuery;

@Component
public class NativeQueryDAOImpl implements NativeQueryDAO {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	@SuppressWarnings("unchecked")
	public Object nativeQuery(String query, String className, List<String> resultSet, String queryMode, ArrayNode param,
			Boolean isNative, Integer lockModeType, Integer offset, Integer limit) throws Exception {
		Object result = returnResult(query, className, param, offset, limit, queryMode, isNative, lockModeType);

		if (resultSet.isEmpty()) {
			return result;
		}
		if (queryMode.equals(Const.QUERYMODE_SINGLE)) {
			if (result == null) {
				return null;
			}
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

	private Object returnResult(String query, String className, ArrayNode param, Integer offset, Integer limit,
			String queryMode, Boolean isNative, Integer lockModeType) throws Exception {
		Query queryObj;
		if (isNative) {
			if (!className.isEmpty()) {
				queryObj = em.createNativeQuery(query, Class.forName(className));
			} else {
				queryObj = em.createNativeQuery(query);
			}
		} else {
			if (!className.isEmpty()) {
				queryObj = em.createQuery(query, Class.forName(className));
			} else {
				queryObj = em.createQuery(query);
			}
		}

		// TODO: check no transation while lock
		// if (!lockModeType.equals(0)) {
		// queryObj.setLockMode(resolveLockMode(lockModeType));
		// }

		if (!(param.size() == 0)) {
			for (int i = 0; i < param.size(); i++) {
				queryObj.setParameter(i, resolveParam(param.get(i)));
			}
		}

		if (!offset.equals(0)) {
			queryObj.setFirstResult(offset);
		}

		if (!offset.equals(0)) {
			queryObj.setMaxResults(limit);
		}

		if (queryMode.equals(Const.QUERYMODE_SINGLE)) {
			try {
				return queryObj.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}
		}

		if (queryMode.equals(Const.QUERYMODE_UPDATE)) {
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
				return node.asLong();
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
		String query = queryObject.get(Const.PARAM_QUERY).asText("");
		Boolean isNative = queryObject.has(Const.PARAM_ISNATIVE) ? queryObject.get(Const.PARAM_ISNATIVE).asBoolean()
				: true;
		String queryMode = queryObject.has(Const.PARAM_QUERYMODE) ? queryObject.get(Const.PARAM_QUERYMODE).asText()
				: "L";
		Integer lockModeType = queryObject.has(Const.LOCKMODETYPE) ? queryObject.get(Const.LOCKMODETYPE).asInt() : 0;
		List<String> resultSet = UtilNativeQuery
				.arrayNodeToListString((ArrayNode) queryObject.get(Const.PARAM_RESULTSET));

		if (queryObject.has(Const.PARAM_INSIDEOBJECT)) {
			JsonNode insideObject = queryObject.get(Const.PARAM_INSIDEOBJECT);
			return nestedNativeQuery(query, resultSet, queryMode, param, isNative, lockModeType, insideObject);
		}
		String className = queryObject.has(Const.PARAM_CLASSNAME) ? queryObject.get(Const.PARAM_CLASSNAME).asText()
				: "";
		Integer offset = queryObject.has(Const.PARAM_OFFSET) ? queryObject.get(Const.PARAM_OFFSET).asInt() : 0;
		Integer limit = queryObject.has(Const.PARAM_LIMIT) ? queryObject.get(Const.PARAM_LIMIT).asInt() : 0;
		return nativeQuery(query, className, resultSet, queryMode, param, isNative, lockModeType, offset, limit);
	}

	@SuppressWarnings("unchecked")
	private Object nestedNativeQuery(String query, List<String> resultSet, String queryMode, ArrayNode param,
			Boolean isNative, Integer lockModeType, JsonNode insideObject) throws Exception {
		if (queryMode.equals(Const.QUERYMODE_SINGLE)) {
			Map<String, Object> rootResult = (Map<String, Object>) nativeQuery(query, "", resultSet, queryMode, param,
					isNative, lockModeType, 0, 0);
			return processSingleNestedNode(rootResult, insideObject);
		}
		List<Map<String, Object>> rootResultList = (List<Map<String, Object>>) nativeQuery(query, "", resultSet,
				queryMode, param, isNative, lockModeType, 0, 0);
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
	public void saveObject(JsonNode obj, String className) throws Exception {
		if (!obj.isArray()) {
			em.persist(UtilNativeQuery.mapper.convertValue(obj, Class.forName(className)));
		} else {
			ArrayNode listNode = (ArrayNode) obj;
			for (JsonNode node : listNode) {
				em.persist(UtilNativeQuery.mapper.convertValue(node, Class.forName(className)));
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
		Object parent = em.find(Class.forName(className), node.get("id").asLong());
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
