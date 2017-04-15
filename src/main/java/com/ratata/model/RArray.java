package com.ratata.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class RArray implements Serializable {
	private static final long serialVersionUID = 3964161752359360151L;

	@Id
	@GeneratedValue
	private int id;

	private int parentId;

	private boolean isArrayParent;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rarray")
	private Set<RArrayItems> rArrayItems;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public boolean isArrayParent() {
		return isArrayParent;
	}

	public void setArrayParent(boolean isArrayParent) {
		this.isArrayParent = isArrayParent;
	}

	public Set<RArrayItems> getrArrayItems() {
		return rArrayItems;
	}

	public void setrArrayItems(Set<RArrayItems> rArrayItems) {
		this.rArrayItems = rArrayItems;
	}

}
