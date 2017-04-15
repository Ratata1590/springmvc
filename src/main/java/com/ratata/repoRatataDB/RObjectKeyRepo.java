package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ratata.model.RObjectKey;

public interface RObjectKeyRepo  extends JpaRepository<RObjectKey, Long> {

}
