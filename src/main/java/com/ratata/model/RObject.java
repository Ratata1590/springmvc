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
public class RObject implements Serializable {
	private static final long serialVersionUID = -7926119162913018135L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "robject")
	private Set<RObjectKey> rObjectKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<RObjectKey> getrObjectKey() {
		return rObjectKey;
	}

	public void setrObjectKey(Set<RObjectKey> rObjectKey) {
		this.rObjectKey = rObjectKey;
	}

}
