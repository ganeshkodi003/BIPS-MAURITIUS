package com.bornfire.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_OutwardTransMonitoring_Repo extends JpaRepository<BIPS_Outward_Trans_Monitoring_Entity, String> {

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE tran_status = 'SUCCESS'", nativeQuery = true)
	Integer getTranSuccessCount();

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE tran_status = 'FAILURE'", nativeQuery = true)
	Integer getTransFailureCount();

	@Query(value = "SELECT CASE WHEN SUM(tran_amount) IS NULL THEN 0 else SUM(tran_amount) END FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE", nativeQuery = true)
	Integer getamtTrans();

	@Query(value = "select * from bips_outward_transaction_monitoring_table where sequence_unique_id=?1 ", nativeQuery = true)
	BIPS_Outward_Trans_Monitoring_Entity getTranfees(String sequence_unique_id);

	@Query(value = "select * from bips_outward_transaction_monitoring_table where merchant_id=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranlst(String merchant_id);

	@Query(value = "select * from bips_outward_transaction_monitoring_table where user_id=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getmainTranlst(String user_id);

	@Query(value = "SELECT * FROM bips_outward_transaction_monitoring_table WHERE TRUNC(tran_date) =?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranDevlst(String currentDate);

	@Query(value = "SELECT * FROM bips_outward_transaction_hist_monitoring_table WHERE TRUNC(tran_date) =?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranDevlstHist(String valueDate);

	@Query(value = "select * from bips_outward_transaction_monitoring_table where merchant_id=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranDevlst1(String merchant_id);

	@Query(value = "select * from bips_outward_transaction_monitoring_table where device_id=?1 and merchant_id=?2", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> findByDeviceIdAndMerchantId(String device_id, String merchant_id);

	@Query(value = "select count(*) from bips_outward_transaction_monitoring_table where merchant_id=?1", nativeQuery = true)
	Integer getTranscount(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_status = 'SUCCESS'", nativeQuery = true)
	Integer getTranssuccess(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_status = 'FAILURE'", nativeQuery = true)
	Integer getTransfailure(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_status = 'INITIATED'", nativeQuery = true)
	Integer getTransinitiated(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE tran_status = 'SUCCESS'", nativeQuery = true)
	Integer getTranssuccess1(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE tran_status = 'FAILURE'", nativeQuery = true)
	Integer getTransfailure1(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE  tran_status = 'INITIATED'", nativeQuery = true)
	Integer getTransinitiated1(String merchant_id);

	@Query(value = "select * from bips_outward_transaction_monitoring_table where TRUNC(tran_date) = ?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getAllRecord(Date tran_date);

	@Query(value = "select * from bips_outward_transaction_monitoring_table where TRUNC(tran_date) = ?1 and merchant_id=?2", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getAllRecord1(Date tran_date, String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') ", nativeQuery = true)
	Integer getDailyTrans(Date tran_date);

	@Query(value = "SELECT COUNT(*) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') AND merchant_id =?1 ", nativeQuery = true)
	Integer getDailyTransMer(String merchant_id);

	@Query(value = "SELECT SUM(tran_amount) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') ", nativeQuery = true)
	Integer getTransactionCounts(Date tran_date);

	@Query(value = "SELECT SUM(tran_amount) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') AND merchant_id =?1 ", nativeQuery = true)
	Integer getTransactionCountMer(String merchant_id);

	@Query(value = "select  DISTINCT device_id FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getdeviceId(String merchant_id1);

	@Query(value = "select  DISTINCT unit_id FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getpartUnitId(String merchant_id1);

	@Query(value = "select  DISTINCT user_id FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getuserid(String merchant_id1);

	@Query(value = "SELECT * FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND TRUNC(tran_date) BETWEEN ?2 AND ?3  AND unit_id = ?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getFromToDate(String merUserId, Date from_date, Date to_date,
			String unit_id);

	@Query(value = "SELECT * FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND TRUNC(tran_date) BETWEEN ?2 AND ?3  AND device_id = ?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getFromToDateDev(String merUserId, Date from_date, Date to_date,
			String device_id);

	@Query(value = "SELECT * FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND TRUNC(tran_date) BETWEEN ?2 AND ?3  AND user_id = ?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getFromToDateUser(String merUserId, Date from_date, Date to_date,
			String user_id);

	@Query(value = "SELECT cim_account from BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getAccNum(String merchant_id1);

	@Query(value = "SELECT (SELECT count(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE) + (SELECT count(*) FROM BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE) FROM dual", nativeQuery = true)
	int getTotalTransaction();
	
	@Query(value = "SELECT count(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE", nativeQuery = true)
	int getTodayTotalTransaction();

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY')", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getBetweenList(String from_date, String to_date);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetails(String fromdate, String todate, String merchantId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND UNIT_ID=?4 AND USER_ID=?5 AND DEVICE_ID=?6", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsAll(String fromdate, String todate,
			String merchantId, String unitId, String userId, String deviceId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND UNIT_ID=?4 AND USER_ID=?5", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsAllUU(String fromdate, String todate,
			String merchantId, String unitId, String userId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND UNIT_ID=?4 AND DEVICE_ID=?5", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsAllUD(String fromdate, String todate,
			String merchantId, String unitId, String deviceId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND USER_ID=?4 AND DEVICE_ID=?5", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsAllUsD(String fromdate, String todate,
			String merchantId, String userId, String deviceId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND UNIT_ID=?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsUnit(String fromdate, String todate,
			String merchantId, String unitId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND USER_ID=?4 ", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsUser(String fromdate, String todate,
			String merchantId, String userId);

	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table)WHERE TRUNC(TRAN_DATE) BETWEEN TO_DATE(?1, 'DD-MON-YY') AND TO_DATE(?2, 'DD-MON-YY') AND merchant_id=?3 AND DEVICE_ID=?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionDetailsDevice(String fromdate, String todate,
			String merchantId, String deviceId);
	
	@Query(value = "SELECT * FROM (SELECT * FROM bips_outward_transaction_monitoring_table UNION ALL SELECT * FROM bips_outward_transaction_hist_monitoring_table) WHERE TRAN_AUDIT_NUMBER = ?1", nativeQuery = true)
	BIPS_Outward_Trans_Monitoring_Entity getSingleRecord(String auditRef);
}
