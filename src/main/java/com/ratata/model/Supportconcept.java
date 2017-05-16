package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the SUPPORTCONCEPTNEW database table.
 * 
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
    scope = Supportconcept.class)
@Entity
public class Supportconcept implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SUPPORTCONCEPTNEW_ID_GENERATOR", sequenceName = "SUPPORTCONCEPT_SEQ",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUPPORTCONCEPTNEW_ID_GENERATOR")
  private int id;

  private String firstlevel;

  private String responsiblentid;

  private String responsiblesince;

  private String secondlevel;

  private String supportconcepturl;

  private String thirdlevel;

  private String womgroup;

  // //bi-directional many-to-one association to Applicationnew
  // @JsonBackReference
  // @JsonIgnoreProperties
  // @OneToMany(mappedBy="supportconcept")
  // private List<Application> applications;

  public Supportconcept() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstlevel() {
    return this.firstlevel;
  }

  public void setFirstlevel(String firstlevel) {
    this.firstlevel = firstlevel;
  }

  public String getResponsiblentid() {
    return this.responsiblentid;
  }

  public void setResponsiblentid(String responsiblentid) {
    this.responsiblentid = responsiblentid;
  }

  public String getResponsiblesince() {
    return this.responsiblesince;
  }

  public void setResponsiblesince(String responsiblesince) {
    this.responsiblesince = responsiblesince;
  }

  public String getSecondlevel() {
    return this.secondlevel;
  }

  public void setSecondlevel(String secondlevel) {
    this.secondlevel = secondlevel;
  }

  public String getSupportconcepturl() {
    return this.supportconcepturl;
  }

  public void setSupportconcepturl(String supportconcepturl) {
    this.supportconcepturl = supportconcepturl;
  }

  public String getThirdlevel() {
    return this.thirdlevel;
  }

  public void setThirdlevel(String thirdlevel) {
    this.thirdlevel = thirdlevel;
  }

  public String getWomgroup() {
    return this.womgroup;
  }

  public void setWomgroup(String womgroup) {
    this.womgroup = womgroup;
  }

  // public List<Application> getApplications() {
  // return this.applications;
  // }
  //
  // public void setApplications(List<Application> applicationnews) {
  // this.applications = applicationnews;
  // }
  public void updateByJsonNode(ObjectNode node) {
    if (node.has("firstlevel")) {
      setFirstlevel(node.get("firstlevel").asText());
    }
    if (node.has("responsiblentid")) {
      setResponsiblentid(node.get("responsiblentid").asText());
    }
    if (node.has("responsiblesince")) {
      setResponsiblesince(node.get("responsiblesince").asText());
    }
    if (node.has("secondlevel")) {
      setSecondlevel(node.get("secondlevel").asText());
    }
    if (node.has("supportconcepturl")) {
      setSupportconcepturl(node.get("supportconcepturl").asText());
    }
    if (node.has("thirdlevel")) {
      setThirdlevel(node.get("thirdlevel").asText());
    }
    if (node.has("womgroup")) {
      setWomgroup(node.get("womgroup").asText());
    }
  }
}
