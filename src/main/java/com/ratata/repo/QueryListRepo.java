package com.ratata.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.model.QueryList;

public interface QueryListRepo extends JpaRepository<QueryList, Long> {

	@Query("select a from QueryList a where queryName like :queryName")
	public QueryList findQueryByKey(@Param("queryName") String queryName);
}
