package com.ratata.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ratata.Util.UtilNativeQuery;

@RestController
public class ExportDBController {

	@PersistenceContext
	private EntityManager em;

	@RequestMapping(value = "/exportTable", method = RequestMethod.GET)
	public JsonNode exportTable(@RequestParam String entityFullName) {
		JsonNode node = UtilNativeQuery.mapper
				.convertValue(em.createQuery("Select t from " + entityFullName + " t").getResultList(), JsonNode.class);
		return node;
	}
}
