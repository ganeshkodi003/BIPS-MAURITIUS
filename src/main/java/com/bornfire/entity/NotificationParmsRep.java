package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



	@Repository
	public interface NotificationParmsRep extends CrudRepository<NotificationParms,String>{
		
		@Query(value = "select * from NOTIFICATION_PARM_MASTER where NVL(DEL_FLG,1) <> 'Y'", nativeQuery = true)
		List<NotificationParms> customFindByAll();
		
		@Query(value = "select * from NOTIFICATION_PARM_MASTER where RECORD_SRL_NO=?1", nativeQuery = true)
		List<NotificationParms> findById1(String record_srl_no);
		
		@Query(value = "select * from NOTIFICATION_PARM_MASTER where RECORD_SRL_NO=?1 and TRAN_CATEGORY=?2", nativeQuery = true)
		List<NotificationParms> customFindById(String recordSrlNo,String tranType);
	
		@Query(value = "SELECT DISTINCT * FROM NOTIFICATION_PARM_MASTER where DEL_FLG!='Y'", nativeQuery = true)
		List<NotificationParms> getlst();
	
		@Query(value = "SELECT MAX(TO_NUMBER(numeric_part)) AS max_numeric_part FROM (SELECT REGEXP_REPLACE(RECORD_SRL_NO, '[^[:digit:]]', '') AS numeric_part FROM NOTIFICATION_PARM_MASTER)", nativeQuery = true)
		String getNotifyRef();
		
		@Query(value = "SELECT NOTIFICATION_SEQ.NEXTVAL FROM dual", nativeQuery = true)
		String getSequence();
}
