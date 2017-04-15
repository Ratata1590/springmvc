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
public class RArrayItems implements Serializable {
	private static final long serialVersionUID = 3382251698368599181L;

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "rarrayId", nullable = false)
	private RArray rarray;

	private String childType;

	private int childId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RArray getRarray() {
		return rarray;
	}

	public void setRarray(RArray rarray) {
		this.rarray = rarray;
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
