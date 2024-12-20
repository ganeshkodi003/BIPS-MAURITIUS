package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_ServiceReq_Repo extends JpaRepository<BIPS_Service_Req_Entity, String>{

	@Query(value = "SELECT DISTINCT * FROM bips_servicereq", nativeQuery = true)
	List<BIPS_Service_Req_Entity> getlst();
	
	@Query(value = "select * from bips_servicereq where device_id=?1 ", nativeQuery = true)
	BIPS_Service_Req_Entity getDevlst(String device_id);
	
}
