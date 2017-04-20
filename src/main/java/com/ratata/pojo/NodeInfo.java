package com.ratata.pojo;

public class NodeInfo {
  private Long id;
  private Integer type;

  public NodeInfo() {
    super();
  }

  public NodeInfo(Long id, Integer type) {
    super();
    this.id = id;
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }


}
