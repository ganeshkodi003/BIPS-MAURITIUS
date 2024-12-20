package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_ChargeBack_Repo extends JpaRepository<BIPS_Charge_Back_Entity, String> {

	@Query(value = "SELECT * FROM BIPS_CHARGE_BACK_TABLE where chargeback_approval ='BANK'", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getAllList();

	@Query(value = "SELECT * FROM BIPS_CHARGE_BACK_TABLE where chargeback_approval ='BANK' AND TRUNC(tran_date) = ?1", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getAllList(String currentDate);

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where reversal_remarks ='REVERTED' AND chargeback_approval ='BANK'", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getRevertedTransactionList();

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where reversal_remarks ='REVERTED' AND chargeback_approval ='BANK' AND TRUNC(tran_date) = ?1", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getRevertedTransactionList(String currentDate);

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where reversal_remarks <>'REVERTED' AND chargeback_approval ='BANK'", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getPendingTransactionList();

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where reversal_remarks <>'REVERTED' AND chargeback_approval ='BANK' AND TRUNC(tran_date) = ?1", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getPendingTransactionList(String currentDate);

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where sequence_unique_id=?1 AND chargeback_approval ='BANK'", nativeQuery = true)
	BIPS_Charge_Back_Entity getTranfees(String sequence_unique_id);

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where reversal_remarks ='REVERTED' and TRUNC(tran_date) between ?1 and ?2 AND chargeback_approval ='BANK'", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getRevertedTransactionListBetween(String from_date, String to_date);

	@Query(value = "select * from BIPS_CHARGE_BACK_TABLE where reversal_remarks <>'REVERTED' and TRUNC(tran_date) between ?1 and ?2 AND chargeback_approval ='BANK'", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getPendingTransactionListBetween(String from_date, String to_date);

	@Query(value = "SELECT * FROM BIPS_CHARGE_BACK_TABLE where TRUNC(tran_date) between ?1 and ?2 AND chargeback_approval ='BANK'", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getAllListBetween(String from_date, String to_date);
	
	@Query(value = "SELECT * FROM BIPS_CHARGE_BACK_TABLE where tran_date between TO_DATE(?1, 'DD-MON-YY') and TO_DATE(?2, 'DD-MON-YY')", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getAllListBetweenForChecking(String from_date, String to_date);
	
	@Query(value = "SELECT * FROM BIPS_CHARGE_BACK_TABLE WHERE (merchant_id = ?3 OR ?3 IS NULL) AND (UNIT_ID = ?4 OR ?4 IS NULL) AND (USER_ID = ?5 OR ?5 IS NULL) AND (DEVICE_ID = ?6 OR ?6 IS NULL) AND (tran_date BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') OR TO_DATE(?1, 'DD-MON-YY') IS NULL OR TO_DATE(?2, 'DD-MON-YY') IS NULL)", nativeQuery = true)
	List<BIPS_Charge_Back_Entity> getTotalListBetween(String from_date, String to_date,String merchant_id,String unit_id,String user_id,String device_id);
	
	
	
	     
	 
	 
	 
	
}
