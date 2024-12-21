package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface SlabRateRepo extends JpaRepository<SlabRateEntity, String> {
	@Query(value = "select * from SLABRATES ", nativeQuery = true)
	List<SlabRateEntity> GetList();
	
}
