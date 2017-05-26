package com.ratata.ObjectEndpoint.pojo;

public class MethodObject {
  private String className;
  private Object obj;
  private String methodName;
  private Object param;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Object getObj() {
    return obj;
  }

  public void setObj(Object obj) {
    this.obj = obj;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Object getParam() {
    return param;
  }

  public void setParam(Object param) {
    this.param = param;
  }
}
