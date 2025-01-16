package com.bornfire.services;

import java.util.Date;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.BankAgentTableRep;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;
import com.bornfire.entity.MerchantCategoryCodeEntity;
import com.bornfire.entity.MerchantCategoryRep;
import com.bornfire.entity.NotificationParms;
import com.bornfire.entity.NotificationParmsRep;

@Service
@ConfigurationProperties("output")
@Transactional

public class NotificationParmsServices {

	@Autowired
	NotificationParmsRep notificationParmsRep;

	@Autowired
	BankAgentTableRep bankAgentTableRep;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	MerchantCategoryRep merchantCategoryRep;

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	public String createNotificationParms(NotificationParms notificationParmsReg, String userid) {
		String msg = "";
		try {
			List<NotificationParms> notificationParmsList = notificationParmsRep.customFindById(
					notificationParmsReg.getRECORD_SRL_NO().toString(),
					notificationParmsReg.getTRAN_CATEGORY().toString());
			if (notificationParmsList.size() > 0) {
				msg = "The given parameter already exist";
			} else {
				notificationParmsReg.setENTRY_USER(userid);
				notificationParmsReg.setENTRY_TIME(new Date());
				notificationParmsReg.setDEL_FLG("N");
				notificationParmsReg.setENTITY_FLG("N");
				notificationParmsRep.save(notificationParmsReg);
				msg = "Notification Parms Added Successfully";

				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userid);
				audit.setFunc_code("NOTIFICATION PARAMETER CREATION");
				audit.setRemarks(
						notificationParmsReg.getRECORD_SRL_NO() + " : Notification Parameter Created Successfully");
				audit.setAudit_table("NOTIFICATION_PARM_MASTER");
				audit.setAudit_screen("Notification Parameter - Add");
				audit.setEvent_id(userid);
				audit.setEvent_name(userid);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

			}

		} catch (Exception e) {
			msg = "Error Occured,Please Contact Administrator";
		}
		return msg;
	}

	public NotificationParms getNotificationParmsList(String record_srl_no) {
		NotificationParms notificationParmsReg = new NotificationParms();
		try {
			List<NotificationParms> mandateList = notificationParmsRep.findById1(record_srl_no);
			// System.out.println(mandateList.size());
			if (mandateList.size() > 0) {
				notificationParmsReg = mandateList.get(0);
			}

		} catch (Exception e) {
			notificationParmsReg = new NotificationParms();
		}
		return notificationParmsReg;
	}

	public String editNotificationParms(NotificationParms notificationParmsReg, String userid) {
		String msg = "";
		try {
			List<NotificationParms> notificationParmsList = notificationParmsRep.customFindById(
					notificationParmsReg.getRECORD_SRL_NO().toString(),
					notificationParmsReg.getTRAN_CATEGORY().toString());
			if (notificationParmsList.size() > 0) {

				NotificationParms notificationParmsReg1 = notificationParmsList.get(0);

				/*
				 * if (notificationParmsReg.getRECORD_SRL_NO().equals(notificationParmsReg1.
				 * getRECORD_SRL_NO()) &&
				 * notificationParmsReg.getNOTIFICATION_EVENT_NO().equals(notificationParmsReg1.
				 * getNOTIFICATION_EVENT_NO()) &&
				 * notificationParmsReg.getNOTIFICATION_EVENT_DESC().equals(
				 * notificationParmsReg1.getNOTIFICATION_EVENT_DESC()) &&
				 * notificationParmsReg.getSTART_DATE().equals(notificationParmsReg1.
				 * getSTART_DATE()) &&
				 * notificationParmsReg.getEND_DATE().equals(notificationParmsReg1.getEND_DATE()
				 * ) &&
				 * notificationParmsReg.getNOTIFICATION_EMAIL_1().equals(notificationParmsReg1.
				 * getNOTIFICATION_EMAIL_1()) &&
				 * notificationParmsReg.getNOTIFICATION_EMAIL_2().equals(notificationParmsReg1.
				 * getNOTIFICATION_EMAIL_2()) &&
				 * notificationParmsReg.getNOTIFICATION_EMAIL_3().equals(notificationParmsReg1.
				 * getNOTIFICATION_EMAIL_3()) &&
				 * notificationParmsReg.getNOTIFICATION_MOBILE_1().equals(notificationParmsReg1.
				 * getNOTIFICATION_MOBILE_1()) &&
				 * notificationParmsReg.getNOTIFICATION_MOBILE_2().equals(notificationParmsReg1.
				 * getNOTIFICATION_MOBILE_2()) &&
				 * notificationParmsReg.getNOTIFICATION_MOBILE_3().equals(notificationParmsReg1.
				 * getNOTIFICATION_MOBILE_3()) &&
				 * notificationParmsReg.getNOTIFICATION_USER_1().equals(notificationParmsReg1.
				 * getNOTIFICATION_USER_1()) &&
				 * notificationParmsReg.getNOTIFICATION_USER_2().equals(notificationParmsReg1.
				 * getNOTIFICATION_USER_2()) &&
				 * notificationParmsReg.getNOTIFICATION_USER_3().equals(notificationParmsReg1.
				 * getNOTIFICATION_USER_3()) &&
				 * notificationParmsReg.getTRAN_CATEGORY().equals(notificationParmsReg1.
				 * getTRAN_CATEGORY())) { msg = "No Any Modification Done"; } else {
				 */
				/// merchantReg1.setAccount_no(merchantReg.getAccount_no());
				// merchantReg1.setMerchant_trade_name(merchantReg.getMerchant_trade_name());
				// merchantReg1.setMerchant_code(merchantReg.getMerchant_code());
				// merchantReg1.setCity(merchantReg.getCity());
				// merchantReg1.setAddress1(merchantReg.getAddress1());
				// merchantReg1.setEnd_date(merchantReg.getEnd_date());
				// merchantReg1.setStart_date(merchantReg.getStart_date());
				// merchantReg1.setAddress1(merchantReg.getAddress1());
				// merchantReg1.setRemarks(merchantReg.getRemarks());
				// merchantReg1.setMail_id(merchantReg.getMail_id());
				notificationParmsReg.setENTRY_USER(notificationParmsReg1.getENTRY_USER());
				notificationParmsReg.setENTRY_TIME(notificationParmsReg1.getENTRY_TIME());
				notificationParmsReg.setDEL_FLG("N");
				notificationParmsReg.setENTITY_FLG("N");
				notificationParmsReg.setMODIFY_USER(userid);
				notificationParmsReg.setMODIFY_TIME(new Date());
				notificationParmsRep.save(notificationParmsReg);
				msg = "NotificationParms Modified Successfully";
				/* } */

				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userid);
				audit.setFunc_code("NOTIFICATION PARAMETER MODIFICATION");
				audit.setRemarks(
						notificationParmsReg.getRECORD_SRL_NO() + " : Notification Parameter Modified Successfully");
				audit.setAudit_table("NOTIFICATION_PARM_MASTER");
				audit.setAudit_screen("Notification Parameter - Modify");
				audit.setEvent_id(userid);
				audit.setEvent_name(userid);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

			} else {
				msg = "No Data Found";
			}

		} catch (Exception e) {
			msg = "Error Occured,Please Contact Administrator";
		}
		return msg;
	}

	public String verifyNotificationParms(NotificationParms notificationParmsReg, String userid) {
		String msg = "";
		try {
			List<NotificationParms> notificationParmsList = notificationParmsRep.customFindById(
					notificationParmsReg.getRECORD_SRL_NO().toString(),
					notificationParmsReg.getTRAN_CATEGORY().toString());
			if (notificationParmsList.size() > 0) {

				NotificationParms notificationParmsReg1 = notificationParmsList.get(0);

				notificationParmsReg.setENTRY_USER(notificationParmsReg1.getENTRY_USER());
				notificationParmsReg.setENTRY_TIME(notificationParmsReg1.getENTRY_TIME());
				notificationParmsReg.setDEL_FLG("N");
				notificationParmsReg.setENTITY_FLG("Y");
				notificationParmsReg.setVERIFY_USER(userid);
				notificationParmsReg.setVERIFY_TIME(new Date());
				notificationParmsRep.save(notificationParmsReg);
				msg = "NotificationParms Verified Successfully";

				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userid);
				audit.setFunc_code("NOTIFICATION PARAMETER VERIFICATION");
				audit.setRemarks(
						notificationParmsReg.getRECORD_SRL_NO() + " : Notification Parameter Verified Successfully");
				audit.setAudit_table("NOTIFICATION_PARM_MASTER");
				audit.setAudit_screen("Notification Parameter - Verify");
				audit.setEvent_id(userid);
				audit.setEvent_name(userid);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

			} else {
				msg = "No Date Found";
			}

		} catch (Exception e) {
			msg = "Error Occured,Please Contact Administrator";
		}
		return msg;
	}

	public String deleteNotificationParms(NotificationParms notificationParmsReg, String userid) {
		String msg = "";
		try {
			List<NotificationParms> notificationParmsList = notificationParmsRep.customFindById(
					notificationParmsReg.getRECORD_SRL_NO().toString(),
					notificationParmsReg.getTRAN_CATEGORY().toString());
			if (notificationParmsList.size() > 0) {

				NotificationParms notificationParmsReg1 = notificationParmsList.get(0);
				notificationParmsReg.setENTRY_USER(notificationParmsReg1.getENTRY_USER());
				notificationParmsReg.setENTRY_TIME(notificationParmsReg1.getENTRY_TIME());
				notificationParmsReg.setDEL_FLG("Y");
				notificationParmsReg.setVERIFY_USER(userid);
				notificationParmsReg.setVERIFY_TIME(new Date());
				notificationParmsRep.save(notificationParmsReg);
				msg = "NotificationParms Deleted Successfully";

			} else {
				msg = "No Date Found";
			}

		} catch (Exception e) {
			msg = "Error Occured,Please Contact Administrator";
		}
		return msg;
	}

	public String createmerchantcate(String refcode, String refdesc, String userid, String Formmode) {
		String msg = "";
		try {
			String notificationParmsList = merchantCategoryRep.getlistcount(refcode);
			MerchantCategoryCodeEntity merchantCategoryCodeEntity = new MerchantCategoryCodeEntity();
			if (Formmode.equals("add")) {
				// System.out.println("Formmode1:"+Formmode);
				if (!notificationParmsList.equals("0")) {
					msg = "The given Merchant Code already exist";
				} else {
					merchantCategoryCodeEntity.setMerchant_code(refcode);
					merchantCategoryCodeEntity.setMerchant_desc(refdesc);
					merchantCategoryCodeEntity.setEntry_user(userid);
					merchantCategoryCodeEntity.setEntry_time(new Date());
					merchantCategoryCodeEntity.setDel_flg("N");
					merchantCategoryCodeEntity.setEntity_cre_flg("N");
					merchantCategoryRep.save(merchantCategoryCodeEntity);
					msg = "Merchant Category Code Added Successfully";
				}

				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userid);
				audit.setFunc_code("MERCHANT CATEGORY CODE CREATION");
				audit.setRemarks(merchantCategoryCodeEntity.getMerchant_code()
						+ " : Merchant Category Code Created Successfully");
				audit.setAudit_table("BIPS_MERCHANT_CATEGORY_CODE_TABLE");
				audit.setAudit_screen("Merchant Category Code-Add");
				audit.setEvent_id(userid);
				audit.setEvent_name(userid);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

			} else if (Formmode.equals("edit")) {
				// System.out.println("Formmode:"+Formmode);
				MerchantCategoryCodeEntity notificationParmsListedit = merchantCategoryRep.getlist(refcode);
				merchantCategoryCodeEntity.setEntry_user(notificationParmsListedit.getEntry_user());
				merchantCategoryCodeEntity.setMerchant_code(refcode);
				merchantCategoryCodeEntity.setMerchant_desc(refdesc);
				merchantCategoryCodeEntity.setEntry_time(notificationParmsListedit.getEntry_time());
				merchantCategoryCodeEntity.setModify_user(userid);
				merchantCategoryCodeEntity.setModify_time(new Date());
				merchantCategoryCodeEntity.setDel_flg("N");
				merchantCategoryCodeEntity.setModify_flg("Y");
				merchantCategoryCodeEntity.setEntity_cre_flg("N");
				merchantCategoryRep.save(merchantCategoryCodeEntity);
				msg = "Merchant Category Code Edited Successfully";

				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userid);
				audit.setFunc_code("MERCHANT CATEGORY CODE MODIFICATION");
				audit.setRemarks(merchantCategoryCodeEntity.getMerchant_code()
						+ " : Merchant Category Code Modified Successfully");
				audit.setAudit_table("BIPS_MERCHANT_CATEGORY_CODE_TABLE");
				audit.setAudit_screen("Merchant Category Code - Modify");
				audit.setEvent_id(userid);
				audit.setEvent_name(userid);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

			} else if (Formmode.equals("delete")) {
				System.out.println(Formmode+"FormmodeFormmode");
				MerchantCategoryCodeEntity notificationParmsDEL = merchantCategoryRep.getlist(refcode);
				notificationParmsDEL.setDel_flg("Y");
				msg = "Merchant Category Code Deleted Successfully";
			}
		} catch (Exception e) {
			msg = e.toString();
		}
		return msg;
	}
}
