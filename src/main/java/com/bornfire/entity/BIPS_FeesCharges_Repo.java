package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_FeesCharges_Repo extends JpaRepository<BIPS_Fees_Charges_Entity, String> {

	@Query(value = "SELECT DISTINCT * FROM bips_feescharge", nativeQuery = true)
	List<BIPS_Fees_Charges_Entity> getlst();
	
	@Query(value = "select * from bips_feescharge where message_ref=?1 ", nativeQuery = true)
	BIPS_Fees_Charges_Entity getfees(String message_ref);
	
}