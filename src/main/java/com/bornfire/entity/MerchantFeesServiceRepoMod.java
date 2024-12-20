package com.bornfire.entity;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantFeesServiceRepoMod extends JpaRepository<MerchantFeesServiceChargesMod,MerchantFeeID> {

    @Query(value = "select * from MERCHANT_FEES_SERVICE_CHARGES_MOD where MERCHANT_ID=?1", nativeQuery = true)
    List<MerchantFeesServiceChargesMod> merchantDetails(String merchantId);
    
    @Query(value = "select * from MERCHANT_FEES_SERVICE_CHARGES_MOD where MERCHANT_ID=?1", nativeQuery = true)
    List<MerchantFeesServiceChargesMod> merchantDetailsMod(String merchantId);
    
    @Query(value = "select FEE_SRL_NO.nextval from dual", nativeQuery = true)
    String SrlNo();

    @Modifying
	@Transactional
    @Query(value = "DELETE FROM MERCHANT_FEES_SERVICE_CHARGES_MOD where MERCHANT_ID=?1", nativeQuery = true)
    void deleteDetails(String merchantId);
}
