package com.bornfire.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BIPS_Pep_Repo extends JpaRepository<BIPS_PEPLIST, String>{

	@Query(value = "select brn_unit from BIPS_PEPLIST where brn_unit is not null", nativeQuery = true)
	List<String> getBrn_No_Unit();
	
	@Query(value = "select brn_date_unit from BIPS_PEPLIST where brn_date_unit is not null", nativeQuery = true)
	List<Date> findBrnDateUnit();
	
	@Query(value = "select brn from BIPS_PEPLIST where brn is not null", nativeQuery = true)
	List<String> findbrn();
	
	@Query(value = "select brn_date from BIPS_PEPLIST where brn_date is not null", nativeQuery = true)
	List<Date> findbrn_date();
	
	@Query(value = "select corporatename from BIPS_PEPLIST where corporatename is not null", nativeQuery = true)
	List<String> getcorporatename();
	
	@Query(value = "select mbl_num from BIPS_PEPLIST where mbl_num is not null", nativeQuery = true)
	List<String> findmbl_num();
	
	
	@Query(value = "select national_id from BIPS_PEPLIST where national_id is not null", nativeQuery = true)
	List<String> findnational_id();	
	
}
