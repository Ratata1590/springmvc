package com.ratata.dynamicCodeRest.dynamicObject;

public class DynamicObject {

	public static final String SEPARATOR = ":";
	private Object obj;

	private String objName;

	public DynamicObject(Object obj, String objName) throws Exception {
		this.obj = obj;
		this.objName = objName;
	}

	public final DynamicObject callMethod(String methodName, Object... param) throws Exception {
		Object result;
		if (param != null) {
			result = obj.getClass().getMethod(methodName, revolseObjectParamType(param)).invoke(obj, param);
		} else {
			result = obj.getClass().getMethod(methodName).invoke(obj);
		}
		if (obj.equals(result) || result == null) {
			return null;
		}
		return new DynamicObject(result, objName.concat(SEPARATOR).concat(String.valueOf(result.hashCode())));
	}

	public final Class<?>[] revolseObjectParamType(Object... param) {
		Class<?>[] classTypeList = new Class<?>[param.length];
		for (int i = 0; i < param.length; i++) {
			classTypeList[i] = param[i].getClass();
		}
		return classTypeList;
	}

	public Object getObjProp(String propName) throws Exception {
		return obj.getClass().getDeclaredField(propName).get(obj);
	}

	public void setObjProp(String propName, Object value) throws Exception {
		obj.getClass().getDeclaredField(propName).set(obj, value);
	}

	public String getObjName() {
		return objName;
	}

	public Object getObj() {
		return obj;
	}
}
