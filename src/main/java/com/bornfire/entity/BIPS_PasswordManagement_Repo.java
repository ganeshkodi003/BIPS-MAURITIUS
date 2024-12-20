package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BIPS_PasswordManagement_Repo extends JpaRepository<BIPS_Password_Management_Entity, String> {

	@Query(value = "select  password FROM BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1", nativeQuery = true)
	String getPassword(String userid);
	
	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT  where merchant_rep_id=?1 and entry_flag='N'", nativeQuery = true)
	BIPS_Password_Management_Entity getbyflg(String usersid);

	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1", nativeQuery = true)
	List<BIPS_Password_Management_Entity> getpasslst(String merchant_user_id);
	
	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_rep_id=?1", nativeQuery = true)
	BIPS_Password_Management_Entity getmerchantrep(String merchant_rep_id);

	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1 ", nativeQuery = true)
	List<Object[]> getpasslsts(String MERCHANT_USER_ID);
	
	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1 AND merchant_rep_id=?2 ", nativeQuery = true)
	List<BIPS_Password_Management_Entity> getmerrep(String MERCHANT_USER_ID ,String merchant_rep_id);
	
	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1 AND unit_id=?2 ", nativeQuery = true)
	List<BIPS_Password_Management_Entity> getmerunit(String MERCHANT_USER_ID ,String merUnit);
	
	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1 ", nativeQuery = true)
	List<BIPS_Password_Management_Entity> getmersecondif(String MERCHANT_USER_ID );

	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1 and unit_id=?2 ", nativeQuery = true)
	List<BIPS_Password_Management_Entity> getPassmerId(String merchant_user_id, String unit_id);

	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_user_id=?1 ", nativeQuery = true)
	List<BIPS_Password_Management_Entity> getPassmer(String merchant_user_id);

	@Query(value = "select * from BIPS_PASSWORD_MANAGEMENT where merchant_rep_id=?1", nativeQuery = true)
	BIPS_Password_Management_Entity getRepId(String merchant_rep_id);
	
	@Query(value = "SELECT * FROM BIPS_PASSWORD_MANAGEMENT WHERE merchant_user_id = ?1", nativeQuery = true)
    List<BIPS_Password_Management_Entity> getPassList(String merchant_user_id);
    
    @Query(value = "select merchant_user_id from BIPS_PASSWORD_MANAGEMENT where merchant_rep_id=?1", nativeQuery = true)
	String getRepQR(String merchant_rep_id);

	 @Query(value="SELECT * FROM BIPS_PASSWORD_MANAGEMENT WHERE MERCHANT_REP_ID=?1 ", nativeQuery = true)
	 BIPS_Password_Management_Entity getrole(String userid);
	 
	 
	 @Query(value="SELECT * FROM BIPS_PASSWORD_MANAGEMENT WHERE unit_id=?1 ", nativeQuery = true)
	 List< BIPS_Password_Management_Entity> getunit(String unitid);
}

