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

  public final Object callObjMethod(String methodName, Object... param) throws Exception {
    return DynamicCodeUtil.callObjMethod(obj, methodName, param);
  }

  public static final Object callClassMethod(Class<?> clazz, String methodName, Object... param)
      throws Exception {
    return DynamicCodeUtil.callStaticMethod(clazz, methodName, param);
  }

  public String getObjName() {
    return objName;
  }

  public Object getObj() {
    return obj;
  }
}
