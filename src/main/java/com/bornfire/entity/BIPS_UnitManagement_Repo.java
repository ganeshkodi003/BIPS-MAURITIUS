package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_UnitManagement_Repo extends JpaRepository<BIPS_Unit_Mangement_Entity, String> {

	@Query(value = "select * from BIPS_MERCHANT_UNIT_MANAGEMENT where unit_id=?1 and del_flg = 'N'", nativeQuery = true)
	List<BIPS_Unit_Mangement_Entity> merctopas(String a);

	@Query(value = "select * from BIPS_MERCHANT_UNIT_MANAGEMENT where unit_id=?1 and del_flg = 'N'", nativeQuery = true)
	BIPS_Unit_Mangement_Entity getUnitId(String unit_id);

	@Query(value = "select * from BIPS_MERCHANT_UNIT_MANAGEMENT where merchant_user_id=?1 and del_flg = 'N'", nativeQuery = true)
	List<BIPS_Unit_Mangement_Entity> getUnitlist(String merchant_user_id);

	@Query(value = "select * from BIPS_MERCHANT_UNIT_MANAGEMENT where  merchant_user_id=?1 and del_flg = 'N'", nativeQuery = true)
	BIPS_Unit_Mangement_Entity findByIdCustoms(String a);

	@Query(value = "select * from BIPS_MERCHANT_UNIT_MANAGEMENT where merchant_user_id=?1 AND unit_id=?2 and del_flg = 'N'", nativeQuery = true)
	List<BIPS_Unit_Mangement_Entity> getUnitId(String merchant_user_id, String unit_id);

	@Query(value = "select * from BIPS_Unit_Mangement_Entity where merchant_user_id=?1 AND merchant_rep_id=?2 and del_flg = 'N'", nativeQuery = true)
	List<BIPS_Unit_Mangement_Entity> getmerrep(String MERCHANT_USER_ID, String merchant_rep_id);

	@Query(value = "select DISTINCT unit_id from BIPS_MERCHANT_UNIT_MANAGEMENT where merchant_user_id =?1 and del_flg = 'N'", nativeQuery = true)
	List<String> getpartUnitId(String merchant_id1);

	@Query(value = "select DISTINCT unit_name from BIPS_MERCHANT_UNIT_MANAGEMENT where merchant_user_id =?1 and del_flg = 'N'", nativeQuery = true)
	List<String> getpartUnitName(String merchant_id1);

	@Query(value = "select DISTINCT unit_type from BIPS_MERCHANT_UNIT_MANAGEMENT where merchant_user_id =?1 and del_flg = 'N'", nativeQuery = true)
	List<String> getpartUnitType(String merchant_id1);

	@Query(value = "select UNIT_NAME,UNIT_TYPE from BIPS_MERCHANT_UNIT_MANAGEMENT where UNIT_ID =?1 and del_flg = 'N'", nativeQuery = true)
	Object[] getUnitDetail(String unitId);

	@Query(value = "select * from BIPS_MERCHANT_UNIT_MANAGEMENT  where merchant_user_id =?1", nativeQuery = true)
	List<BIPS_Unit_Mangement_Entity> UnitId(String merchant_user_id);

}
