package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMaster_Repo  extends JpaRepository<DocumentMaster_Entity, DocumentMaster_Entity_PrimaryId> {
	
	@Query(value = "select * from DOCUMENT_MASTER where merchant_id= ?1", nativeQuery = true)
	List<DocumentMaster_Entity> findByMer( String merchant_id);

	@Query(value = "select * from DOCUMENT_MASTER where merchant_id= ?1 and unique_id = ?2", nativeQuery = true)
	List<DocumentMaster_Entity> findBy1(String merchant_id,String doc_type);
	
	@Query(value = "select * from DOCUMENT_MASTER where merchant_id= ?1 and unique_id = ?2", nativeQuery = true)
	DocumentMaster_Entity findByApplAndUnquieimg(String merchant_id,String doc_type);
	
	@Query(value = "select * from DOCUMENT_MASTER where unique_id = ?1", nativeQuery = true)
	DocumentMaster_Entity findByUnique1(String unique_id);
}
