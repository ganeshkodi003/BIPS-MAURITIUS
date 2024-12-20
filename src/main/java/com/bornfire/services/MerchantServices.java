package com.bornfire.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.BankAgentTableRep;
import com.bornfire.entity.MerchantFees;
import com.bornfire.entity.MerchantFeesRep;

@Service
@ConfigurationProperties("output")
@Transactional

public class MerchantServices {
	
	@Autowired
	MerchantFeesRep merchantFeesRep;
	
	@Autowired
	BankAgentTableRep bankAgentTableRep;
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	SequenceGenerator sequence;
	private static final String NUM_LIST = "0123456789";

	public List<MerchantFees> getMerchantFeeList() {
		List<MerchantFees> list=merchantFeesRep.customFindByAll();
		return list;
	}

	public MerchantFees getMerchantFeeIndList(String acctNumber) {
		MerchantFees merchantReg=new MerchantFees();
		try {
			List<MerchantFees> mandateList=merchantFeesRep.customFindById(acctNumber);
			if(mandateList.size()>0) {
				merchantReg=mandateList.get(0);
			}
			
		}catch(Exception e) {
			merchantReg=new MerchantFees();
		}
		return merchantReg;
	}

	public String editMerchantFees(MerchantFees merchantReg, String userid) {
		String msg = "";
		try {
			List<MerchantFees> merchantList = merchantFeesRep.customFindById(merchantReg.getAccount_no().toString());
			if (merchantList.size() > 0) {

				MerchantFees merchantReg1 = merchantList.get(0);

				
					///merchantReg1.setAccount_no(merchantReg.getAccount_no());
					//merchantReg1.setMerchant_trade_name(merchantReg.getMerchant_trade_name());
					//merchantReg1.setMerchant_code(merchantReg.getMerchant_code());
					//merchantReg1.setCity(merchantReg.getCity());
					//merchantReg1.setAddress1(merchantReg.getAddress1());
					//merchantReg1.setEnd_date(merchantReg.getEnd_date());
					//merchantReg1.setStart_date(merchantReg.getStart_date());
					//merchantReg1.setAddress1(merchantReg.getAddress1());
					//merchantReg1.setRemarks(merchantReg.getRemarks());
					//merchantReg1.setMail_id(merchantReg.getMail_id());
					merchantReg.setEntry_user(merchantReg1.getEntry_user());
					merchantReg.setEntry_time(merchantReg1.getEntry_time());
					merchantReg.setDel_flg("N");
					merchantReg.setEntity_flg("Y");
					merchantReg.setModify_user(userid);
					merchantReg.setModify_time(new Date());
					merchantFeesRep.save(merchantReg);
					msg = "Merchant Fees Modified Successfully";
				}

			

		} catch (Exception e) {
			msg = "Error Occured,Please Contact Administrator";
		}
		return msg;

	};
	
	private int getRandomMsgNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(NUM_LIST.length());

		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}

	public String generateBIPSNumber() {

		StringBuffer randStr = new StringBuffer();
		randStr.append("CIM");
		randStr.append(new SimpleDateFormat("yyMMdd").format(new Date()));

		for (int i = 0; i < 6; i++) {
			int number = getRandomMsgNumber();
			char ch = NUM_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
}

	
	

