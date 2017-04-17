package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class RObjectKey implements Serializable {
  private static final long serialVersionUID = 6261045500655743641L;

  @Id
  @GeneratedValue
  private Long id;

  private String keyName;

  @ManyToOne
  @JoinColumn(name = "robjectId", nullable = false)
  private RObject robject;

  private Integer childType;

  private Long childId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getKeyName() {
    return keyName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  @JsonIgnore
  public RObject getObject() {
    return robject;
  }

  public void setObject(RObject robject) {
    this.robject = robject;
  }

  public Integer getChildType() {
    return childType;
  }

  public void setChildType(Integer childType) {
    this.childType = childType;
  }

  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

}
