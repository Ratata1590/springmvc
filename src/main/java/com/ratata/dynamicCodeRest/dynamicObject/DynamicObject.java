package com.ratata.dynamicCodeRest.dynamicObject;

import com.ratata.dynamicCodeRest.utils.DynamicCodeUtil;

public class DynamicObject {

	public static final String SEPARATOR = ":";
	public static final String CLASS_SEPARATOR = "$";
	private Object obj;

	private String objName;

	public DynamicObject(Object obj, String objName) throws Exception {
		this.obj = obj;
		this.objName = objName;
	}

	public final DynamicObject callObjMethod(String methodName, Object... param) throws Exception {
		Object result = DynamicCodeUtil.callObjMethod(obj, methodName, param);
		if (obj.equals(result) || result == null) {
			return null;
		}
		return new DynamicObject(result, objName.concat(SEPARATOR).concat(String.valueOf(result.hashCode())));
	}

	public static final DynamicObject callClassMethod(Class<?> clazz, String methodName, Object... param)
			throws Exception {
		Object result = DynamicCodeUtil.callStaticMethod(clazz, methodName, param);
		if (result == null) {
			return null;
		}
		return new DynamicObject(result,
				clazz.getName().concat(CLASS_SEPARATOR).concat(String.valueOf(result.hashCode())));
	}

	public String getObjName() {
		return objName;
	}

	public Object getObj() {
		return obj;
	}
}
