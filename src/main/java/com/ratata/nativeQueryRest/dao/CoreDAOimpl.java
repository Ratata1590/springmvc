package com.ratata.nativeQueryRest.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
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

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	// ===========================================nativeQuery===========================================
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
				queryObj = em.createQuery(nativeQueryParam.getQuery(), Class.forName(nativeQueryParam.getClassName()));
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

	// ===========================================getEntityMapDetail===========================================
	public Field[] getEntityMapDetail(String className) throws Exception {
		return Class.forName(className).getDeclaredFields();
	}

	// ===========================================getEntityMap===========================================
	public ObjectNode getEntityMap(String className) throws Exception {
		ObjectNode node = Mapper.mapper.createObjectNode();
		Field[] fields = Class.forName(className).getDeclaredFields();
		for (Field field : fields) {
			node.put(field.getName(), field.getAnnotatedType().getType().getTypeName());
		}
		return node;
	}

	// ===========================================processQueryObject===========================================
	public Object processQueryObject(JsonNode queryObject, JsonNode param) {
		try {
			NativeQueryParam nativeQueryParam = new NativeQueryParam(queryObject, param);
			if (queryObject.has(Const.PARAM_INSIDEOBJECT)) {
				JsonNode insideObject = queryObject.get(Const.PARAM_INSIDEOBJECT);
				return nestedNativeQuery(nativeQueryParam, insideObject);
			}
			return nativeQuery(nativeQueryParam);
		} catch (Exception e) {
			return createErrorObject(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object nestedNativeQuery(NativeQueryParam nativeQueryParam, JsonNode insideObject) throws Exception {
		if (nativeQueryParam.getQueryMode().equals(Const.QUERYMODE_SINGLE)) {
			Map<String, Object> rootResult = (Map<String, Object>) nativeQuery(nativeQueryParam);
			return processSingleNestedNode(rootResult, insideObject);
		}
		List<Map<String, Object>> rootResultList = (List<Map<String, Object>>) nativeQuery(nativeQueryParam);
		List<Object> result = new ArrayList<Object>();
		for (Map<String, Object> item : rootResultList) {
			result.add(processSingleNestedNode(item, insideObject));
		}
		return result;
	}

	private Object processSingleNestedNode(Map<String, Object> rootResult, JsonNode insideObject) {
		Iterator<String> obj = ((ObjectNode) insideObject).fieldNames();
		while (obj.hasNext()) {
			String key = obj.next();
			Object result = null;
			result = processQueryObject(insideObject.get(key).get(Const.PARAM_DATA),
					resolvePassParam(rootResult, insideObject.get(key).get(Const.PARAM_PASSPARAM)));
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
				((Map<String, Object>) resultParam).put(item.getKey(), rootResult.get(item.getValue().asText()));
			}
		}
		return Mapper.mapper.valueToTree(resultParam);
	}

	// ===========================================initObject===========================================
	public JsonNode initObject(String className, boolean showClassName, boolean showRelation) throws Exception {
		ObjectNode node = Mapper.mapper.valueToTree(Class.forName(className).newInstance());
		node.remove(Const.INITOBJECT_PARAM_ID);
		if (showClassName) {
			node.put(Const.INITOBJECT_PARAM_CLASSNAME, className);
		}
		if (!showRelation) {
			node = (ObjectNode) node;
			Iterator<String> keys = node.fieldNames();
			while (keys.hasNext()) {
				String key = keys.next();
				if (node.get(key).isArray() || node.get(key).isObject()) {
					node.remove(key);
				}
			}
		}
		return node;
	}

	// ===========================================saveObject===========================================
	@Transactional
	public JsonNode saveObject(JsonNode node) throws Exception {
		if (node.isObject()) {
			return resolvecreateObject((ObjectNode) node);
		}

		if (node.isArray()) {
			ArrayNode result = Mapper.mapper.createArrayNode();
			node = (ArrayNode) node;
			for (JsonNode innerNode : node) {
				result.add(resolvecreateObject((ObjectNode) innerNode));
			}
			return result;
		}
		return null;
	}

	private JsonNode resolvecreateObject(ObjectNode node) {
		try {
			String className = node.get(Const.INITOBJECT_PARAM_CLASSNAME).asText();
			node.remove(Const.INITOBJECT_PARAM_CLASSNAME);
			em.persist(Mapper.mapper.convertValue(node, Class.forName(className)));
			ObjectNode result = Mapper.mapper.createObjectNode();
			result.put(Const.UPDATEOBJECT_STATUS, Const.UPDATEOBJECT_SUCCESS);
			return result;
		} catch (Exception e) {
			return createErrorObject(e);
		}
	}

	// ===========================================updateObject===========================================
	@Transactional
	public JsonNode updateObject(JsonNode node) throws Exception {
		if (node.isObject()) {
			return resolveupdateObject((ObjectNode) node);
		}

		if (node.isArray()) {
			ArrayNode result = Mapper.mapper.createArrayNode();
			node = (ArrayNode) node;
			for (JsonNode innerNode : node) {
				result.add(resolveupdateObject((ObjectNode) innerNode));
			}
			return result;
		}
		return null;
	}

	private JsonNode resolveupdateObject(ObjectNode node) {
		try {
			Object object = em.find(Class.forName(node.get(Const.UPDATEOBJECT_CLASSNAME).asText()),
					node.get(Const.UPDATEOBJECT_ID).asInt());
			object.getClass().getMethod(Const.UPDATEOBJECT_UPDATEMETHOD, ObjectNode.class).invoke(object, node);
			ObjectNode result = Mapper.mapper.createObjectNode();
			result.put(Const.UPDATEOBJECT_STATUS, Const.UPDATEOBJECT_SUCCESS);
			return result;
		} catch (Exception e) {
			return createErrorObject(e);
		}
	}

	// ===========================================deleteObject===========================================
	@Transactional
	public JsonNode deleteObject(JsonNode node) throws Exception {
		if (node.isObject()) {
			return resolveDeleteObject((ObjectNode) node);
		}

		if (node.isArray()) {
			ArrayNode result = Mapper.mapper.createArrayNode();
			node = (ArrayNode) node;
			for (JsonNode innerNode : node) {
				result.add(resolveDeleteObject((ObjectNode) innerNode));
			}
			return result;
		}
		return null;
	}

	private JsonNode resolveDeleteObject(ObjectNode node) {
		try {
			Object object = em.find(Class.forName(node.get(Const.DELETEOBJECT_CLASSNAME).asText()),
					node.get(Const.DELETEOBJECT_ID).asInt());
			if (object != null) {
				em.remove(object);
			}
			ObjectNode result = Mapper.mapper.createObjectNode();
			result.put(Const.DELETEOBJECT_STATUS, Const.DELETEOBJECT_SUCCESS);
			return result;
		} catch (Exception e) {
			return createErrorObject(e);
		}
	}

	// ===========================================linkObject===========================================
	@Transactional
	public Object linkObject(JsonNode node) {
		ArrayNode result = Mapper.mapper.createArrayNode();
		if (node.isArray()) {
			node = (ArrayNode) node;
			for (JsonNode innerNode : node) {
				result.add(checkLinkAction(innerNode));
			}
		}
		if (node.isObject()) {
			return checkLinkAction(node);
		}
		return result;
	}

	private JsonNode checkLinkAction(JsonNode node) {
		try {
			String className = node.get(Const.LINKOBJECT_PARAM_CLASSNAME).asText();
			Object parent = em.find(Class.forName(className), node.get(Const.LINKOBJECT_PARAM_ID).asInt());
			ArrayNode actionList = (ArrayNode) node.get(Const.LINKOBJECT_PARAM_ACTIONLIST);
			ArrayNode result = Mapper.mapper.createArrayNode();
			for (JsonNode actionNode : actionList) {
				try {
					switch (actionNode.get(Const.LINKOBJECT_PARAM_LINKACTION).asText()) {
					case Const.LINKOBJECT_PARAM_LINKACTION_SET:
						result.add(resolveSetObject(actionNode, parent));
						break;
					case Const.LINKOBJECT_PARAM_LINKACTION_ADD:
						result.add(resolveAddObject(actionNode, parent));
						break;
					case Const.LINKOBJECT_PARAM_LINKACTION_REMOVE:
						result.add(resolveRemoveObject(actionNode, parent));
						break;
					case Const.LINKOBJECT_PARAM_LINKACTION_CLEAR:
						result.add(resolveClearObject(actionNode, parent));
						break;
					}
				} catch (Exception e) {
					result.add(createErrorObject(e));
				}
			}
			((ObjectNode) node).set(Const.LINKOBJECT_PARAM_ACTIONLIST, result);
		} catch (Exception e) {
			return createErrorObject(e);
		}
		return node;
	}

	private int resolveSetObject(JsonNode node, Object parent) throws Exception {
		String query = String.format("UPDATE %1$s a SET a.%2$s =:parent WHERE a.id IN (%3$s)",
				node.get(Const.LINKOBJECT_PARAM_CLASSNAME).asText(),
				node.get(Const.LINKOBJECT_PARAM_PARENTKEY).asText(),
				arrayNodeToString((ArrayNode) node.get(Const.LINKOBJECT_PARAM_IDLIST)));
		return em.createQuery(query).setParameter("parent", parent).executeUpdate();
	}

	private int resolveAddObject(JsonNode node, Object parent) throws Exception {
		String query = String.format("UPDATE %1$s a SET a.%2$s =:parent WHERE a.id IN (%3$s) and a.%2$s = null",
				node.get(Const.LINKOBJECT_PARAM_CLASSNAME).asText(),
				node.get(Const.LINKOBJECT_PARAM_PARENTKEY).asText(),
				arrayNodeToString((ArrayNode) node.get(Const.LINKOBJECT_PARAM_IDLIST)));
		return em.createQuery(query).setParameter("parent", parent).executeUpdate();
	}

	private int resolveRemoveObject(JsonNode node, Object parent) throws Exception {
		String query = String.format("UPDATE %1$s a SET a.%2$s = null WHERE a.id IN (%3$s) and a.%2$s =:parent",
				node.get(Const.LINKOBJECT_PARAM_CLASSNAME).asText(),
				node.get(Const.LINKOBJECT_PARAM_PARENTKEY).asText(),
				arrayNodeToString((ArrayNode) node.get(Const.LINKOBJECT_PARAM_IDLIST)));
		return em.createQuery(query).setParameter("parent", parent).executeUpdate();
	}

	private int resolveClearObject(JsonNode node, Object parent) throws Exception {
		String query = String.format("UPDATE %1$s a SET a.%2$s = null WHERE a.%2$s =:parent",
				node.get(Const.LINKOBJECT_PARAM_CLASSNAME).asText(),
				node.get(Const.LINKOBJECT_PARAM_PARENTKEY).asText());
		return em.createQuery(query).setParameter("parent", parent).executeUpdate();
	}

	// ===========================================tableFieldsToResultset===========================================
	@SuppressWarnings("unchecked")
	public String tableFieldsToResultset(String queryShow, Integer fieldNumber) throws Exception {
		NativeQueryParam query = new NativeQueryParam();
		query.setQuery(queryShow);
		List<Object[]> result = (List<Object[]>) nativeQuery(query);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < result.size(); i++) {
			sb.append((String) result.get(i)[fieldNumber]);
			if (i == result.size() - 1) {
				break;
			}
			sb.append(",");
		}
		return sb.toString();
	}

	// ===========================================otherUtil===========================================
	private String arrayNodeToString(ArrayNode idList) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < idList.size(); i++) {
			sb.append(idList.get(i).asText());
			if (i == idList.size() - 1) {
				break;
			}
			sb.append(",");
		}
		return sb.toString();
	}

	private JsonNode createErrorObject(Exception e) {
		ObjectNode errorNode = Mapper.mapper.createObjectNode();
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		errorNode.put(Const.ERRORNODE, errors.toString());
		return errorNode;
	}
}
