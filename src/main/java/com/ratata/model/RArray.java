package com.ratata.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.Util.UtilNativeQuery;

@Entity
@Table
public class RArray implements Serializable {
	private static final long serialVersionUID = 3964161752359360151L;

	@Id
	@GeneratedValue
	private Long id;

	// array sequence map
	private String sequenceMap;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "rarray")
	private Set<RArrayItems> rArrayItems;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<RArrayItems> getrArrayItems() {
		return rArrayItems;
	}

	public void setrArrayItems(Set<RArrayItems> rArrayItems) {
		this.rArrayItems = rArrayItems;
	}

	public LinkedList<Long> getSequenceMap() throws Exception {
		JsonNode sequenceMapString = UtilNativeQuery.mapper.valueToTree(sequenceMap);
		LinkedList<Long> sequenceMap = new LinkedList<Long>();
		for (Integer i = 0; i < sequenceMapString.size(); i++) {
			sequenceMap.addLast(sequenceMapString.get(i.intValue()).get(1).asLong());
		}
		return sequenceMap;
	}

	public void setSequenceMap(LinkedList<RArrayItems> sequenceMap) throws Exception {
		Map<String, LinkedList<Object>> sequenceMapString = new HashMap<String, LinkedList<Object>>();
		for (Integer i = 0; i < sequenceMap.size(); i++) {
			LinkedList<Object> typeAndId = new LinkedList<Object>();
			typeAndId.addLast(sequenceMap.get(i).getChildType());
			typeAndId.addLast(sequenceMap.get(i).getId());
			sequenceMapString.put(i.toString(), typeAndId);
		}
		this.sequenceMap = UtilNativeQuery.mapper.writeValueAsString(sequenceMapString);
	}

}
