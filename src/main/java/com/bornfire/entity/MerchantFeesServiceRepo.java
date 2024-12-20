package com.bornfire.entity;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantFeesServiceRepo extends JpaRepository<MerchantFeesServiceCharges,MerchantFeeID> {

    @Query(value = "select * from MERCHANT_FEES_SERVICE_CHARGES where MERCHANT_ID=?1 union all select * from MERCHANT_FEES_SERVICE_CHARGES_mod where MERCHANT_ID=?1", nativeQuery = true)
    List<MerchantFeesServiceCharges> merchantDetails(String merchantId);
    
    @Query(value = "select * from MERCHANT_FEES_SERVICE_CHARGES_mod where MERCHANT_ID=?1", nativeQuery = true)
    List<MerchantFeesServiceCharges> merchantDetailsFromMod(String merchantId);
    
    @Query(value = "select FEE_SRL_NO.nextval from dual", nativeQuery = true)
    String SrlNo();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM MERCHANT_FEES_SERVICE_CHARGES where MERCHANT_ID=?1", nativeQuery = true)
    void deleteDetails(String merchantId);
    
    @Query(value = "select distinct FEE_FREQ from MERCHANT_FEES_SERVICE_CHARGES where FEE_FREQ is not null", nativeQuery = true)
    List<String> merchantfee();
}
