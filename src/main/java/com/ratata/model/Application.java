package com.ratata.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the APPLICATIONNEW database table.
 * 
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
    scope = Application.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table
public class Application implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "APPLICATIONNEW_ID_GENERATOR", sequenceName = "APPLICATION_SEQ",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPLICATIONNEW_ID_GENERATOR")
  private int id;

  private String appname;

  private String comments;

  private String criticalityboolean;

  @Column(name = "\"DATABASE\"")
  private String database;

  private String description;

  private String documentationurl;

  private String integratorntid;

  private String lastmodifiedby;

  private String lastmodifieddate;

  private String navigationpath;

  private String otherdependency;

  private String pcdpathfolder;

  private String pcdrolenames;

  private String publishingstate;

  private String targetgroup;

  private String technicalusers;

  private String wpdgroups;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
  @JoinColumn(name = "SUPPORTCONCEPTID")
  private Supportconcept supportconcept;

  @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
  private Set<Iview> iviews;

  // bi-directional many-to-one association to Url
  @OneToMany(mappedBy = "application", fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
  @Fetch(value = FetchMode.SUBSELECT)
  private Set<Url> urls;

  // bi-directional many-to-one association to Targetsystem
  @ManyToOne
  @JoinColumn(name = "TARGETSYSTEMID")
  private Targetsystem targetsystem;

  public Application() {
    super();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAppname() {
    return appname;
  }

  public void setAppname(String appname) {
    this.appname = appname;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getCriticalityboolean() {
    return criticalityboolean;
  }

  public void setCriticalityboolean(String criticalityboolean) {
    this.criticalityboolean = criticalityboolean;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDocumentationurl() {
    return documentationurl;
  }

  public void setDocumentationurl(String documentationurl) {
    this.documentationurl = documentationurl;
  }

  public String getIntegratorntid() {
    return integratorntid;
  }

  public void setIntegratorntid(String integratorntid) {
    this.integratorntid = integratorntid;
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

  public String getNavigationpath() {
    return navigationpath;
  }

  public void setNavigationpath(String navigationpath) {
    this.navigationpath = navigationpath;
  }

  public String getOtherdependency() {
    return otherdependency;
  }

  public void setOtherdependency(String otherdependency) {
    this.otherdependency = otherdependency;
  }

  public String getPcdpathfolder() {
    return pcdpathfolder;
  }

  public void setPcdpathfolder(String pcdpathfolder) {
    this.pcdpathfolder = pcdpathfolder;
  }

  public String getPcdrolenames() {
    return pcdrolenames;
  }

  public void setPcdrolenames(String pcdrolenames) {
    this.pcdrolenames = pcdrolenames;
  }

  public String getPublishingstate() {
    return publishingstate;
  }

  public void setPublishingstate(String publishingstate) {
    this.publishingstate = publishingstate;
  }

  public String getTargetgroup() {
    return targetgroup;
  }

  public void setTargetgroup(String targetgroup) {
    this.targetgroup = targetgroup;
  }

  public String getTechnicalusers() {
    return technicalusers;
  }

  public void setTechnicalusers(String technicalusers) {
    this.technicalusers = technicalusers;
  }

  public String getWpdgroups() {
    return wpdgroups;
  }

  public void setWpdgroups(String wpdgroups) {
    this.wpdgroups = wpdgroups;
  }

  public Supportconcept getSupportconcept() {
    return supportconcept;
  }

  public void setSupportconcept(Supportconcept supportconcept) {
    this.supportconcept = supportconcept;
  }

  public Set<Iview> getIviews() {
    return iviews;
  }

  public void setIviews(Set<Iview> iviews) {
    this.iviews = iviews;
  }

  public Set<Url> getUrls() {
    return urls;
  }

  public void setUrls(Set<Url> urls) {
    this.urls = urls;
  }

  public Targetsystem getTargetsystem() {
    return targetsystem;
  }

  public void setTargetsystem(Targetsystem targetsystem) {
    this.targetsystem = targetsystem;
  }

  public void updateByJsonNode(ObjectNode node) {
    if (node.has("appname")) {
      setAppname(node.get("appname").asText());
    }
    if (node.has("comments")) {
      setComments(node.get("comments").asText());
    }
    if (node.has("criticalityboolean")) {
      setCriticalityboolean(node.get("criticalityboolean").asText());
    }
    if (node.has("database")) {
      setDatabase(node.get("database").asText());
    }
    if (node.has("description")) {
      setDescription(node.get("description").asText());
    }
    if (node.has("documentationurl")) {
      setDocumentationurl(node.get("documentationurl").asText());
    }
    if (node.has("integratorntid")) {
      setIntegratorntid(node.get("integratorntid").asText());
    }
    if (node.has("lastmodifiedby")) {
      setLastmodifiedby(node.get("lastmodifiedby").asText());
    }
    if (node.has("lastmodifieddate")) {
      setLastmodifieddate(node.get("lastmodifieddate").asText());
    }
    if (node.has("navigationpath")) {
      setNavigationpath(node.get("navigationpath").asText());
    }
    if (node.has("otherdependency")) {
      setOtherdependency(node.get("otherdependency").asText());
    }
    if (node.has("pcdpathfolder")) {
      setPcdpathfolder(node.get("pcdpathfolder").asText());
    }
    if (node.has("pcdrolenames")) {
      setPcdrolenames(node.get("pcdrolenames").asText());
    }
    if (node.has("publishingstate")) {
      setPublishingstate(node.get("publishingstate").asText());
    }
    if (node.has("targetgroup")) {
      setTargetgroup(node.get("targetgroup").asText());
    }
    if (node.has("technicalusers")) {
      setTechnicalusers(node.get("technicalusers").asText());
    }
    if (node.has("wpdgroups")) {
      setWpdgroups(node.get("wpdgroups").asText());
    }

  }
}
