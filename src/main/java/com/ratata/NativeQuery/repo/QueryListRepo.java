package com.ratata.NativeQuery.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.NativeQuery.entity.QueryList;

public interface QueryListRepo extends JpaRepository<QueryList, Long> {

	@Query("select a from QueryList a where queryName like :queryName")
	public QueryList findQueryByKey(@Param("queryName") String queryName);

	@Query("select a.queryName from QueryList a")
	public List<String> getAllqueryName();

	@Modifying
	@Query("delete from QueryList a where a.queryName like :queryName")
	public Integer deleteQueryByqueryName(@Param("queryName") String queryName);
}
