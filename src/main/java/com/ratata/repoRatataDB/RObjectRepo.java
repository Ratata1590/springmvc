package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ratata.model.RObject;

public interface RObjectRepo extends JpaRepository<RObject, Long> {
}
