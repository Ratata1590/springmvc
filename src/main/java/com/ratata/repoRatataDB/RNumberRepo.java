package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.model.RNumber;

public interface RNumberRepo extends JpaRepository<RNumber, Long> {
	@Query("select a from RNumber a where a.data = :value")
	public RNumber findbyValue(@Param(value = "value") Double value);
}
