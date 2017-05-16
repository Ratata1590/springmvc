package com.ratata.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Entity
public class ServerConfig {

  @Id
  @SequenceGenerator(name = "SERVERCONFIG_ID_GENERATOR", sequenceName = "SERVERCONFIG_SEQ",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVERCONFIG_ID_GENERATOR")
  private int id;

  @Column(unique = true)
  private String key;

  private String value;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void updateByJsonNode(ObjectNode node) {
    if (node.has("key")) {
      setKey(node.get("key").asText());
    }
    if (node.has("value")) {
      setValue(node.get("value").asText());
    }
  }
}
