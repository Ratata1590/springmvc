package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class RObjectKey implements Serializable {
	private static final long serialVersionUID = 6261045500655743641L;

	@Id
	@GeneratedValue
	private int id;

	private String keyName;

	@ManyToOne
	@JoinColumn(name = "robjectId", nullable = false)
	private RObject robject;

	private String childType;

	private int childId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public RObject getObject() {
		return robject;
	}

	public void setObject(RObject robject) {
		this.robject = robject;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}

	public int getChildId() {
		return childId;
	}

	public void setChildId(int childId) {
		this.childId = childId;
	}

}
