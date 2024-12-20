

package com.bornfire.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MerchantChargesAndFeesRepositry extends JpaRepository<MerchantChargesAndFees, String> {
	
	 Optional<MerchantChargesAndFees> findById(String directorId);


	@Query(value = "select * from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE where entity_flg='Y' and del_flg='N' and description is not null union all select * from BIPS_MERCHANT_CHARGES_AND_FEES_MOD_TABLE where del_flg='N'", nativeQuery = true)
	List<MerchantChargesAndFees> findAllCustom();
	
	
	@Query(value = "select * from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE where reference_number= ?1 ", nativeQuery = true)
	MerchantChargesAndFees findByIdReferenceNum(String Id);

	@Query(value = "select * from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE where del_flg='N' and description = ?1", nativeQuery = true)
	MerchantChargesAndFees findByFeeDesc(String desc);
	
	@Query(value = "select * from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE where criteria='VAT'", nativeQuery = true)
	List<MerchantChargesAndFees> findAllVatfeesdata();
	
	@Query(value = "SELECT MAX(TO_NUMBER(numeric_part)) AS max_numeric_part FROM (SELECT REGEXP_REPLACE(REFERENCE_NUMBER, '[^[:digit:]]', '') AS numeric_part FROM BIPS_MERCHANT_CHARGES_AND_FEES_TABLE UNION ALL SELECT REGEXP_REPLACE(REFERENCE_NUMBER, '[^[:digit:]]', '') AS numeric_part FROM BIPS_MERCHANT_CHARGES_AND_FEES_MOD_TABLE)", nativeQuery = true)
	String getMerchantRef();
	
	@Query(value = "select * from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE where entity_flg='Y' and del_flg='N' and description is not null", nativeQuery = true)
	List<MerchantChargesAndFees> findAllCustomNew();
	
	@Query(value = "select distinct DESCRIPTION from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE where DESCRIPTION is not null", nativeQuery = true)
	List<String> findAlldescval();
	
	@Query(value = "select * from ( select * from BIPS_MERCHANT_CHARGES_AND_FEES_TABLE  union all select * from BIPS_MERCHANT_CHARGES_AND_FEES_MOD_TABLE ) where del_flg = 'N' and description = ?1 ", nativeQuery = true)
	MerchantChargesAndFees findByDescription(String desc);

}
