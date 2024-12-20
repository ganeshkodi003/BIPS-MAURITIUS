package com.bornfire.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginSecurityRepository extends JpaRepository<LoginSecurity, String> {

	Optional<LoginSecurity> findById(String directorId);

	@Query(value = "select * from BIPS_LOGIN_SEC_TABLE ", nativeQuery = true)
	List<LoginSecurity> findAllCustom();

	@Query(value = "select * from BIPS_LOGIN_SEC_TABLE ", nativeQuery = true)
	LoginSecurity findByIdCustom(String Id);

}
