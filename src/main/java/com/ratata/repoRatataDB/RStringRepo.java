package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.model.RString;

public interface RStringRepo  extends JpaRepository<RString, Long>{ 
	@Query("select a from RString a where a.data like :value")
	public RString findbyValue(@Param(value = "value") String value);
}
