package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the IVIEWTYPE database table.
 * 
 */
@Entity
public class Iviewtype implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "IVIEWTYPE_ID_GENERATOR", sequenceName = "IVIEWTYPE_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IVIEWTYPE_ID_GENERATOR")
  private int id;

  private String description;

  private String typename;

  public Iviewtype() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTypename() {
    return this.typename;
  }

  public void setTypename(String typename) {
    this.typename = typename;
  }

  public void updateByJsonNode(ObjectNode node) {
    if (node.has("description")) {
      setDescription(node.get("description").asText());
    }
    if (node.has("typename")) {
      setTypename(node.get("typename").asText());
    }
  }
}
