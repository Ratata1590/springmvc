package com.ratata.repoRatataDB;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.model.RObjectKey;

public interface RObjectKeyRepo extends JpaRepository<RObjectKey, Long> {

  @Query("select a.keyName from RObjectKey a where a.robject.id = :id")
  Set<String> getAllKeyName(@Param(value = "id") Long id);

  @Query("select a from RObjectKey a where a.robject.id = :robjectId and a.keyName = :keyName")
  public RObjectKey findbyValue(@Param(value = "robjectId") Long robjectId,
      @Param(value = "keyName") String keyName);

  @Query("select a.robject.id from RObjectKey a where a.childId=:childId and a.childType=:childType")
  public List<Long> findParentObjectId(@Param(value = "childId") Long childId,
      @Param(value = "childType") Integer childType);
}
