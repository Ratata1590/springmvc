package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the SYSTEMOBJECT_NEW database table.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "SYSTEMOBJECT")
public class Systemobject implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SYSTEMOBJECT_NEW_ID_GENERATOR", sequenceName = "SYSTEMOBJECT_SEQ",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SYSTEMOBJECT_NEW_ID_GENERATOR")
  private int id;

  private String systemalias;

  private String systemname;

  // // bi-directional many-to-one association to Iview
  // @JsonBackReference
  // @OneToMany(mappedBy = "systemobject")
  // private List<Iview> iviews;

  public Systemobject() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSystemalias() {
    return this.systemalias;
  }

  public void setSystemalias(String systemalias) {
    this.systemalias = systemalias;
  }

  public String getSystemname() {
    return this.systemname;
  }

  public void setSystemname(String systemname) {
    this.systemname = systemname;
  }

  // public List<Iview> getIviews() {
  // return this.iviews;
  // }
  //
  // public void setIviews(List<Iview> iviewnews) {
  // this.iviews = iviewnews;
  // }
  public void updateByJsonNode(ObjectNode node) {
    if (node.has("systemalias")) {
      setSystemalias(node.get("systemalias").asText());
    }
    if (node.has("systemname")) {
      setSystemname(node.get("systemname").asText());
    }
  }
}
