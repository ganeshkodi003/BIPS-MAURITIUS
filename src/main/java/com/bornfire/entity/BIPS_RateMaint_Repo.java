package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_RateMaint_Repo extends JpaRepository<BIPS_Rate_Maint_Entity, String> {

	@Query(value = "SELECT DISTINCT * FROM bips_ratemaint", nativeQuery = true)
	List<BIPS_Rate_Maint_Entity> getlst();
	
	@Query(value = "select * from bips_ratemaint where srl=?1 ", nativeQuery = true)
	BIPS_Rate_Maint_Entity getDevlst(String srl);
	
	@Query(value = "select CIMBIPS.SRL.NEXTVAL FROM dual", nativeQuery = true)
	String SRL();
	
}