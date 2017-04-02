package com.ratata.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class NativeQueryDAO {

	@PersistenceContext
	private EntityManager em;

	ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public Object nativeQuery(String query, String className, List<String> resultSet, boolean singleReturn,
			String param) throws ClassNotFoundException, JsonProcessingException, IOException {
		Object result = returnResult(singleReturn, returnQuery(query, className, param));

		if (resultSet != null && resultSet.size() != 0) {
			if (singleReturn) {
				Object[] record = (Object[]) result;
				Map<String, Object> resultMap = new HashMap<String, Object>();
				for (int i = 0; i < record.length; i++) {
					resultMap.put(resultSet.get(i), record[i]);
				}
				return resultMap;
			} else {
				List<Object> resultReturn = new ArrayList<>();
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
		} else {
			return result;
		}
	}

	private Object returnResult(boolean singleReturn, Query queryOjb) {
		if (singleReturn) {
			return queryOjb.getSingleResult();
		} else {
			return queryOjb.getResultList();
		}
	}

	private Query returnQuery(String query, String className, String param)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		Query queryObj;
		if (className != null && !className.isEmpty()) {
			queryObj = em.createNativeQuery(query, Class.forName(className));
		} else {
			queryObj = em.createNativeQuery(query);
		}
		if (param != null && !param.isEmpty()) {
			ArrayNode paramNode = ((ArrayNode) mapper.readTree(param));
			for (int i = 0; i < paramNode.size(); i++) {
				queryObj.setParameter(i, resolveParam(paramNode.get(i)));
			}
		}
		return queryObj;
	}

	private Object resolveParam(JsonNode node) {
		if (node.isArray()) {
			List<Object> arrayData = new ArrayList<Object>();
			for (int i = 0; i < ((ArrayNode) node).size(); i++) {
				arrayData.add(resolveParam(node.get(i)));
			}
			return arrayData;
		}
		if (node.isNumber()) {
			return node.asLong();
		}
		if (node.isBoolean()) {
			return node.asBoolean();
		}
		if (node.isTextual()) {
			return node.asText();
		}
		return node;
	}

	public Object processQueryObject(JsonNode queryObject, String param)
			throws ClassNotFoundException, JsonProcessingException, IOException {
		String query = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_QUERY)
				? queryObject.get(NativeQueryDynamicPojoDAO.PARAM_QUERY).asText() : null;
		String className = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_CLASSNAME)
				? queryObject.get(NativeQueryDynamicPojoDAO.PARAM_CLASSNAME).asText() : null;
		List<String> resultSet = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_RESULTSET) ? UtilNativeQuery
				.arrayNodeToListString((ArrayNode) queryObject.get(NativeQueryDynamicPojoDAO.PARAM_RESULTSET)) : null;
		boolean singleReturn = queryObject.has(NativeQueryDynamicPojoDAO.PARAM_SINGLERETURN)
				? queryObject.get(NativeQueryDynamicPojoDAO.PARAM_SINGLERETURN).asBoolean() : false;
		return nativeQuery(query, className, resultSet, singleReturn, param);
	}

	@Transactional
	public void saveObject(Object obj, String className) throws IllegalArgumentException, ClassNotFoundException {
		em.persist(mapper.convertValue(obj, Class.forName(className)));
	}
}
