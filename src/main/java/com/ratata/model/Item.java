package com.ratata.model;

import java.io.Serializable;
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
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Item.class)
@Entity
@Table
public class Item implements Serializable {
	private static final long serialVersionUID = 8182035780340032132L;

	@Id
	@GeneratedValue
	private long id;

	@Column
	private String data;

	//@JsonBackReference
	@JsonIdentityReference(alwaysAsId=true)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	//@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "item", fetch = FetchType.EAGER)
	private Set<InnerItem> inneritems;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	@JsonIgnore
	public Set<InnerItem> getInneritems() {
		return inneritems;
	}

	public void setInneritems(Set<InnerItem> inneritems) {
		this.inneritems = inneritems;
	}

}
