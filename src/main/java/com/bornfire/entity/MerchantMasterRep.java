package com.bornfire.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantMasterRep extends JpaRepository<MerchantMaster, String> {
	Optional<MerchantMaster> findById(String directorId);

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_id = ?1", nativeQuery = true)
	MerchantMaster findByIdCustomvals(String merchant_acct_no);

	@Query(value = "select * from MERCHANT_MASTER_TABLE ", nativeQuery = true)
	List<MerchantMaster> findAllCustom();

	@Query(value = "select * from MERCHANT_MASTER_TABLE where del_flg='N' ", nativeQuery = true)
	List<MerchantMaster> findAllData();

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_id= ?1 and del_flg='N'", nativeQuery = true)
	MerchantMaster findByIdCustom(String Id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE  where del_flg ='N' UNION ALL select * from MERCHANT_MASTER_TABLE_MOD where entity_flg ='N' and del_flg ='N'", nativeQuery = true)
	List<MerchantMaster> ALLDATA();

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_id= ?1", nativeQuery = true)
	List<MerchantMaster> ALLDATAs(String Id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE  where entity_flg ='Y' and del_flg ='N'  and merchant_pow_ca_no like %:mpcn% and merchant_legal_id like %:mlid% and merchant_name like  %:mn% UNION ALL select * from MERCHANT_MASTER_TABLE_MOD  where entity_flg ='N' and merchant_pow_ca_no like %:mpcn% and merchant_legal_id like %:mlid% and merchant_name like  %:mn%", nativeQuery = true)
	List<MerchantMaster> ALLDATASEARCH(String mpcn, String mlid, String mn);

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_id=?1 ", nativeQuery = true)
	MerchantMaster getMerlst(String Id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_legal_id=?1 and merchant_corp_name =?2 and merchant_name=?3 and mer_user_id_r1=?4 ", nativeQuery = true)
	List<MerchantMaster> merctopas(String a, String b, String c, String d);

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_legal_id =?1", nativeQuery = true)
	MerchantMaster getMerchantPass(String merchant_legal_id);

	@Query(value = "select * from MERCHANT_MASTER_TABLE where merchant_id=?1", nativeQuery = true)
	List<MerchantMaster> merModi(String Id);

	@Query(value = "SELECT COUNT(*) AS merchant_id FROM merchant_master_table", nativeQuery = true)
	Integer getMerCount();

	@Query(value = "SELECT * FROM MERCHANT_MASTER_TABLE WHERE merchant_id=?1 AND del_flg='N' UNION ALL SELECT * FROM MERCHANT_MASTER_TABLE_MOD WHERE merchant_id=?1 AND del_flg='N'", nativeQuery = true)
	List<MerchantMaster> getBlobimage(String Id);
	
	@Query(value = "SELECT MERCHANT_ID FROM merchant_master_table WHERE ENTITY_FLG = 'Y' ORDER BY REGEXP_REPLACE(MERCHANT_ID, '[0-9]', ''),TO_NUMBER(REGEXP_SUBSTR(MERCHANT_ID, '[0-9]+')) ASC", nativeQuery = true)
	List<String> getMerchantIds();
	
	@Query(value = "SELECT MERCHANT_NAME from merchant_master_table WHERE MERCHANT_ID=?1", nativeQuery = true)
	String getMerchantName(String merchantId);
	
	@Query(
		    value = "SELECT * FROM MERCHANT_MASTER_TABLE WHERE del_flg = 'N' AND hr_holdreject_flg = 'N' " +
		            "UNION ALL " +
		            "SELECT * FROM MERCHANT_MASTER_TABLE_MOD WHERE entity_flg = 'N' AND hr_holdreject_flg = 'N'",
		    nativeQuery = true
		)
		List<MerchantMaster> findAllDataHr();


}