package com.ratata.nativeQueryRest.controller;

public class ObjectContainer {
	private Object obj;

	private String objType;

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.objType = obj.getClass().getTypeName();
		this.obj = obj;
	}

	public Object convertNumber(String type, String method) throws Exception {
		return Class.forName(type).getMethod(method, Class.forName(objType)).invoke(null, obj);
	}
}
