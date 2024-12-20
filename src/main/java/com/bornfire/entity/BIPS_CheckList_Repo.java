package com.bornfire.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BIPS_CheckList_Repo extends JpaRepository<BIPS_CheckListEntity, String>{

	@Query(value = "SELECT BIPS_CHECKLIST_SEQ.NEXTVAL FROM dual", nativeQuery = true)
	String getSequence();
	
}
