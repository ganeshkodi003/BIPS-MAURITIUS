package com.bornfire.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MerchantCategoryRep extends JpaRepository<MerchantCategoryCodeEntity, String> {
	
	 Optional<MerchantCategoryCodeEntity> findById( String directorId);


		@Query(value = "select * from BIPS_MERCHANT_CATEGORY_CODE_TABLE where del_flg<>'Y' order by merchant_code asc", nativeQuery = true)
		List<MerchantCategoryCodeEntity> findAllCustom();
		
		@Query(value = "select * from BIPS_MERCHANT_CATEGORY_CODE_TABLE where merchant_code=?1", nativeQuery = true)
		MerchantCategoryCodeEntity getlist(String merchant);
		
		@Query(value = "select count(*) from BIPS_MERCHANT_CATEGORY_CODE_TABLE where merchant_code=?1", nativeQuery = true)
		String getlistcount(String merchant);
		
		@Query(value = "SELECT MAX(TO_NUMBER(numeric_part)) AS max_numeric_part FROM (SELECT REGEXP_REPLACE(MERCHANT_CODE, '[^[:digit:]]', '') AS numeric_part FROM BIPS_MERCHANT_CATEGORY_CODE_TABLE)", nativeQuery = true)
		String getMerCode(); 
		 
		 @Modifying
		 @Transactional
		 @Query(value = "UPDATE BIPS_MERCHANT_CATEGORY_CODE_TABLE SET MERCHANT_DESC = ?2 WHERE MERCHANT_CODE = ?1", nativeQuery = true)
		 void updateMerchantDescription(String merchantCode, String merchantDesc);
		 
		 @Query(value = "SELECT MERCHANT_CODE, MERCHANT_DESC FROM BIPS_MERCHANT_CATEGORY_CODE_TABLE ORDER BY MERCHANT_CODE ASC", nativeQuery = true)
		 List<Object[]> getMerCategCode();

}
