package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the TARGETSYSTEM database table.
 * 
 */
@Entity
public class Targetsystem implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private int id;

  private String targetsystemname;

  // bi-directional many-to-one association to Application
  // @OneToMany(mappedBy="targetsystem", fetch=FetchType.EAGER)
  // private List<Application> applications;

  public Targetsystem() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTargetsystemname() {
    return this.targetsystemname;
  }

  public void setTargetsystemname(String targetsystemname) {
    this.targetsystemname = targetsystemname;
  }

  // public List<Application> getApplications() {
  // return this.applications;
  // }
  //
  // public void setApplications(List<Application> applications) {
  // this.applications = applications;
  // }
  public void updateByJsonNode(ObjectNode node) {
    if (node.has("targetsystemname")) {
      setTargetsystemname(node.get("targetsystemname").asText());
    }
  }
}
