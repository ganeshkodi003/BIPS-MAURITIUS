package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_Service_ReqMonitoring_Repo extends JpaRepository<BIPS_Service_ReqMonitoring_Entity, String> {
	
	@Query(value = "select * from SERVICE_REQUEST_MONITORING where status= 'APPROVED'", nativeQuery = true)
	List<BIPS_Service_ReqMonitoring_Entity> getApprovedList();
	
	@Query(value = "select * from SERVICE_REQUEST_MONITORING where status= 'PENDING'", nativeQuery = true)
	List<BIPS_Service_ReqMonitoring_Entity> getPendingList();
	
	@Query(value = "select * from SERVICE_REQUEST_MONITORING where request_id=?1", nativeQuery = true)
	BIPS_Service_ReqMonitoring_Entity getReqId(String request_id);

}
