package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the IVIEWNEW database table.
 * 
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
    scope = Iview.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Iview implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "IVIEWNEW_ID_GENERATOR", sequenceName = "IVIEW_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IVIEWNEW_ID_GENERATOR")
  private int id;

  private String fioricategory;

  private String fioriremotecatalogue;

  private String iviewname;

  private String lastmodifiedby;

  private String lastmodifieddate;

  private String pcdpath;

  // bi-directional many-to-one association to Application
  @JsonBackReference
  // @JsonIgnoreProperties
  @ManyToOne
  @JoinColumn(name = "APPID")
  private Application application;

  // bi-directional many-to-one association to Systemobject
  @ManyToOne
  @JoinColumn(name = "SYSTEMOBJECT")
  private Systemobject systemobject;

  // bi-directional many-to-one association to Iviewtype
  @ManyToOne
  @JoinColumn(name = "IVIEWTYPEID")
  private Iviewtype iviewtype;

  public Iview() {}


  public int getId() {
    return id;
  }


  public void setId(int id) {
    this.id = id;
  }


  public String getFioricategory() {
    return fioricategory;
  }


  public void setFioricategory(String fioricategory) {
    this.fioricategory = fioricategory;
  }


  public String getFioriremotecatalogue() {
    return fioriremotecatalogue;
  }


  public void setFioriremotecatalogue(String fioriremotecatalogue) {
    this.fioriremotecatalogue = fioriremotecatalogue;
  }


  public String getIviewname() {
    return iviewname;
  }


  public void setIviewname(String iviewname) {
    this.iviewname = iviewname;
  }


  public String getLastmodifiedby() {
    return lastmodifiedby;
  }


  public void setLastmodifiedby(String lastmodifiedby) {
    this.lastmodifiedby = lastmodifiedby;
  }


  public String getLastmodifieddate() {
    return lastmodifieddate;
  }


  public void setLastmodifieddate(String lastmodifieddate) {
    this.lastmodifieddate = lastmodifieddate;
  }


  public String getPcdpath() {
    return pcdpath;
  }


  public void setPcdpath(String pcdpath) {
    this.pcdpath = pcdpath;
  }


  public Application getApplication() {
    return application;
  }


  public void setApplication(Application application) {
    this.application = application;
  }


  public Systemobject getSystemobject() {
    return systemobject;
  }


  public void setSystemobject(Systemobject systemobject) {
    this.systemobject = systemobject;
  }


  public Iviewtype getIviewtype() {
    return iviewtype;
  }


  public void setIviewtype(Iviewtype iviewtype) {
    this.iviewtype = iviewtype;
  }


  public void updateByJsonNode(ObjectNode node) {
    if (node.has("fioricategory")) {
      setFioricategory(node.get("fioricategory").asText());
    }
    if (node.has("fioriremotecatalogue")) {
      setFioriremotecatalogue(node.get("fioriremotecatalogue").asText());
    }
    if (node.has("iviewname")) {
      setIviewname(node.get("iviewname").asText());
    }
    if (node.has("lastmodifiedby")) {
      setLastmodifiedby(node.get("lastmodifiedby").asText());
    }
    if (node.has("lastmodifieddate")) {
      setLastmodifieddate(node.get("lastmodifieddate").asText());
    }
    if (node.has("pcdpath")) {
      setPcdpath(node.get("pcdpath").asText());
    }
  }
}
