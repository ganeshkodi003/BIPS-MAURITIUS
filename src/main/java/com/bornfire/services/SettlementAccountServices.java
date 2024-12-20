package com.bornfire.services;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bornfire.entity.SettlementAccount;
import com.bornfire.entity.SettlementAccountRepository;

@Service
@ConfigurationProperties("output")
@Transactional
public class SettlementAccountServices {

	@Autowired
	SettlementAccountRepository settlementAccountRepository;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	Environment env;

	public String CreateSettlement(SettlementAccount settlementAccount, String formmode, String user) {

		String msg = "";
		if (formmode.equals("addsubmit")) {
			Optional<SettlementAccount> data = settlementAccountRepository.findById(settlementAccount.getAcc_code());
			if (!data.isPresent()) {
				SettlementAccount up = settlementAccount;
				up.setDel_flg("N");
				up.setEntity_flg("N");
				up.setModify_flg("N");
				up.setEntry_user(user);
				up.setEntry_time(new Date());
				settlementAccountRepository.save(up);
				msg = "Added Successfully";
			} else {
				if (data.get().getDel_flg().equals("Y")) {
					SettlementAccount up = settlementAccount;
					up.setDel_flg("N");
					up.setEntity_flg("N");
					up.setEntry_user(user);
					up.setEntry_time(new Date());
					settlementAccountRepository.save(up);
					msg = "Added Successfully";
				} else {
					msg = "Account Code already exist";
				}
			}
		} else if (formmode.equals("edit")) {
			Optional<SettlementAccount> data = settlementAccountRepository.findById(settlementAccount.getAcc_code());
			if (data.isPresent()) {
				SettlementAccount up = settlementAccount;
				up.setDel_flg("N");
				up.setEntity_flg("N");
				up.setModify_user(user);
				up.setModify_time(new Date());
				up.setModify_flg("Y");
				up.setEntry_user(data.get().getEntry_user());
				up.setEntry_time(data.get().getEntry_time());
				up.setVerify_user(data.get().getVerify_user());
				up.setVerify_time(data.get().getVerify_time());
				settlementAccountRepository.save(up);
				msg = "Modified Successfully";
			} else {
				msg = "Data not Found.Please contact Administrator";
			}
		} else if (formmode.equals("verify")) {
			Optional<SettlementAccount> data = settlementAccountRepository.findById(settlementAccount.getAcc_code());
			if (data.isPresent()) {
				SettlementAccount up = settlementAccount;
				up.setDel_flg("N");
				up.setEntity_flg("Y");
				up.setVerify_user(user);
				up.setModify_flg("N");
				up.setVerify_time(new Date());
				up.setEntry_user(data.get().getEntry_user());
				up.setEntry_time(data.get().getEntry_time());
				up.setModify_user(data.get().getModify_user());
				up.setModify_time(data.get().getModify_time());
				settlementAccountRepository.save(up);
				msg = "Verified Successfully";
			} else {
				msg = "Data not Found.Please contact Administrator";
			}
		} else if (formmode.equals("delete")) {
			Optional<SettlementAccount> data = settlementAccountRepository.findById(settlementAccount.getAcc_code());
			if (data.isPresent()) {
				SettlementAccount up = settlementAccount;
				up.setDel_flg("Y");
				up.setEntity_flg("N");
				up.setModify_flg("N");
				up.setEntry_user(data.get().getEntry_user());
				up.setEntry_time(data.get().getEntry_time());
				up.setModify_user(data.get().getModify_user());
				up.setModify_time(data.get().getModify_time());
				up.setVerify_user(user);
				up.setVerify_time(new Date());
				settlementAccountRepository.save(up);
				msg = "Deleted Successfully";
			} else {
				msg = "Data not Found.Please contact Administrator";
			}
		}
		return msg;
	}

}
