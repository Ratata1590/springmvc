package com.ratata.ObjectEndpoint.pojo;

import com.fasterxml.jackson.databind.JsonNode;

public class ParamContainer {
	private Class<?>[] classTypeList;
	private Object[] objectList;

	public ParamContainer(JsonNode data) {
		super();
		this.classTypeList = new Class<?>[data.size()];
		this.objectList = new Object[data.size()];
	}

	public Class<?>[] getClassTypeList() {
		return classTypeList;
	}

	public void setClassTypeList(Class<?>[] classTypeList) {
		this.classTypeList = classTypeList;
	}

	public Object[] getObjectList() {
		return objectList;
	}

	public void setObjectList(Object[] objectList) {
		this.objectList = objectList;
	}

}
