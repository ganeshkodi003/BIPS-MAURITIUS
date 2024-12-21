package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantMasterModRep extends JpaRepository<MerchantMasterMod, String> {
	
	@Query(value = "select * from MERCHANT_MASTER_TABLE_MOD where merchant_id= ?1 and del_flg='N'", nativeQuery = true)
	MerchantMasterMod findByIdCustomDelete(String Id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE_MOD where merchant_id = ?1", nativeQuery = true)
	MerchantMasterMod findByIdCustom(String merchant_id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE_MOD where merchant_legal_id= ?1", nativeQuery = true)
	MerchantMasterMod findByIdCustom1(String Id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE_MOD where merchant_legal_id=?1 and merchant_corp_name =?2 and merchant_name=?3 and mer_user_id_r1=?4 ", nativeQuery = true)
	List<MerchantMasterMod> merctopas(String a, String b, String c, String d);
	
	@Query(value = "SELECT COUNT(*) AS merchant_id FROM merchant_master_table_mod", nativeQuery = true)
	Integer getMerCount();
	
	@Query(value = "SELECT MAX(TO_NUMBER(numeric_part)) AS max_numeric_part FROM (SELECT REGEXP_REPLACE(MERCHANT_ID, '[^[:digit:]]', '') AS numeric_part FROM MERCHANT_MASTER_TABLE_MOD)\r\n"
			+ "", nativeQuery = true)
	String getMerID();
	
	@Query(value = "SELECT * FROM MERCHANT_MASTER_TABLE_MOD WHERE hr_status IN ('HOLD', 'REJECT') and hr_holdreject_flg='Y'", nativeQuery = true)
	List<MerchantMasterMod> holdrejectlist();
}