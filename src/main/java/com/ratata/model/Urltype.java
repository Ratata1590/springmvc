package com.ratata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The persistent class for the URLTYPENEW database table.
 * 
 */
@Entity
public class Urltype implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "URLTYPENEW_ID_GENERATOR", sequenceName = "URLTYPE_SEQ",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "URLTYPENEW_ID_GENERATOR")
  private int id;

  @JsonProperty
  private String urltypename;

  // //bi-directional many-to-one association to Url
  // @JsonBackReference
  // @JsonIgnore
  // @JsonIgnoreProperties
  // @OneToMany(mappedBy="urltype")
  // private List<Url> urls;

  public Urltype() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUrltypename() {
    return this.urltypename;
  }

  public void setUrltypename(String urltypename) {
    this.urltypename = urltypename;
  }

  // public List<Url> getUrls() {
  // return this.urls;
  // }
  //
  // public void setUrls(List<Url> urls) {
  // this.urls = urls;
  // }
  public void updateByJsonNode(ObjectNode node) {
    if (node.has("urltypename")) {
      setUrltypename(node.get("urltypename").asText());
    }
  }
}
