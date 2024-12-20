package com.bornfire.services;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import com.bornfire.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.bornfire.configuration.PasswordEncryption;
import com.bornfire.configuration.SequenceGenerator;

@Service
@ConfigurationProperties("output")
public class BankAndBranchMasterServices {

	@Autowired
	BankAgentTableRep bankAgentTableRep;
	
	@Autowired
	BankAgentTmpTableRep bankAgentTmpTableRep;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	@Autowired
	MerchantMasterRep merchantMasterRep;

	@Autowired
	MerchantMasterModRep merchantMasterModRep;
	
	@Autowired
	MerchantFeesServiceRepo feeRepo;

	@Autowired
	MerchantFeesServiceRepoMod feeRepoMod;

	@Autowired
	BIPS_MerUserManagement_Repo bIPS_MerUserManagement_Repo;

	@Autowired
	BIPS_PasswordManagement_Repo bIPS_PasswordManagement_Repo;

	@Autowired
	BIPS_UnitManagement_Repo bIPS_UnitManagement_Repo;

	@Autowired
	Environment env;

	public String addBank(BankAgentTable bankAgentTable, String formmode, String userID) {
		String msg = "";

		if (formmode.equals("editsubmit")) {

			BankAgentTable bankAgentTableOrgl = bankAgentTableRep.findById(bankAgentTable.getBank_code()).get();

			if (bankAgentTableOrgl.getBank_name().equals(bankAgentTable.getBank_name())
					&& bankAgentTableOrgl.getBank_agent().equals(bankAgentTable.getBank_agent())
					&& bankAgentTableOrgl.getBank_agent_account().equals(bankAgentTable.getBank_agent_account())
					&& bankAgentTableOrgl.getAddress().equals(bankAgentTable.getAddress()) && bankAgentTableOrgl
							.getBusiness_central_bank_code().equals(bankAgentTable.getBusiness_central_bank_code())) {
				msg = "No any modification done";

			} else {
				String audit_ref_no = sequence.generateRequestUUId();
				BankAgentTmpTable upTmp = new BankAgentTmpTable(bankAgentTable);

				upTmp.setDisable_flg(bankAgentTableOrgl.getDisable_flg());
				upTmp.setDel_flg("N");
				upTmp.setModify_flg("Y");
				upTmp.setEntity_flg("N");
				upTmp.setNew_bank_flg("N");
				upTmp.setModify_time(new Date());
				upTmp.setModify_user(userID);

				Session session = sessionFactory.getCurrentSession();
				session.saveOrUpdate(upTmp);

				bankAgentTableOrgl.setEntity_flg("N");
				bankAgentTableRep.save(bankAgentTableOrgl);
				msg = "Modified Successfully";

				StringBuilder stringBuilder = new StringBuilder();

				if (bankAgentTableOrgl.getBank_name().equals(bankAgentTable.getBank_name())
						|| bankAgentTableOrgl.getBank_agent().equals(bankAgentTable.getBank_agent())
						|| bankAgentTableOrgl.getBank_agent_account().equals(bankAgentTable.getBank_agent_account())
						|| bankAgentTableOrgl.getAddress().equals(bankAgentTable.getAddress())
								&& bankAgentTableOrgl.getBusiness_central_bank_code()
										.equals(bankAgentTable.getBusiness_central_bank_code())) {

					if (!isNullCheck(bankAgentTableOrgl.getBank_name())
							.equals(isNullCheck(bankAgentTable.getBank_name()))) {
						stringBuilder = stringBuilder.append("Bank Name+" + bankAgentTableOrgl.getBank_name() + "+"
								+ bankAgentTable.getBank_name() + "||");
					}

					if (!isNullCheck(bankAgentTableOrgl.getBank_agent())
							.equals(isNullCheck(bankAgentTable.getBank_agent()))) {
						stringBuilder = stringBuilder.append("Bank Agent+" + bankAgentTableOrgl.getBank_agent() + "+"
								+ bankAgentTable.getBank_agent() + "||");
					}

					if (!isNullCheck(bankAgentTableOrgl.getBank_agent_account())
							.equals(isNullCheck(bankAgentTable.getBank_agent_account()))) {
						stringBuilder = stringBuilder
								.append("Bank Agent Account+" + bankAgentTableOrgl.getBank_agent_account() + "+"
										+ bankAgentTable.getBank_agent_account() + "||");
					}
					if (!isNullCheck(bankAgentTableOrgl.getAddress())
							.equals(isNullCheck(bankAgentTable.getAddress()))) {
						stringBuilder = stringBuilder.append("Address+" + bankAgentTableOrgl.getAddress() + "+"
								+ bankAgentTable.getAddress() + "||");
					}

					if (!isNullCheck(bankAgentTableOrgl.getBusiness_central_bank_code())
							.equals(isNullCheck(bankAgentTable.getBusiness_central_bank_code()))) {
						stringBuilder = stringBuilder.append(
								"Business central Bank Code+" + bankAgentTableOrgl.getBusiness_central_bank_code() + "+"
										+ bankAgentTable.getBusiness_central_bank_code() + "||");
					}

					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(userID);
					audit.setFunc_code("PARTICIPANT BANK MODIFICATION");
					audit.setRemarks(bankAgentTable.getBank_code() + " : Participant Bank Modified Successfully");
					audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
					audit.setAudit_screen("Participant Bank-Modify");
					audit.setEvent_id(bankAgentTable.getBank_code());
					// audit.setEvent_name(up.getUsername());
					String modiDetails = stringBuilder.toString();
					audit.setModi_details(modiDetails);
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);

				}

			}

		} else if (formmode.equals("verifysubmit")) {

			Optional<BankAgentTmpTable> dat = bankAgentTmpTableRep.findById(bankAgentTable.getBank_code());

			String delRec = deletePreviDeleeteRec(dat, bankAgentTable.getBank_agent());

			if (delRec.equals("0")) {
				String verifyBankCode = verifyBankCode(bankAgentTable.getBank_code(), userID);

				if (verifyBankCode.equals("0")) {

					String audit_ref_no = sequence.generateRequestUUId();
					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(userID);
					audit.setFunc_code("Participant Bank VERIFICATION");
					audit.setRemarks(dat.get().getBank_code() + " : Participant Bank Verified Successfully");
					audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
					audit.setAudit_screen("Participant Bank-Verify");
					audit.setEvent_id(dat.get().getBank_code());
					audit.setAuth_user(userID);
					audit.setAuth_time(new Date());
					// audit.setEvent_name(up.getUsername());
					if (!dat.get().getNew_bank_flg().equals("Y")) {
						IPSAuditTable audit1 = ipsAuditTableRep.getModifyList1(dat.get().getBank_code(),
								"PARTICIPANT BANK MODIFICATION");
						/*
						 * String modiDetails=audit1.getModi_details();
						 * audit.setModi_details(modiDetails);
						 */
						if (audit1 != null) {
							if (!String.valueOf(audit1.getModi_details()).equals("null")
									|| !String.valueOf(audit1.getModi_details()).equals("")) {
								String modiDetails = audit1.getModi_details();
								audit.setModi_details(modiDetails);
							} else {
								audit.setModi_details("-");
							}
						} else {
							audit.setModi_details("-");
						}
					} else {
						audit.setModi_details("-");
					}

					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);

					msg = "Verified Successfully";
					bankAgentTmpTableRep.deleteById(bankAgentTable.getBank_code());

					return msg;
				}

			}

		} else if (formmode.equals("cancelsubmit")) {

			Optional<BankAgentTmpTable> dat = bankAgentTmpTableRep.findById(bankAgentTable.getBank_code());

			if (dat.get().getNew_bank_flg().equals("Y")) {
				msg = "Bank Cancelled Successfully";
				bankAgentTmpTableRep.deleteById(bankAgentTable.getBank_code());

				String audit_ref_no = sequence.generateRequestUUId();
				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userID);
				audit.setFunc_code("Participant Bank CANCEL");
				audit.setRemarks(dat.get().getBank_code() + " : Participant Bank Cancelled Successfully");
				audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
				audit.setAudit_screen("Participant Bank-Cancel");
				audit.setEvent_id(dat.get().getBank_code());
				audit.setAuth_user(userID);
				audit.setAuth_time(new Date());
				// audit.setEvent_name(up.getUsername());
				if (!dat.get().getNew_bank_flg().equals("Y")) {
					IPSAuditTable audit1 = ipsAuditTableRep.getModifyList1(dat.get().getBank_code(),
							"PARTICIPANT BANK MODIFICATION");
					/*
					 * String modiDetails=audit1.getModi_details();
					 * audit.setModi_details(modiDetails);
					 */
					if (audit1 != null) {
						if (!String.valueOf(audit1.getModi_details()).equals("null")
								|| !String.valueOf(audit1.getModi_details()).equals("")) {
							String modiDetails = audit1.getModi_details();
							audit.setModi_details(modiDetails);
						} else {
							audit.setModi_details("-");
						}
					} else {
						audit.setModi_details("-");
					}
				} else {
					audit.setModi_details("-");
				}

				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

				return msg;
			} else {

				String status = cancelBankModification(bankAgentTable.getBank_code(), userID);

				if (status.equals("0")) {
					String audit_ref_no = sequence.generateRequestUUId();
					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(userID);
					audit.setFunc_code("Participant Bank CANCEL");
					audit.setRemarks(dat.get().getBank_code() + " : Participant Bank Cancelled Successfully");
					audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
					audit.setAudit_screen("Participant Bank-Cancel");
					audit.setEvent_id(dat.get().getBank_code());
					audit.setAuth_user(userID);
					audit.setAuth_time(new Date());
					// audit.setEvent_name(up.getUsername());
					if (!dat.get().getNew_bank_flg().equals("Y")) {
						IPSAuditTable audit1 = ipsAuditTableRep.getModifyList1(dat.get().getBank_code(),
								"PARTICIPANT BANK MODIFICATION");
						/*
						 * String modiDetails=audit1.getModi_details();
						 * audit.setModi_details(modiDetails);
						 */
						if (audit1 != null) {
							if (!String.valueOf(audit1.getModi_details()).equals("null")
									|| !String.valueOf(audit1.getModi_details()).equals("")) {
								String modiDetails = audit1.getModi_details();
								audit.setModi_details(modiDetails);
							} else {
								audit.setModi_details("-");
							}
						} else {
							audit.setModi_details("-");
						}
					} else {
						audit.setModi_details("-");
					}

					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);

					msg = "Bank Cancelled Successfully";
					bankAgentTmpTableRep.deleteById(bankAgentTable.getBank_code());

					return msg;
				}

			}

		} else if (formmode.equals("deletesubmit")) {

			BankAgentTable up = bankAgentTable;
			up.setDel_flg("Y");
			/// up.setDisable_flg("N");
			up.setDel_user(userID);
			up.setDel_time(new Date());

			bankAgentTableRep.save(up);
			msg = "Deleted Successfully";

			String audit_ref_no = sequence.generateRequestUUId();
			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setFunc_code("Participant Bank DELETE");
			audit.setRemarks(up.getBank_code() + " : Participant Bank Deleted Successfully");
			audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
			audit.setAudit_screen("Participant Bank-Delete");
			audit.setModi_details("-");

			audit.setAudit_ref_no(audit_ref_no);

			ipsAuditTableRep.save(audit);

		} else if (formmode.equals("disablesubmit")) {

			BankAgentTable up = bankAgentTableRep.findByIdCustom(bankAgentTable.getBank_code());
			up.setDisable_flg("Y");

			up.setDisable_time(new Date());
			up.setDisable_user(userID);
			bankAgentTableRep.save(up);

			String audit_ref_no = sequence.generateRequestUUId();
			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setFunc_code("Participant Bank DISABLE");
			audit.setRemarks(up.getBank_code() + " : Participant Bank Disabled Successfully");
			audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
			audit.setAudit_screen("Participant Bank-Disable");
			audit.setModi_details("-");

			audit.setAudit_ref_no(audit_ref_no);

			ipsAuditTableRep.save(audit);

			msg = "Disabled Successfully";

		} else if (formmode.equals("enablesubmit")) {

			BankAgentTable up = bankAgentTableRep.findByIdCustom(bankAgentTable.getBank_code());
			up.setDisable_flg("N");

			up.setDisable_time(new Date());
			up.setDisable_user(userID);
			bankAgentTableRep.save(up);
			msg = "Enabled Successfully";

			String audit_ref_no = sequence.generateRequestUUId();
			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setFunc_code("Participant Bank ENABLE");
			audit.setRemarks(up.getBank_code() + " : Participant Bank Enabled Successfully");
			audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
			audit.setAudit_screen("Participant Bank-Enable");
			audit.setModi_details("-");

			audit.setAudit_ref_no(audit_ref_no);

			ipsAuditTableRep.save(audit);

		} else if (formmode.equals("addsubmit")) {

			BankAgentTmpTable upTmp = new BankAgentTmpTable(bankAgentTable);
			Optional<BankAgentTable> bankList = bankAgentTableRep.findById(bankAgentTable.getBank_code());
			List<BankAgentTable> bankListAgent = bankAgentTableRep.findByIdCustomAgent1(bankAgentTable.getBank_agent());

			if (bankList.isPresent()) {

				if (bankList.get().getDel_flg().equals("Y")) {

					// BankAgentTable up = bankAgentTable;
					upTmp.setEntity_flg("N");
					upTmp.setModify_flg("N");
					upTmp.setDisable_flg("N");
					upTmp.setDel_flg("N");
					upTmp.setNew_bank_flg("Y");
					upTmp.setEntry_time(new Date());
					upTmp.setEntry_user(userID);
					bankAgentTmpTableRep.save(upTmp);
					msg = "Bank Added Successfully";

					String audit_ref_no = sequence.generateRequestUUId();
					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(userID);
					audit.setFunc_code("BANK CREATION");
					audit.setRemarks(upTmp.getBank_code() + " : Participant Bank Created Successfully");
					audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
					audit.setAudit_screen("Participant Bank-Add");
					audit.setEvent_id(upTmp.getBank_code());
					audit.setEvent_name(userID);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);
				} else {

					if (bankList.get().getBank_agent().equals(bankAgentTable.getBank_agent())) {
						msg = "Bank Agent Already Exist";
					} else {
						msg = "Bank Code Already Exist";
					}
				}
			} else if (bankListAgent.size() > 0) {
				if (bankListAgent.get(0).getDel_flg().equals("Y")) {

					// BankAgentTable up = bankAgentTable;
					upTmp.setEntity_flg("N");
					upTmp.setModify_flg("N");
					upTmp.setDisable_flg("N");
					upTmp.setDel_flg("N");
					upTmp.setNew_bank_flg("Y");
					upTmp.setEntry_time(new Date());
					upTmp.setEntry_user(userID);
					bankAgentTmpTableRep.save(upTmp);
					msg = "Bank Added Successfully";

					String audit_ref_no = sequence.generateRequestUUId();
					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(userID);
					audit.setFunc_code("BANK CREATION");
					audit.setRemarks(upTmp.getBank_code() + " : Participant Bank Created Successfully");
					audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
					audit.setAudit_screen("Participant Bank-Add");
					audit.setEvent_id(upTmp.getBank_code());
					audit.setEvent_name(userID);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);
				} else {

					if (bankListAgent.get(0).getBank_agent().equals(bankAgentTable.getBank_agent())) {
						msg = "Bank Agent Already Exist";
					} else {
						msg = "Bank Code Already Exist";
					}
				}
			} else {

				// //System.out.println("Entry User"+bankAgentTable.getEntry_user());
				upTmp.setEntity_flg("N");
				upTmp.setModify_flg("N");
				upTmp.setDisable_flg("N");
				upTmp.setDel_flg("N");
				upTmp.setNew_bank_flg("Y");
				upTmp.setEntry_time(new Date());
				upTmp.setEntry_user(userID);
				bankAgentTmpTableRep.save(upTmp);
				;

				msg = "Bank Added Successfully";

				String audit_ref_no = sequence.generateRequestUUId();
				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(userID);
				audit.setFunc_code("BANK CREATION");
				audit.setRemarks(upTmp.getBank_code() + " : Participant Bank Created Successfully");
				audit.setAudit_table("BIPS_OTHER_BANK_AGENT_TABLE");
				audit.setAudit_screen("Participant Bank-Add");
				audit.setEvent_id(userID);
				audit.setEvent_name(userID);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);
			}

		}
		return msg;

	}

	private String verifyBankCode(String bankCode, String USERID) {
		Optional<BankAgentTmpTable> user = bankAgentTmpTableRep.findById(bankCode);
		BankAgentTmpTable user1 = user.get();
		BankAgentTable verifiedData = new BankAgentTable(user1);

		verifiedData.setDel_flg("N");
		verifiedData.setModify_flg("N");
		verifiedData.setEntity_flg("Y");
		verifiedData.setNew_bank_flg("N");
		verifiedData.setVerify_user(USERID);
		verifiedData.setVerify_time(new Date());

		Session hs = sessionFactory.getCurrentSession();
		hs.saveOrUpdate(verifiedData);
		// bankAgentTableRep.save(verifiedData);
		return "0";

	}

	private String cancelBankModification(String bankCode, String USERID) {
		Optional<BankAgentTable> user = bankAgentTableRep.findById(bankCode);
		BankAgentTable user1 = user.get();

		user1.setDel_flg("N");
		user1.setModify_flg("N");
		user1.setEntity_flg("Y");
		user1.setNew_bank_flg("N");
		user1.setVerify_user(USERID);
		user1.setVerify_time(new Date());

		Session hs = sessionFactory.getCurrentSession();
		hs.saveOrUpdate(user1);
		// bankAgentTableRep.save(verifiedData);
		return "0";
	}

	private String deletePreviDeleeteRec(Optional<BankAgentTmpTable> dat, String bankAgent) {
		if (!dat.get().getNew_bank_flg().equals("N")) {
			/// delete Previous Deleted Records.
			List<BankAgentTable> dataLi = bankAgentTableRep.findByIdCustomAgent1(bankAgent);

			if (dataLi.size() > 0) {
				// //System.out.println("donne->"+dataLi.get(0).getBank_code());
				bankAgentTableRep.deleteById(dataLi.get(0).getBank_code());
				bankAgentTableRep.flush();
				return "0";
			} else {
				return "0";
			}
		} else {
			return "0";
		}

	}

	private String isNullCheck(String inpData) {
		String outData = "";
		if (inpData != null) {
			if (!String.valueOf(inpData).equals("null")) {
				if (!String.valueOf(inpData).equals("")) {
					outData = String.valueOf(inpData);
					return outData;
				}
			}
		}
		return outData;
	}

	public String addmerchantReg2(MerchantMasterMod user, String formmode, String USERID)
			throws UnknownHostException, SQLException {
		String msg = "";
		// System.out.println("Formmode---->" + formmode);
		IPSAuditTable audit = new IPSAuditTable();
		String audit_ref_no = sequence.generateRequestUUId();
		try {
			if (formmode.equals("editsubmit")) {
				Optional<MerchantMaster> up1 = merchantMasterRep.findById(user.getMerchant_id());
				if (up1.isPresent()) {
					if (user.getSend_notify() != (null)) {
						if (!user.getSend_notify().equals("Y")) {
							user.setSend_notify("N");
						}
					} else {
						user.setSend_notify("N");
					}

					String[] feeDesc = user.getFee_desc() == null ? null : user.getFee_desc().split(",");
					String[] feeType = user.getFee_type() == null ? null : user.getFee_type().split(",");
					String[] amountPer = user.getAmount_per() == null ? null : user.getAmount_per().split(",");
					String[] feeFreq = user.getFee_freq() == null ? null : user.getFee_freq().split(",");
					String[] vatCollect = user.getVat_collect() == null ? null : user.getVat_collect().split(",");

					String values = user.getFee_desc();
					if (values != null && values.startsWith(",,")) {
						values = values.substring(2); // Remove the leading ",,"
					}

					user.setFee_desc(values);
					user.setEntry_user(USERID);
					user.setEntry_time(new Date());
					user.setEntity_flg('N');
					user.setModify_user(USERID);
					user.setModify_time(new Date());
					user.setModify_flg('Y');
					user.setDel_flg('N');
					if (!user.getStatus_enable().equals("Enable")) {
						user.setFreeze_flg("Y");
					} else {
						user.setFreeze_flg("N");
					}
					user.setCurr("BWP");
					user.setStatus("Unverified");
					user.setMerchant_acc_no(user.getBank_account_no());
					user.setAuth_user(up1.get().getAuth_user());
					user.setAuth_time(up1.get().getAuth_time());
					user.setPhoto(user.getPhoto());

					merchantMasterModRep.save(user);
					up1.get().setEntity_flg('N');
					up1.get().setDel_flg('Y');
					merchantMasterRep.save(up1.get());
					// System.out.println(" user.getFee_desc()" + user.getFee_desc().toString());
					feeRepoMod.deleteDetails(user.getMerchant_id());
					feeRepo.deleteDetails(user.getMerchant_id());

					if (Objects.nonNull(feeDesc) && Objects.nonNull(feeType) && Objects.nonNull(amountPer)
							&& Objects.nonNull(feeFreq)) {
						for (int i = 0; i < feeType.length; i++) {
							MerchantFeesServiceChargesMod mfc = new MerchantFeesServiceChargesMod();
							mfc.setSrl_no(feeRepo.SrlNo());
							mfc.setMerchant_id(user.getMerchant_id());
							mfc.setFee_type(feeType[i]);
							mfc.setFee_desc(feeDesc[i]);
							mfc.setAmount_per(amountPer[i]);
							mfc.setFee_freq(feeFreq[i]);
							mfc.setVat_collect(vatCollect[i]);
							mfc.setDel_flg("N");
							feeRepoMod.save(mfc);
						}
					}

					audit.setAudit_date(new Date());
					audit.setAudit_table("MERCHANT_MASTER_TABLE");
					audit.setAudit_screen("MERCHANT MASTER - MODIFICATION");
					audit.setFunc_code("MERCHANT MODIFICATION");
					audit.setRemarks(user.getMerchant_legal_id() + " :MERCHANT MODIFIED SUCCESSFULLY");
					audit.setEvent_id(user.getMerchant_legal_id());
					audit.setEvent_name(user.getMerchant_name());
					audit.setAuth_user(USERID);
					audit.setAuth_time(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(USERID);
					audit.setAudit_ref_no(audit_ref_no);
					audit.setModi_details("-");
					ipsAuditTableRep.save(audit);
					msg = "Modified Successfully";
				} else {
					msg = "Merchant Account Not Exist";
				}
			} else if (formmode.equals("verifysubmit")) {
				Optional<MerchantMasterMod> bankList = merchantMasterModRep.findById(user.getMerchant_id());
				/*
				 * List<BIPS_Unit_Mangement_Entity> up2 = BIPS_UnitManagement_Repo
				 * .getUnitlist(bankList.get().getMerchant_id()); for (int i = 0; i <
				 * up2.size(); i++) { BIPS_Unit_Mangement_Entity unit = up2.get(i);
				 * merchantunitverify(bankList.get().getMerchant_id(),
				 * bankList.get().getMerchant_name(), unit.getUnit_id(),
				 * unit.getContact_person1_name(), unit.getContact_person2_name(),
				 * unit.getContact_person3_name(), unit.getContact_person4_name(),
				 * unit.getContact_person5_name(), unit.getContact_person6_name(), USERID); }
				 */

				merchanttopassword(bankList.get().getMerchant_id(), bankList.get().getMerchant_corp_name(),
						bankList.get().getMerchant_name(), bankList.get().getMer_cont_pers(),
						bankList.get().getMer_cont_pers_r2(), bankList.get().getMer_cont_pers_r3(),
						bankList.get().getMer_cont_pers_r4(), bankList.get().getMer_cont_pers_r5(),
						bankList.get().getMer_cont_pers_r6(), bankList.get().getMer_cont_pers_r7(),
						bankList.get().getMer_cont_pers_r8(), bankList.get().getMer_cont_pers_r9(),
						bankList.get().getMer_cont_pers_r10(), USERID);

				if (bankList.isPresent()) {
					bankList.get().setEntity_flg('Y');
					bankList.get().setDel_flg('N');
					bankList.get().setAuth_user(USERID);
					bankList.get().setAuth_time(new Date());
					MerchantMaster Main = new MerchantMaster(bankList.get());
					Main.setEntity_flg('Y');
					Main.setCurr("BWP");
					Main.setPhoto(bankList.get().getPhoto());
					Main.setMerchant_acc_no(Main.getBank_account_no());
					Main.setDetailed_address1(bankList.get().getDetailed_address1());
					Main.setDetailed_address2(bankList.get().getDetailed_address2());
					merchantMasterRep.save(Main);
					merchantMasterModRep.deleteById(bankList.get().getMerchant_id());
					List<MerchantFeesServiceChargesMod> merchantFeesServiceCharges = feeRepoMod
							.merchantDetailsMod(user.getMerchant_id());
					List<MerchantFeesServiceCharges> MFS = new ArrayList<>();
					if (!merchantFeesServiceCharges.isEmpty()) {
						Session session = sessionFactory.getCurrentSession();
						session.beginTransaction();
						String hql = "DELETE FROM MerchantFeesServiceCharges WHERE MERCHANT_ID = :id";
						session.createQuery(hql).setParameter("id", user.getMerchant_id()).executeUpdate();
						session.getTransaction().commit();
						for (int i = 0; i < merchantFeesServiceCharges.size(); i++) {
							MerchantFeesServiceCharges mfc = new MerchantFeesServiceCharges();
							mfc.setSrl_no(merchantFeesServiceCharges.get(i).getSrl_no());
							mfc.setMerchant_id(merchantFeesServiceCharges.get(i).getMerchant_id());
							mfc.setFee_desc(merchantFeesServiceCharges.get(i).getFee_desc());
							mfc.setFee_type(merchantFeesServiceCharges.get(i).getFee_type());
							mfc.setAmount_per(merchantFeesServiceCharges.get(i).getAmount_per());
							mfc.setFee_freq(merchantFeesServiceCharges.get(i).getFee_freq());
							mfc.setVat_collect(merchantFeesServiceCharges.get(i).getVat_collect());
							mfc.setDel_flg(merchantFeesServiceCharges.get(i).getDel_flg());
							MFS.add(mfc);
						}
					}
					feeRepo.saveAll(MFS);
					feeRepoMod.deleteDetails(user.getMerchant_id());
					audit.setAudit_date(new Date());
					audit.setAudit_table("MERCHANT_MASTER_TABLE");
					audit.setAudit_screen("USER PROFILE - VERIFICATION");
					audit.setFunc_code("MERCHANT VERIFICATION");
					audit.setRemarks(user.getMerchant_legal_id() + " :Merchant Verified Successfully");
					audit.setEvent_id(user.getMerchant_legal_id());
					audit.setEvent_name(user.getMerchant_name());
					audit.setAuth_user(USERID);
					audit.setAuth_time(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(USERID);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);
					ipsAuditTableRep.save(audit);
					msg = "Verified Successfully";
				} else {
					msg = "Merchant Account Not Exist";
				}
			} else if (formmode.equals("deletesubmit")) {
				Optional<MerchantMaster> up = merchantMasterRep.findById(user.getMerchant_id());
				if (up.isPresent()) {
					MerchantMaster Dmain = merchantMasterRep.findByIdCustom(user.getMerchant_id());
					Dmain.setEntity_flg('N');
					Dmain.setDel_flg('Y');
					Dmain.setModify_user(USERID);
					Dmain.setModify_time(new Date());
					merchantMasterRep.save(Dmain);
					Session session = sessionFactory.getCurrentSession();
					session.saveOrUpdate(Dmain);
				} else {
					MerchantMasterMod Dmod = merchantMasterModRep.findByIdCustom(user.getMerchant_id());
					Dmod.setEntity_flg('Y');
					Dmod.setDel_flg('Y');
					Dmod.setCurr("BWP");
					Dmod.setModify_user(USERID);
					Dmod.setModify_time(new Date());
					merchantMasterModRep.save(Dmod);
					msg = "Deleted Successfully";
				}
			} else if (formmode.equals("addsubmit")) {
				Optional<MerchantMasterMod> bankList = merchantMasterModRep.findById(user.getMerchant_id());
				if (bankList.isPresent()) {
					msg = "Merchant Account Already Exist";
				} else {
					System.out.println(user.getFee_desc());
					MerchantMasterMod up = user;
					String[] feeDesc = user.getFee_desc() == null ? null : user.getFee_desc().split(",");
					String[] feeType = user.getFee_type() == null ? null : user.getFee_type().split(",");
					String[] amountPer = user.getAmount_per() == null ? null : user.getAmount_per().split(",");
					String[] feeFreq = user.getFee_freq() == null ? null : user.getFee_freq().split(",");
					String[] vatCollect = user.getVat_collect() == null ? null : user.getVat_collect().split(",");
					String[] delFlag = user.getDel_flag() == null ? null : user.getDel_flag().split(",");
					up.setEntity_flg('N');
					up.setModify_flg('N');
					up.setCurr("BWP");
					up.setDel_flg('N');
					up.setEntry_user(USERID);
					up.setEntry_time(new Date());
					if (!user.getStatus_enable().equals("Enable")) {
						user.setFreeze_flg("Y");
					} else {
						user.setFreeze_flg("N");
					}
					if (user.getSend_notify() != (null)) {
						if (user.getSend_notify().equals("Y")) {
							up.setSend_notify("Y");
						} else {
							up.setSend_notify("N");
						}
					} else {
						up.setSend_notify("N");
					}
					user.setMerchant_acc_no(user.getBank_account_no());
					user.setFee_desc(Objects.nonNull(feeDesc) ? feeDesc[0] : null);
					user.setFee_type(Objects.nonNull(feeType) ? feeType[0] : null);
					user.setAmount_per(Objects.nonNull(amountPer) ? amountPer[0] : null);
					user.setFee_freq(Objects.nonNull(feeFreq) ? feeFreq[0] : null);
					user.setVat_collect(Objects.nonNull(vatCollect) ? vatCollect[0] : null);
					user.setDel_flag(Objects.nonNull(delFlag) ? delFlag[0] : null);
					// Save
					merchantMasterModRep.save(up);
					if (Objects.nonNull(feeDesc) && Objects.nonNull(feeType) && Objects.nonNull(amountPer)
							&& Objects.nonNull(feeFreq)) {
						for (int i = 0; i < feeType.length; i++) {
							MerchantFeesServiceChargesMod mfc = new MerchantFeesServiceChargesMod();
							mfc.setSrl_no(feeRepo.SrlNo());
							mfc.setMerchant_id(user.getMerchant_id());
							mfc.setFee_desc(feeDesc[i]);
							mfc.setFee_type(feeType[i]);
							mfc.setAmount_per(amountPer[i]);
							mfc.setFee_freq(feeFreq[i]);
							mfc.setVat_collect(vatCollect[i]);
							mfc.setDel_flg("N");
							feeRepoMod.save(mfc);
						}
					}
					// Audit
					audit.setAudit_date(new Date());
					audit.setAudit_table("MERCHANT_MASTER_TABLE");
					audit.setAudit_screen("MERCHANT - CREATED");
					audit.setFunc_code("MERCHANT CREATION");
					audit.setRemarks(user.getMerchant_legal_id() + " : Merchant Created Successfully");
					audit.setEvent_id(user.getMerchant_legal_id());
					audit.setEvent_name(user.getMerchant_name());
					audit.setAuth_user(USERID);
					audit.setAuth_time(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(USERID);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);
					ipsAuditTableRep.save(audit);
					msg = "Merchant Account Added Successfully";
				}
			}
		} catch (DataIntegrityViolationException ex) {
			return "Data integrity violation: " + ex.getMessage();
		} catch (DataAccessException ex) {
			// System.out.println(" ex.getMessage():" + ex.getMessage());
			return "Data access error: " + ex.getMessage();
		}
		return msg;
	}

	@Autowired
	BIPS_UnitManagement_Repo BIPS_UnitManagement_Repo;

	public String addunit(BIPS_Unit_Mangement_Entity user, String formmode, String USERID) throws UnknownHostException {
		String msg = "";
		// System.out.println("Formmode---->" + formmode);
		try {
			if (formmode.equals("addsubmit")) {
				user.setEntry_user(USERID);
				user.setEntry_time(new Date());
				user.setModify_flag("N");
				user.setEntry_flag("N");
				user.setDel_flg("N");
				BIPS_UnitManagement_Repo.save(user);

				// Audit
				IPSAuditTable audit = new IPSAuditTable();
				String audit_ref_no = sequence.generateRequestUUId();
				audit.setAudit_date(new Date());
				audit.setAudit_table("BIPS_MERCHANT_UNIT_MANAGEMENT");
				audit.setAudit_screen("MERCHANT UNIT - CREATED");
				audit.setFunc_code("MERCHANT UNIT CREATION");
				audit.setRemarks(user.getUnit_id() + " : Merchant Unit Created Successfully");
				audit.setEvent_id(user.getMerchant_user_id());
				audit.setEvent_name(user.getMerchant_name());
				audit.setAuth_user(USERID);
				audit.setAuth_time(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(USERID);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);
				ipsAuditTableRep.save(audit);

				msg = "Unit Added successfully";
			}
		} catch (Exception e) {
			msg = "Error: " + e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}

	public BIPS_Mer_User_Management_Entity getUserId(String id) {

		// System.out.println("getsrlno");
		Session session = sessionFactory.getCurrentSession();
		Query<BIPS_Mer_User_Management_Entity> query = session.createQuery(
				" from BIPS_Mer_User_Management_Entity where user_id=?1 ", BIPS_Mer_User_Management_Entity.class);
		query.setParameter(1, id);

		// System.out.println(id);

		List<BIPS_Mer_User_Management_Entity> result = query.getResultList();

		if (!result.isEmpty()) {

			return result.get(0);
		} else {

			return new BIPS_Mer_User_Management_Entity();
		}

	};

	public String VerifyUserMer(String user_ids, String userID) {
		String msg = "";
		// System.out.println("In the second stage of verificatioh " + user_ids);
		BIPS_Mer_User_Management_Entity roleEntity = bIPS_MerUserManagement_Repo.getbyflg(user_ids);
		if (Objects.nonNull(roleEntity)) {
			// System.out.println("Service in the third stage");
			roleEntity.setVerify_user(userID);
			;
			roleEntity.setVerify_time(new Date());
			roleEntity.setEntry_flag("Y");
			roleEntity.setModify_flag("N");
			bIPS_MerUserManagement_Repo.save(roleEntity);

			IPSAuditTable audit = new IPSAuditTable();
			String audit_ref_no = sequence.generateRequestUUId();
			audit.setAudit_date(new Date());
			audit.setAudit_table("BIPS_MERCHANT_USER_MANAGEMENT");
			audit.setAudit_screen("MERCHANT USER - VERIFIED");
			audit.setFunc_code("MERCHANT USER VERIFICATION");
			audit.setRemarks(roleEntity.getUser_id() + " : MERCHANT USER VERIFIED SUCCESSFULLY");
			audit.setEvent_id(roleEntity.getMerchant_user_id());
			audit.setEvent_name(roleEntity.getMerchant_name());
			audit.setAuth_user(userID);
			audit.setAuth_time(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);

			msg = "User Verified Successfully";

		}
		return msg;
	}

	@Autowired
	BIPS_MerDeviceManagement_Repo BIPS_MerDeviceManagement_Repo;

	public String VerifyDeviceMer(String user_ids, String userID) {

		String msg = "";
		// System.out.println("In the second stage of verificatioh " + user_ids);
		BIPS_Mer_Device_Management_Entity roleEntity = BIPS_MerDeviceManagement_Repo.getbyflg(user_ids);
		if (Objects.nonNull(roleEntity)) {
			// System.out.println("Service in the second stage");
			roleEntity.setVerify_user(userID);
			;
			roleEntity.setVerify_time(new Date());
			roleEntity.setEntry_flag("Y");
			roleEntity.setModify_flag("N");
			BIPS_MerDeviceManagement_Repo.save(roleEntity);

			IPSAuditTable audit = new IPSAuditTable();
			String audit_ref_no = sequence.generateRequestUUId();
			audit.setAudit_date(new Date());
			audit.setAudit_table("BIPS_MERCHANT_DEVICE_MANAGEMENT");
			audit.setAudit_screen("MERCHANT DEVICE - VERIFIED");
			audit.setFunc_code("MERCHANT DEVICE VERIFICATION");
			audit.setRemarks(roleEntity.getDevice_id() + " : MERCHANT DEVICE VERIFIED SUCCESSFULLY");
			audit.setEvent_id(roleEntity.getMerchant_user_id());
			audit.setEvent_name(roleEntity.getMerchant_name());
			audit.setAuth_user(userID);
			audit.setAuth_time(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);

			msg = "Device Verified Successfully";

		}
		return msg;
	}

	public String VerifyPassMer(String rep_id, String userID) {
		String msg = "";
		BIPS_Password_Management_Entity reppass = bIPS_PasswordManagement_Repo.getbyflg(rep_id);
		if (Objects.nonNull(reppass)) {
			reppass.setVerify_user(userID);
			reppass.setVerify_time(new Date());
			reppass.setEntry_flag("Y");
			reppass.setModify_flag("N");
			bIPS_PasswordManagement_Repo.save(reppass);

			IPSAuditTable audit = new IPSAuditTable();
			String audit_ref_no = sequence.generateRequestUUId();
			audit.setAudit_date(new Date());
			audit.setAudit_table("BIPS_MERCHANT_USER_MANAGEMENT");
			audit.setAudit_screen("MERCHANT USER - VERIFIED");
			audit.setFunc_code("MERCHANT USER VERIFICATION");
			audit.setRemarks(reppass.getMerchant_rep_id() + " : MERCHANT USER VERIFIED SUCCESSFULLY");
			audit.setEvent_id(reppass.getMerchant_user_id());
			audit.setEvent_name(reppass.getMerchant_name());
			audit.setAuth_user(userID);
			audit.setAuth_time(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);

			msg = "Password Verified Successfully";
		}
		return msg;
	}

	@Autowired
	IPSChargesAndFeesRepository IPSChargesAndFeesRepository;

	public String VerifyServiceMer(String userID, String user_ids) {
		String msg = "";
		IPSChargesAndFees roleEntity = IPSChargesAndFeesRepository.getbyflg(user_ids);
		if (Objects.nonNull(roleEntity)) {
			roleEntity.setVerify_user(userID);
			roleEntity.setVerify_time(new Date());
			roleEntity.setEntity_flg("Y");
			roleEntity.setModify_flg("N");
			IPSChargesAndFeesRepository.save(roleEntity);
			msg = "Service charges and fees verified successfully";
		} else {
			msg = "No record found for verify";
		}
		return msg;
	}

//	public String merchantunitverify(String MerUserId, String MerchantName, String UnitId, String ContactPerson1,
//			String ContactPerson2, String ContactPerson3, String ContactPerson4, String ContactPerson5,
//			String ContactPerson6, String USERID) throws SQLException {
//		String userID = USERID;
//		String password = env.getProperty("user.password");
//		try {
//			List<BIPS_Unit_Mangement_Entity> up2 = BIPS_UnitManagement_Repo.merctopas(UnitId);
//			List<BIPS_Password_Management_Entity> up3 = new ArrayList<>();
//			if (UnitId != null && ContactPerson1 != null && !ContactPerson1.trim().isEmpty()) {
//				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
//					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
//					LocalDate currentDate1 = LocalDate.now();
//					// Calculate the account expiry date by adding 1 year to the current date
//					LocalDate accountExpiryDate = currentDate1.plusYears(1);
//					// Convert LocalDate to Date
//					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
//					// Set the account_expiry_date attribute of salarymain
//					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
//					salarymain.setAlternate_email_id(salsecond.getEmail_id());
//					salarymain.setAlternate_mobile_no(null);
//					salarymain.setDel_flag("N");
//					salarymain.setPassword_life("180");
//					salarymain.setEmail_address(salsecond.getContact_person1_email());
//					salarymain.setEntry_time(new Date());
//					salarymain.setEntry_user(userID);
//					salarymain.setLogin_channel("WEB");
//					salarymain.setLogin_status("N");
//					salarymain.setMaker_or_checker("CHECKER");
//					salarymain.setNo_of_attmp(0);
//					salarymain.setUser_locked_flg("N");
//					salarymain.setEntry_flag("Y");
//					salarymain.setModify_flag("N");
//					salarymain.setUser_category("Representative");
//					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
//					salarymain.setMerchant_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R01");
//					salarymain.setMer_representative_name(salsecond.getContact_person1_name());
//					salarymain.setMobile_no(salsecond.getContact_person1_mobile());
//					salarymain.setModify_time(salsecond.getModify_time());
//					salarymain.setModify_user(salsecond.getModify_user());
//					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
//					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
//					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
//					salarymain.setPassword(encryptedPassword);
//					LocalDate today = LocalDate.now();
//					// Add 30 days to today's date
//					LocalDate expiryDate = today.plusDays(180);
//					// Set the password expiry date
//					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
//					LocalDate currentDate = LocalDate.now();
//					// Parse the password_life from string to integer
//					// Set the password_expiry_date attribute of salarymain
//					salarymain.setUnit_id(salsecond.getUnit_id());
//					salarymain.setUnit_name(salsecond.getUnit_name());
//					salarymain.setUnit_type(salsecond.getUnit_type());
//					salarymain.setUser_disable_flag("N");
//					salarymain.setPwlog_flg("UNIT");
//					salarymain.setUser_status("Active");
//					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
//					salarymain.setVerify_time(salsecond.getVerify_time());
//					salarymain.setVerify_user(salsecond.getVerify_user());
//					up3.add(salarymain);
//					bIPS_PasswordManagement_Repo.saveAll(up3);
//				}
//			}
//
//			if (UnitId != null && ContactPerson2 != null && !ContactPerson2.trim().isEmpty()) {
//				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
//					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
//					LocalDate currentDate1 = LocalDate.now();
//					// Calculate the account expiry date by adding 1 year to the current date
//					LocalDate accountExpiryDate = currentDate1.plusYears(1);
//					// Convert LocalDate to Date
//					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
//					// Set the account_expiry_date attribute of salarymain
//					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
//					salarymain.setAlternate_email_id(salsecond.getEmail_id());
//					salarymain.setEmail_address(salsecond.getContact_person2_email());
//					salarymain.setAlternate_mobile_no(null);
//					salarymain.setDel_flag("N");
//					salarymain.setEntry_time(new Date());
//					salarymain.setEntry_user(userID);
//					salarymain.setLogin_channel("WEB");
//					salarymain.setLogin_status("N");
//					salarymain.setMaker_or_checker("CHECKER");
//					salarymain.setNo_of_attmp(0);
//					salarymain.setUser_locked_flg("N");
//					salarymain.setEntry_flag("N");
//					salarymain.setModify_flag("N");
//					salarymain.setEntry_flag("Y");
//					salarymain.setModify_flag("N");
//					salarymain.setPassword_life("180");
//					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
//					salarymain.setMerchant_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R02");
//					salarymain.setMer_representative_name(salsecond.getContact_person2_name());
//					salarymain.setMobile_no(salsecond.getContact_person2_mobile());
//					salarymain.setModify_time(salsecond.getModify_time());
//					salarymain.setModify_user(salsecond.getModify_user());
//					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
//					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
//					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
//					salarymain.setPassword(encryptedPassword);
//					LocalDate today = LocalDate.now();
//					// Add 30 days to today's date
//					LocalDate expiryDate = today.plusDays(180);
//					// Set the password expiry date
//					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
//					LocalDate currentDate = LocalDate.now();
//					// Parse the password_life from string to integer
//					salarymain.setUnit_id(salsecond.getUnit_id());
//					salarymain.setUnit_name(salsecond.getUnit_name());
//					salarymain.setUnit_type(salsecond.getUnit_type());
//					salarymain.setUser_disable_flag("N");
//					salarymain.setPwlog_flg("UNIT");
//					salarymain.setUser_status("ACTIVE");
//					salarymain.setUser_category("Representative");
//					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
//					salarymain.setVerify_time(salsecond.getVerify_time());
//					salarymain.setVerify_user(salsecond.getVerify_user());
//					up3.add(salarymain);
//					bIPS_PasswordManagement_Repo.saveAll(up3);
//				}
//			}
//
//			if (UnitId != null && ContactPerson3 != null && !ContactPerson3.trim().isEmpty()) {
//				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
//					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
//					LocalDate currentDate1 = LocalDate.now();
//					// Calculate the account expiry date by adding 1 year to the current date
//					LocalDate accountExpiryDate = currentDate1.plusYears(1);
//					// Convert LocalDate to Date
//					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
//					// Set the account_expiry_date attribute of salarymain
//					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
//					salarymain.setAlternate_email_id(salsecond.getEmail_id());
//					salarymain.setEmail_address(salsecond.getContact_person3_email());
//					salarymain.setAlternate_mobile_no(null);
//					salarymain.setDel_flag("N");
//					salarymain.setEntry_time(new Date());
//					salarymain.setEntry_user(userID);
//					salarymain.setLogin_channel("WEB");
//					salarymain.setLogin_status("N");
//					salarymain.setMaker_or_checker("CHECKER");
//					salarymain.setNo_of_attmp(0);
//					salarymain.setUser_locked_flg("N");
//					salarymain.setEntry_flag("Y");
//					salarymain.setModify_flag("N");
//					salarymain.setPassword_life("180");
//					salarymain.setUser_category("Representative");
//					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
//					salarymain.setMerchant_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R03");
//					salarymain.setMer_representative_name(salsecond.getContact_person3_name());
//					salarymain.setMobile_no(salsecond.getContact_person3_mobile());
//					salarymain.setModify_time(salsecond.getModify_time());
//					salarymain.setModify_user(salsecond.getModify_user());
//					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
//					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
//					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
//					salarymain.setPassword(encryptedPassword);
//					LocalDate today = LocalDate.now();
//					// Add 30 days to today's date
//					LocalDate expiryDate = today.plusDays(180);
//					// Set the password expiry date
//					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
//					LocalDate currentDate = LocalDate.now();
//					// Parse the password_life from string to integer
//					salarymain.setUnit_id(salsecond.getUnit_id());
//					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
//					salarymain.setUnit_name(salsecond.getUnit_name());
//					salarymain.setUnit_type(salsecond.getUnit_type());
//					salarymain.setUser_disable_flag("N");
//					salarymain.setPwlog_flg("UNIT");
//					salarymain.setUser_status("ACTIVE");
//					salarymain.setVerify_time(salsecond.getVerify_time());
//					salarymain.setVerify_user(salsecond.getVerify_user());
//					up3.add(salarymain);
//					bIPS_PasswordManagement_Repo.saveAll(up3);
//				}
//			}
//
//			if (UnitId != null && ContactPerson4 != null && !ContactPerson4.trim().isEmpty()) {
//				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
//					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
//					LocalDate currentDate1 = LocalDate.now();
//					// Calculate the account expiry date by adding 1 year to the current date
//					LocalDate accountExpiryDate = currentDate1.plusYears(1);
//					// Convert LocalDate to Date
//					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
//					// Set the account_expiry_date attribute of salarymain
//					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
//					salarymain.setAlternate_email_id(salsecond.getEmail_id());
//					salarymain.setEmail_address(salsecond.getContact_person4_email());
//					salarymain.setAlternate_mobile_no(null);
//					salarymain.setDel_flag("N");
//					salarymain.setEntry_time(new Date());
//					salarymain.setEntry_user(userID);
//					salarymain.setLogin_channel("WEB");
//					salarymain.setLogin_status("N");
//					salarymain.setMaker_or_checker("CHECKER");
//					salarymain.setNo_of_attmp(0);
//					salarymain.setUser_locked_flg("N");
//					salarymain.setEntry_flag("N");
//					salarymain.setModify_flag("N");
//					salarymain.setEntry_flag("Y");
//					salarymain.setModify_flag("N");
//					salarymain.setPassword_life("180");
//					salarymain.setUser_category("Representative");
//					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
//					salarymain.setMerchant_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R04");
//					salarymain.setMer_representative_name(salsecond.getContact_person4_name());
//					salarymain.setMobile_no(salsecond.getContact_person4_mobile());
//					salarymain.setModify_time(salsecond.getModify_time());
//					salarymain.setModify_user(salsecond.getModify_user());
//					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
//					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
//					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
//					salarymain.setPassword(encryptedPassword);
//					LocalDate today = LocalDate.now();
//					// Add 30 days to today's date
//					LocalDate expiryDate = today.plusDays(180);
//					// Set the password expiry date
//					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
//					LocalDate currentDate = LocalDate.now();
//					// Parse the password_life from string to integer
//					salarymain.setUnit_id(salsecond.getUnit_id());
//					salarymain.setUnit_name(salsecond.getUnit_name());
//					salarymain.setUnit_type(salsecond.getUnit_type());
//					salarymain.setUser_disable_flag("N");
//					salarymain.setPwlog_flg("UNIT");
//					salarymain.setUser_status("ACTIVE");
//					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
//					salarymain.setVerify_time(salsecond.getVerify_time());
//					salarymain.setVerify_user(salsecond.getVerify_user());
//					up3.add(salarymain);
//					bIPS_PasswordManagement_Repo.saveAll(up3);
//				}
//			}
//
//			if (UnitId != null && ContactPerson5 != null && !ContactPerson5.trim().isEmpty()) {
//				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
//					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
//					LocalDate currentDate1 = LocalDate.now();
//					// Calculate the account expiry date by adding 1 year to the current date
//					LocalDate accountExpiryDate = currentDate1.plusYears(1);
//					// Convert LocalDate to Date
//					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
//					// Set the account_expiry_date attribute of salarymain
//					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
//					salarymain.setAlternate_email_id(salsecond.getEmail_id());
//					salarymain.setEmail_address(salsecond.getContact_person5_email());
//					salarymain.setAlternate_mobile_no(null);
//					salarymain.setDel_flag("N");
//					salarymain.setEntry_time(new Date());
//					salarymain.setEntry_user(userID);
//					salarymain.setLogin_channel("WEB");
//					salarymain.setLogin_status("N");
//					salarymain.setMaker_or_checker("CHECKER");
//					salarymain.setNo_of_attmp(0);
//					salarymain.setUser_locked_flg("N");
//					salarymain.setEntry_flag("N");
//					salarymain.setModify_flag("N");
//					salarymain.setEntry_flag("Y");
//					salarymain.setModify_flag("N");
//					salarymain.setPassword_life("180");
//					salarymain.setUser_category("Representative");
//					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
//					salarymain.setMerchant_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R05");
//					salarymain.setMer_representative_name(salsecond.getContact_person5_name());
//					salarymain.setMobile_no(salsecond.getContact_person5_mobile());
//					salarymain.setModify_time(salsecond.getModify_time());
//					salarymain.setModify_user(salsecond.getModify_user());
//					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
//					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
//					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
//					salarymain.setPassword(encryptedPassword);
//					LocalDate today = LocalDate.now();
//					// Add 30 days to today's date
//					LocalDate expiryDate = today.plusDays(180);
//					// Set the password expiry date
//					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
//					LocalDate currentDate = LocalDate.now();
//					// Parse the password_life from string to integer
//					salarymain.setUnit_id(salsecond.getUnit_id());
//					salarymain.setUnit_name(salsecond.getUnit_name());
//					salarymain.setUnit_type(salsecond.getUnit_type());
//					salarymain.setUser_disable_flag("N");
//					salarymain.setPwlog_flg("UNIT");
//					salarymain.setUser_status("ACTIVE");
//					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
//					salarymain.setVerify_time(salsecond.getVerify_time());
//					salarymain.setVerify_user(salsecond.getVerify_user());
//					up3.add(salarymain);
//					bIPS_PasswordManagement_Repo.saveAll(up3);
//				}
//			}
//
//			if (UnitId != null && ContactPerson6 != null && !ContactPerson6.trim().isEmpty()) {
//				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
//					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
//					LocalDate currentDate1 = LocalDate.now();
//					// Calculate the account expiry date by adding 1 year to the current date
//					LocalDate accountExpiryDate = currentDate1.plusYears(1);
//					// Convert LocalDate to Date
//					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
//					// Set the account_expiry_date attribute of salarymain
//					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
//					salarymain.setAlternate_email_id(salsecond.getEmail_id());
//					salarymain.setEmail_address(salsecond.getContact_person6_email());
//					salarymain.setAlternate_mobile_no(null);
//					salarymain.setDel_flag("N");
//					salarymain.setEntry_time(new Date());
//					salarymain.setEntry_user(userID);
//					salarymain.setLogin_channel("WEB");
//					salarymain.setLogin_status("N");
//					salarymain.setMaker_or_checker("CHECKER");
//					salarymain.setNo_of_attmp(0);
//					salarymain.setUser_locked_flg("N");
//					salarymain.setEntry_flag("N");
//					salarymain.setModify_flag("N");
//					salarymain.setEntry_flag("Y");
//					salarymain.setModify_flag("N");
//					salarymain.setPassword_life("180");
//					salarymain.setUser_category("Representative");
//					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
//					salarymain.setMerchant_name(salsecond.getMerchant_name());
//					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R06");
//					salarymain.setMer_representative_name(salsecond.getContact_person6_name());
//					salarymain.setMobile_no(salsecond.getContact_person6_mobile());
//					salarymain.setModify_time(salsecond.getModify_time());
//					salarymain.setModify_user(salsecond.getModify_user());
//					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
//					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
//					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
//					salarymain.setPassword(encryptedPassword);
//					LocalDate today = LocalDate.now();
//					// Add 30 days to today's date
//					LocalDate expiryDate = today.plusDays(180);
//					// Set the password expiry date
//					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
//					LocalDate currentDate = LocalDate.now();
//					// Parse the password_life from string to integer
//					salarymain.setUnit_id(salsecond.getUnit_id());
//					salarymain.setUnit_name(salsecond.getUnit_name());
//					salarymain.setUnit_type(salsecond.getUnit_type());
//					salarymain.setUser_disable_flag("N");
//					salarymain.setPwlog_flg("UNIT");
//					salarymain.setUser_status("ACTIVE");
//					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
//					salarymain.setVerify_time(salsecond.getVerify_time());
//					salarymain.setVerify_user(salsecond.getVerify_user());
//					up3.add(salarymain);
//					bIPS_PasswordManagement_Repo.saveAll(up3);
//				}
//			}
//
//			if (up2.size() > 0 && up3.size() > 0) {
//				up2.get(0).setEntry_flag("Y");
//				up2.get(0).setVerify_time(new Date());
//				up2.get(0).setVerify_user(userID);
//				bIPS_UnitManagement_Repo.save(up2.get(0));
//				bIPS_PasswordManagement_Repo.saveAll(up3);
//				return "Unit Verified Successfully";
//			} else {
//				return "Unit Verified UnSuccessfully";
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Error occurred while processing the request";
//		}
//	}

	public String merchanttopassword(String MerLegalId, String MerCorpName, String MerchantName, String MerRepId1,
			String MerRepId2, String MerRepId3, String MerRepId4, String MerRepId5, String MerRepId6, String MerRepId7,
			String MerRepId8, String MerRepId9, String MerRepId10, String USERID) throws SQLException {

		String userID = USERID;
		String password = env.getProperty("user.password");

		try {

			MerchantMasterMod up2 = merchantMasterModRep.findByIdCustom(MerLegalId);
			List<BIPS_Password_Management_Entity> up3 = new ArrayList<>();

			if (Objects.nonNull(up2)) {
				if (Objects.nonNull(MerRepId1)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r1());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r1());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r1());
					salarymain.setMer_representative_name(up2.getMer_cont_pers());
					salarymain.setMobile_no(up2.getMer_ph_no());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no());
					salarymain.setEmail_address(up2.getMer_email_addr_r1());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId2)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r1());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r2());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r2());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r2());
					salarymain.setMobile_no(up2.getMer_ph_no_r2());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r2());
					salarymain.setEmail_address(up2.getMer_email_addr_r2());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId3)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r3());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r3());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r3());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r3());
					salarymain.setMobile_no(up2.getMer_ph_no_r3());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r3());
					salarymain.setEmail_address(up2.getMer_email_addr_r3());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId4)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r4());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r4());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r4());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r4());
					salarymain.setMobile_no(up2.getMer_ph_no_r4());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r4());
					salarymain.setEmail_address(up2.getMer_email_addr_r4());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId5)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r5());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r5());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r5());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r5());
					salarymain.setMobile_no(up2.getMer_ph_no_r5());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r5());
					salarymain.setEmail_address(up2.getMer_email_addr_r5());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId6)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r6());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r6());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r6());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r6());
					salarymain.setMobile_no(up2.getMer_ph_no_r6());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r6());
					salarymain.setEmail_address(up2.getMer_email_addr_r6());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId7)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r7());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r7());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r7());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r7());
					salarymain.setMobile_no(up2.getMer_ph_no_r7());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r7());
					salarymain.setEmail_address(up2.getMer_email_addr_r7());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId8)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r8());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r8());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r8());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r8());
					salarymain.setMobile_no(up2.getMer_ph_no_r8());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r8());
					salarymain.setEmail_address(up2.getMer_email_addr_r8());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId9)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r9());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r9());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r9());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r9());
					salarymain.setMobile_no(up2.getMer_ph_no_r9());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r9());
					salarymain.setEmail_address(up2.getMer_email_addr_r9());
					up3.add(salarymain);
				}

				if (Objects.nonNull(MerRepId10)) {
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(up2.getMerchant_legal_id());
					salarymain.setMerchant_name(up2.getMerchant_name());
					salarymain.setMerchant_corporate_name(up2.getMerchant_corp_name());
					salarymain.setMerchant_user_id(up2.getMerchant_legal_id());
					salarymain.setUser_disable_flag("N");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					salarymain.setPassword_life("180");
					salarymain.setDel_flag("N");
					salarymain.setUser_status("ACTIVE");
					salarymain.setLogin_status("N");
					salarymain.setLogin_channel("WEB");
					salarymain.setPwlog_flg("MERCHANT");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setEntry_user(userID);
					salarymain.setEntry_time(new Date());
					salarymain.setNo_of_concurrent_users(new BigDecimal("1"));
					salarymain.setNo_of_active_devices(new BigDecimal("1"));
					LocalDate currentDate = LocalDate.now();
					LocalDate passwordExpiryDate = currentDate
							.plusDays(Integer.parseInt(salarymain.getPassword_life()));
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}
					LocalDate accountExpiryDate = currentDate.plusYears(1);
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setCountrycode(up2.getPh_countrycode_r10());
					salarymain.setAlt_countrycode(up2.getOfc_countrycode_r10());
					salarymain.setMerchant_rep_id(up2.getMer_user_id_r10());
					salarymain.setMer_representative_name(up2.getMer_cont_pers_r10());
					salarymain.setMobile_no(up2.getMer_ph_no_r10());
					salarymain.setAlternate_mobile_no(up2.getMer_ofc_no_r10());
					salarymain.setEmail_address(up2.getMer_email_addr_r10());
					up3.add(salarymain);
				}
			}
			bIPS_PasswordManagement_Repo.saveAll(up3);
			return "Successfully processed the request";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occurred while processing the request";
		}
	}

}
