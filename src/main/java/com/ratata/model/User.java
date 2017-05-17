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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
    scope = User.class)
@Entity
@Table
public class User implements Serializable {

  private static final long serialVersionUID = 6541162783907686900L;

  @Id
  @GeneratedValue
  private int id;

  @Column
  private String username;

  @Version
  private int version;

  @JsonManagedReference
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
      mappedBy = "user", fetch = FetchType.EAGER)
  private Set<Item> items = new HashSet<Item>();

  public User() {
    super();
  }

  public User(String username) {
    super();
    this.username = username;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public Set<Item> getItems() {
    return items;
  }

  public void setItems(Set<Item> items) {
    this.items = items;
  }

  public void updateByJsonNode(ObjectNode node) {
    if (node.has("username")) {
      setUsername(node.get("username").asText());
    }
    if (node.has("version")) {
      setVersion(node.get("version").asInt());
    }
  }
}
