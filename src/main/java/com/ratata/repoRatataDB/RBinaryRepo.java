package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ratata.model.RBinary;

public interface RBinaryRepo extends JpaRepository<RBinary, Long> {

  @Query("select a from RBinary a where a.hashMD5 like :hashMD5")
  public RBinary findbyhashMD5(@Param("hashMD5") String hashMD5);

}
