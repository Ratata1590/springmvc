package com.ratata.repoRatataDB;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ratata.model.RString;

public interface RStringRepo  extends JpaRepository<RString, Long>{ 

}
