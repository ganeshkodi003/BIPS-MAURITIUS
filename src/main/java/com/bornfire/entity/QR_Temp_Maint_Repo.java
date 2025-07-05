package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QR_Temp_Maint_Repo extends JpaRepository<QR_Temp_Maint_Entity, String>{

	@Query(value = "SELECT MAX(TO_NUMBER(numeric_part)) AS max_numeric_part FROM (SELECT REGEXP_REPLACE(banner_id, '[^[:digit:]]', '') AS numeric_part FROM QR_TEMPLATE_MAINTENANCE)", nativeQuery = true)
	String getBannerID();
	
	@Query(value = "select * from qr_template_maintenance where banner_id =?1", nativeQuery = true)
	QR_Temp_Maint_Entity getTemplate(String banner_id);
	
	@Query(value = "select * from qr_template_maintenance ORDER BY banner_id", nativeQuery = true)
	List<QR_Temp_Maint_Entity> gettemplist();
	
	@Query(value = "SELECT * FROM qr_template_maintenance WHERE banner_id=?1 ", nativeQuery = true)
	List<QR_Temp_Maint_Entity> getBlobImage(String userid);
}
