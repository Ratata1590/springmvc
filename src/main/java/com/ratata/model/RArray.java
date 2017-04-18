package com.ratata.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
    scope = RArray.class)
@Entity
@Table
public class RArray implements Serializable {
  private static final long serialVersionUID = 3964161752359360151L;

  @Id
  @GeneratedValue
  private Long id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "rarray", fetch = FetchType.EAGER)
  @OrderColumn(name = "rOrder")
  private List<RArrayItems> rArrayItems;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<RArrayItems> getrArrayItems() {
    return rArrayItems;
  }

  public void setrArrayItems(List<RArrayItems> rArrayItems) {
    this.rArrayItems = rArrayItems;
  }

}
