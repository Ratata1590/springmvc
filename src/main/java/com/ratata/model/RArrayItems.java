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
	private Long id;

	@ManyToOne
	@JoinColumn(name = "rarrayId", nullable = false)
	private RArray rarray;

	private Integer childType;

	private Long childId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RArray getRarray() {
		return rarray;
	}

	public void setRarray(RArray rarray) {
		this.rarray = rarray;
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
