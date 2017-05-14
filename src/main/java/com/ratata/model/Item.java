package com.ratata.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table
public class Item implements Serializable {
	private static final long serialVersionUID = 8182035780340032132L;

	@Id
	@GeneratedValue
	private int id;

	@Column
	private String data;

	@JsonBackReference
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userId")
	private User user;

	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "item", fetch = FetchType.EAGER)
	private Set<InnerItem> inneritems = new HashSet<InnerItem>();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Set<InnerItem> getInneritems() {
		return inneritems;
	}

	public void setInneritems(Set<InnerItem> inneritems) {
		this.inneritems = inneritems;
	}

}
