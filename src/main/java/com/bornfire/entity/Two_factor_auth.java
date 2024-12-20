package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Two_factor_auth  extends JpaRepository<Twofactorauth,String> {
	 @Query(value = "select * from Two_factor_auth where user_id=?1", nativeQuery = true)
	    List<Two_factor_auth> merchantDetails(String merchantId);

}