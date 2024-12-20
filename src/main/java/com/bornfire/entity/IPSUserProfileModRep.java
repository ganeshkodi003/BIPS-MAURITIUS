package com.bornfire.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPSUserProfileModRep extends CrudRepository<IPSUserProfileMod, String> {

	public Optional<IPSUserProfileMod> findByusername(String userName);

	@Query(value = "select * from BIPS_USER_PROFILE_MOD_TABLE where USER_ID= ?1", nativeQuery = true)
	IPSUserProfileMod findByIdCustom(String Id);

	@Query(value = "select * from BIPS_USER_PROFILE_MOD_TABLE where NVL(DEL_FLG,1) <> 'Y'", nativeQuery = true)
	List<IPSUserProfileMod> findByAll(String Id);
}
