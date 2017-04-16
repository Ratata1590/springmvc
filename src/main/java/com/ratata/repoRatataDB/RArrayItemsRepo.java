package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.model.RArrayItems;

public interface RArrayItemsRepo extends JpaRepository<RArrayItems, Long> {
	@Query("select a from RArrayItems a where a.rarray.id = :rarrayId and a.childType = :childType and a.childId = :childId")
	public RArrayItems findbyValue(@Param(value = "rarrayId") Long rarrayId,
			@Param(value = "childType") Integer childType, @Param(value = "childId") Long childId);
	
	
}
