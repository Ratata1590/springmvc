package com.ratata.nativeQueryRest.pojo;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ratata.nativeQueryRest.utils.Const;
import com.ratata.nativeQueryRest.utils.Mapper;

public class NativeQueryParam {
	private String query = "";
	private String className = "";
	private ArrayNode resultSet;
	private String queryMode = "L";
	private JsonNode param;
	private Boolean isNative = true;
	private Integer lockModeType = 0;
	private Integer offset = 0;
	private Integer limit = 0;

	public NativeQueryParam(JsonNode queryObject, JsonNode param) {
		super();
		this.query = queryObject.get(Const.PARAM_QUERY).asText("");
		if (queryObject.has(Const.PARAM_QUERYMODE)) {
			this.queryMode = queryObject.get(Const.PARAM_QUERYMODE).asText();
		}
		if (queryObject.has(Const.PARAM_ISNATIVE)) {
			this.isNative = queryObject.get(Const.PARAM_ISNATIVE).asBoolean();
		}
		if (queryObject.has(Const.PARAM_RESULTSET)) {
			this.resultSet = (ArrayNode) queryObject.get(Const.PARAM_RESULTSET);
		}
		if (queryObject.has(Const.LOCKMODETYPE)) {
			this.lockModeType = queryObject.get(Const.LOCKMODETYPE).asInt();
		}
		if (queryObject.has(Const.PARAM_CLASSNAME)) {
			this.className = queryObject.get(Const.PARAM_CLASSNAME).asText();
		}
		if (queryObject.has(Const.PARAM_OFFSET)) {
			this.offset = queryObject.get(Const.PARAM_OFFSET).asInt();
		}
		if (queryObject.has(Const.PARAM_LIMIT)) {
			this.limit = queryObject.get(Const.PARAM_LIMIT).asInt();
		}
		this.param = param;
	}

	public NativeQueryParam(String query, String className, String[] resultSet, String queryMode, String param,
			Boolean isNative, Integer lockModeType, Integer offset, Integer limit) throws Exception {
		super();
		this.query = query;
		this.className = className;
		this.resultSet = (ArrayNode) Mapper.mapper.convertValue(Arrays.asList(resultSet), JsonNode.class);
		this.queryMode = queryMode;
		this.param = Mapper.mapper.readTree(param);
		this.isNative = isNative;
		this.lockModeType = lockModeType;
		this.offset = offset;
		this.limit = limit;
	}

	public NativeQueryParam() {
		super();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayNode getResultSet() {
		return resultSet;
	}

	public void setResultSet(ArrayNode resultSet) {
		this.resultSet = resultSet;
	}

	public String getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public JsonNode getParam() {
		return param;
	}

	public void setParam(JsonNode param) {
		this.param = param;
	}

	public Boolean getIsNative() {
		return isNative;
	}

	public void setIsNative(Boolean isNative) {
		this.isNative = isNative;
	}

	public Integer getLockModeType() {
		return lockModeType;
	}

	public void setLockModeType(Integer lockModeType) {
		this.lockModeType = lockModeType;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
