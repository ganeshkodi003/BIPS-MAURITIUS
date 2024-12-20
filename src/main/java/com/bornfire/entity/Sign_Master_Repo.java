package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Sign_Master_Repo  extends JpaRepository<Sign_Master_Entity, BigDecimal> {
	
	@Query(value = "SELECT BIPS_SIGNATURE_SEQ.NEXTVAL FROM dual", nativeQuery = true)
	BigDecimal getSequence();
	
	@Query(value = "select * from BIPS_SIGN_MASTER where merchant_id = ?1", nativeQuery = true)
	List<Sign_Master_Entity> findByref_no(String appl_ref_no);
	
	@Query(value = "select * from BIPS_SIGN_MASTER where merchant_id=?1", nativeQuery = true)
	Sign_Master_Entity getMerId(String merchant_id);
}
