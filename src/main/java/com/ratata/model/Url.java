package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the URLNEW database table.
 * 
 */
@Entity
public class Url implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "URLNEW_ID_GENERATOR", sequenceName = "URL_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "URLNEW_ID_GENERATOR")
  private int id;

  @JsonProperty
  private String url;

  // bi-directional many-to-one association to Application
  @JsonBackReference
  @JsonIgnoreProperties
  @ManyToOne
  @JoinColumn(name = "APPID")
  private Application application;

  // bi-directional many-to-one association to Urltype
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "URLTYPE")
  private Urltype urltype;

  public Url() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Application getApplication() {
    return this.application;
  }

  public void setApplication(Application applicationnew) {
    this.application = applicationnew;
  }

  public Urltype getUrltype() {
    return this.urltype;
  }

  public void setUrltype(Urltype urltypenew) {
    this.urltype = urltypenew;
  }

  public void updateByJsonNode(ObjectNode node) {
    if (node.has("url")) {
      setUrl(node.get("url").asText());
    }
  }
}
