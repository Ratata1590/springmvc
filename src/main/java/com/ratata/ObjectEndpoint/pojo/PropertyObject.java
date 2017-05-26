package com.ratata.ObjectEndpoint.pojo;

public class PropertyObject {
  private String className;
  private Object obj;
  private String propertyName;
  private Boolean get;
  private Object value;

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

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public Boolean getGet() {
    return get;
  }

  public void setGet(Boolean get) {
    this.get = get;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
