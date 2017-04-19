package com.ratata.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

public class RBinary implements Serializable {

  private static final long serialVersionUID = -8099480854673486001L;
  @Id
  @GeneratedValue
  private Long id;

  private String hashMD5;

  @Lob
  private byte[] data;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public String getHashMD5() {
    return hashMD5;
  }

  public void setHashMD5(String hashMD5) {
    this.hashMD5 = hashMD5;
  }


}
