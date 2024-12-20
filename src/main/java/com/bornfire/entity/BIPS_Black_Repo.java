package com.bornfire.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BIPS_Black_Repo extends JpaRepository<BIPS_BlackList, String>{

	@Query(value = "select brn_unit from BIPS_BLACKLIST where brn_unit is not null", nativeQuery = true)
	List<String> getBrn_No_Unit();
	
	@Query(value = "select brn_date_unit from BIPS_BLACKLIST where brn_date_unit is not null", nativeQuery = true)
	List<Date> findBrnDateUnit();
	
	@Query(value = "select brn from BIPS_BLACKLIST where brn is not null", nativeQuery = true)
	List<String> findbrn();
	
	@Query(value = "select brn_date from BIPS_BLACKLIST where brn_date is not null", nativeQuery = true)
	List<Date> findbrn_date();
	
	@Query(value = "select corporatename from BIPS_BLACKLIST where corporatename is not null", nativeQuery = true)
	List<String> getcorporatename();
	
	@Query(value = "select mbl_num from BIPS_BLACKLIST where mbl_num is not null", nativeQuery = true)
	List<String> findmbl_num();
	
	
	@Query(value = "select national_id from BIPS_BLACKLIST where national_id is not null", nativeQuery = true)
	List<String> findnational_id();
	

	

	
	
}
