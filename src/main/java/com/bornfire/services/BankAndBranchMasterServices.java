package com.bornfire.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import com.bornfire.entity.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
					user.setCurr("MUR");
					user.setStatus("Unverified");
					user.setHr_holdreject_flg('N');
					user.setMerchant_acc_no(user.getBank_account_no());
					user.setAuth_user(up1.get().getAuth_user());
					user.setAuth_time(up1.get().getAuth_time());
					user.setType_maucas(up1.get().getType_maucas());
					user.setType_upi(up1.get().getType_upi());
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
					Main.setCurr("MUR");
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
					Dmod.setCurr("MUR");
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
					up.setCurr("MUR");
					up.setDel_flg('N');
					up.setHr_holdreject_flg('N');
					up.setEntry_user(USERID);
					up.setEntry_time(new Date());
					up.setModify_user(USERID);
					up.setModify_time(new Date());
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
	
	public String UploadgstserviceCOLLECTION(String screenId, MultipartFile file, String userid,
			MerchantMasterMod MerchantMasterMod)
			throws SQLException, FileNotFoundException, IOException, NullPointerException {
		System.out.println("Entering third Service Succesfully of GST EXCEL UPLOAD");

		String fileName = file.getOriginalFilename();

		String fileExt = "";
		String msg = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			fileExt = fileName.substring(i + 1);
		}

		if (fileExt.equals("xlsx") || fileExt.equals("xls")) {

			try {	
				Workbook workbook = WorkbookFactory.create(file.getInputStream());

				List<HashMap<Integer, String>> mapList = new ArrayList<HashMap<Integer, String>>();
				for (Sheet s : workbook) {
					for (Row r : s) {

						if (!isRowEmpty(r)) {
							if (r.getRowNum() == 0) {
								continue;
							}

							HashMap<Integer, String> map = new HashMap<>();

							for (int j = 0; j < 200; j++) {

								Cell cell = r.getCell(j);
								DataFormatter formatter = new DataFormatter();
								String text = formatter.formatCellValue(cell);
								map.put(j, text);
							}
							mapList.add(map);

						}

					}

				}

				for (HashMap<Integer, String> item : mapList) {

					MerchantMasterMod PO = new MerchantMasterMod(); 
					
					String datePattern = "MM/dd/yy"; // Correct pattern for dates like "12/25/24"
					SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

					String merchant_id  = item.get(0);
					System.out.println("merchant_id: " + merchant_id);  

					String mer_corpName = item.get(1);
					System.out.println("mer_corpName: " + mer_corpName);

					String merchant_name = item.get(2);
					System.out.println("merchant_name: " + merchant_name); 
					
					String regaddress1 = item.get(3);
					System.out.println("regaddress1: " + regaddress1);    
					
					String mer_mode = item.get(4);
					System.out.println("mer_mode: " + mer_mode);    
					
					String regaddress2 = item.get(5);
					System.out.println("regaddress2: " + regaddress2);    
					
					String regaddress3 = item.get(6);
					System.out.println("regaddress3: " + regaddress3);  
					
					String merPartner = item.get(7);
					System.out.println("merPartner: " + merPartner);  
					
					 
					String brn_no = item.get(8);
					System.out.println("brn_no: " + brn_no);

					String brn_date = item.get(9);
					System.out.println("brn_date: " + brn_date);  
					Date date_value1 = null;

					if (brn_date != null && !brn_date.isEmpty()) {
					    try {
					    	date_value1 = dateFormat.parse(brn_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value1: " + date_value1);
					
					String tran_amt = item.get(10);
					System.out.println("tran_amt: " + tran_amt); 
					
					String mer_city = item.get(11);
					System.out.println("mer_city: " + mer_city); 

					String mer_status = item.get(12);
					System.out.println("mer_status: " + mer_status); 
					
					String chargeback_approval = item.get(13);
					System.out.println("chargeback_approval: " + chargeback_approval);
					
					BigDecimal chargeback_amt = new BigDecimal(item.get(14));
					System.out.println("chargeback_amt: " + chargeback_amt); 
					
					String repid1  = item.get(15);
					System.out.println("repid1: " + repid1);  

					String contpers1 = item.get(16);
					System.out.println("contpers1: " + contpers1);

					String ph_countrycode1 = item.get(17);
					System.out.println("ph_countrycode1: " + ph_countrycode1); 
					
					BigDecimal phno1 = new BigDecimal(item.get(18));
					System.out.println("phno1: " + phno1); 
					
					String ofc_countrycode1  = item.get(19);
					System.out.println("ofc_countrycode1: " + ofc_countrycode1);  
 
					BigDecimal ofcno1 = new BigDecimal(item.get(20));
					System.out.println("ofcno1: " + ofcno1); 

					String emailid1 = item.get(21);
					System.out.println("emailid1: " + emailid1); 
					
					String notify_mode1 = item.get(22);
					System.out.println("notify_mode1: " + notify_mode1); 
					
					String repid2  = item.get(23);
					System.out.println("repid2: " + repid2);  

					String contpers2 = item.get(24);
					System.out.println("contpers2: " + contpers2);

					String ph_countrycode2 = item.get(25);
					System.out.println("ph_countrycode2: " + ph_countrycode2); 
					
					BigDecimal phno2 = new BigDecimal(item.get(26));
					System.out.println("phno2: " + phno2); 
					
					String ofc_countrycode2  = item.get(27);
					System.out.println("ofc_countrycode2: " + ofc_countrycode2);  

					BigDecimal ofcno2 = new BigDecimal(item.get(28));
					System.out.println("ofcno2: " + ofcno2);

					String emailid2 = item.get(29);
					System.out.println("emailid2: " + emailid2); 
					
					String notify_mode2 = item.get(30);
					System.out.println("notify_mode2: " + notify_mode2); 
					
					String repid3  = item.get(31);
					System.out.println("repid3: " + repid3);  

					String contpers3 = item.get(32);
					System.out.println("contpers3: " + contpers3);

					String ph_countrycode3 = item.get(33);
					System.out.println("ph_countrycode3: " + ph_countrycode3); 
					
					BigDecimal phno3 = new BigDecimal(item.get(34));
					System.out.println("phno3: " + phno3); 
					
					String ofc_countrycode3  = item.get(35);
					System.out.println("ofc_countrycode3: " + ofc_countrycode3);  

					BigDecimal ofcno3 = new BigDecimal(item.get(36));
					System.out.println("ofcno3: " + ofcno3);

					String emailid3 = item.get(37);
					System.out.println("emailid3: " + emailid3); 
					
					String notify_mode3 = item.get(38);
					System.out.println("notify_mode3: " + notify_mode3); 
					
					String repid4  = item.get(39);
					System.out.println("repid4: " + repid4);  

					String contpers4 = item.get(40);
					System.out.println("contpers4: " + contpers4);

					String ph_countrycode4 = item.get(41);
					System.out.println("ph_countrycode4: " + ph_countrycode4); 
					
					BigDecimal phno4 = new BigDecimal(item.get(42));
					System.out.println("phno4: " + phno4); 
					
					String ofc_countrycode4  = item.get(43);
					System.out.println("ofc_countrycode4: " + ofc_countrycode4);  

					BigDecimal ofcno4 = new BigDecimal(item.get(44));
					System.out.println("ofcno4: " + ofcno4);

					String emailid4 = item.get(45);
					System.out.println("emailid4: " + emailid4); 
					
					String notify_mode4 = item.get(46);
					System.out.println("notify_mode4: " + notify_mode4); 
					
					String repid5  = item.get(47);
					System.out.println("repid5: " + repid5);  

					String contpers5 = item.get(48);
					System.out.println("contpers5: " + contpers5);

					String ph_countrycode5 = item.get(49);
					System.out.println("ph_countrycode5: " + ph_countrycode5); 
					
					BigDecimal phno5 = new BigDecimal(item.get(50));
					System.out.println("phno5: " + phno5); 
					
					String ofc_countrycode5  = item.get(51);
					System.out.println("ofc_countrycode5: " + ofc_countrycode5);  

					BigDecimal ofcno5 = new BigDecimal(item.get(52));
					System.out.println("ofcno5: " + ofcno5);

					String emailid5 = item.get(53);
					System.out.println("emailid5: " + emailid5); 
					
					String notify_mode5 = item.get(54);
					System.out.println("notify_mode1: " + notify_mode1); 
					
					String repid6  = item.get(55);
					System.out.println("repid6: " + repid6);  

					String contpers6 = item.get(56);
					System.out.println("contpers6: " + contpers6);

					String ph_countrycode6 = item.get(57);
					System.out.println("ph_countrycode6: " + ph_countrycode6); 
					
					BigDecimal phno6 = new BigDecimal(item.get(58));
					System.out.println("phno6: " + phno6); 
					
					String ofc_countrycode6  = item.get(59);
					System.out.println("ofc_countrycode6: " + ofc_countrycode6);  
 
					BigDecimal ofcno6 = new BigDecimal(item.get(60));
					System.out.println("ofcno6: " + ofcno6); 

					String emailid6 = item.get(61);
					System.out.println("emailid6: " + emailid6); 
					
					String notify_mode6 = item.get(62);
					System.out.println("notify_mode6: " + notify_mode6); 
					
					String repid7  = item.get(63);
					System.out.println("repid7: " + repid7);  

					String contpers7 = item.get(64);
					System.out.println("contpers7: " +contpers7);

					String ph_countrycode7 = item.get(65);
					System.out.println("ph_countrycode7: " + ph_countrycode7); 
					
					BigDecimal phno7 = new BigDecimal(item.get(66));
					System.out.println("phno7: " + phno7); 
					
					String ofc_countrycode7  = item.get(67);
					System.out.println("ofc_countrycode7: " + ofc_countrycode7);  

					BigDecimal ofcno7 = new BigDecimal(item.get(68));
					System.out.println("ofcno7: " + ofcno7);

					String emailid7 = item.get(69);
					System.out.println("emailid7: " + emailid7); 
					
					String notify_mode7 = item.get(70);
					System.out.println("notify_mode7: " + notify_mode7); 
					
					String repid8  = item.get(71);
					System.out.println("repid8: " + repid8);  

					String contpers8 = item.get(72);
					System.out.println("contpers8: " + contpers8);

					String ph_countrycode8 = item.get(73);
					System.out.println("ph_countrycode8: " + ph_countrycode8); 
					
					BigDecimal phno8 = new BigDecimal(item.get(74));
					System.out.println("phno8: " + phno8); 
					
					String ofc_countrycode8  = item.get(75);
					System.out.println("ofc_countrycode8: " + ofc_countrycode8);  

					BigDecimal ofcno8 = new BigDecimal(item.get(76));
					System.out.println("ofcno8: " + ofcno8);

					String emailid8 = item.get(77);
					System.out.println("emailid8: " + emailid8); 
					
					String notify_mode8 = item.get(78);
					System.out.println("notify_mode8: " + notify_mode8); 
					
					String repid9  = item.get(79);
					System.out.println("repid9: " + repid9);  

					String contpers9 = item.get(80);
					System.out.println("contpers9: " + contpers9);

					String ph_countrycode9 = item.get(81);
					System.out.println("ph_countrycode9: " + ph_countrycode9); 
					
					BigDecimal phno9 = new BigDecimal(item.get(82));
					System.out.println("phno9: " + phno9); 
					
					String ofc_countrycode9  = item.get(83);
					System.out.println("ofc_countrycode9: " + ofc_countrycode9);  

					BigDecimal ofcno9 = new BigDecimal(item.get(84));
					System.out.println("ofcno9: " + ofcno9);

					String emailid9 = item.get(85);
					System.out.println("emailid9: " + emailid9); 
					
					String notify_mode9 = item.get(86);
					System.out.println("notify_mode9: " + notify_mode9); 
					
					String repid10  = item.get(87);
					System.out.println("repid10: " + repid10);  

					String contpers10 = item.get(88);
					System.out.println("contpers10: " + contpers10);

					String ph_countrycode10 = item.get(89);
					System.out.println("ph_countrycode10: " + ph_countrycode10); 
					
					BigDecimal phno10 = new BigDecimal(item.get(90));
					System.out.println("phno10: " + phno10); 
					
					String ofc_countrycode10  = item.get(91);
					System.out.println("ofc_countrycode10: " + ofc_countrycode10);  

					BigDecimal ofcno10 = new BigDecimal(item.get(92));
					System.out.println("ofcno10: " + ofcno10);

					String emailid10 = item.get(93);
					System.out.println("emailid10: " + emailid10); 
					
					String notify_mode10 = item.get(94);
					System.out.println("notify_mode10: " + notify_mode10); 
					
					String mer_bipsno  = item.get(95);
					System.out.println("mer_bipsno: " + mer_bipsno);  
					
					String mer_terminal  = item.get(96);
					System.out.println("mer_terminal: " + mer_terminal);  

					String mer_type = item.get(97);
					System.out.println("mer_type: " + mer_type);

					String outlet_location = item.get(98);
					System.out.println("outlet_location: " + outlet_location); 
					
					String Mer_Categorycode = item.get(99);
					System.out.println("Mer_Categorycode: " + Mer_Categorycode); 
					
					String Mer_Categorydesc = item.get(100);
					System.out.println("Mer_Categorydesc: " + Mer_Categorydesc); 
					
					String address1 = item.get(101);
					System.out.println("address1: " + address1); 
					
					String address2 = item.get(102);
					System.out.println("address2: " + address2); 
					
					String address3 = item.get(103);
					System.out.println("address3: " + address3); 
					
					String type_qraccept  = item.get(104);
					System.out.println("type_qraccept: " + type_qraccept);  
					
					String tip_conv_indic  = item.get(105);
					System.out.println("tip_conv_indic: " + tip_conv_indic);
					
					String conv_fee_type  = item.get(106);
					System.out.println("conv_fee_type: " + conv_fee_type);
					
					String value_conv_fee  = item.get(107);
					System.out.println("value_conv_fee: " + value_conv_fee);
					
					String loyalty_num  = item.get(108);
					System.out.println("loyalty_num: " + loyalty_num);
					
					String purp_of_tran  = item.get(109);
					System.out.println("purp_of_tran: " + purp_of_tran);
					
					String static_dynamic  = item.get(110);
					System.out.println("static_dynamic: " + static_dynamic);

					String version = item.get(111);
					System.out.println("version: " + version);

					String modes = item.get(112);
					System.out.println("modes: " + modes); 
					
					String purpose = item.get(113);
					System.out.println("purpose: " + purpose);
					
					String original_id = item.get(114);
					System.out.println("original_id: " + original_id);
					
					String tran_id  = item.get(115);
					System.out.println("tran_id: " + tran_id);  

					String tran_ref = item.get(116);
					System.out.println("tran_ref: " + tran_ref);

					String tran_note = item.get(117);
					System.out.println("tran_note: " + tran_note); 
					
					String payee_upiid = item.get(118);
					System.out.println("payee_upiid: " + payee_upiid); 
					
					String store_id = item.get(119);
					System.out.println("store_id: " + store_id); 
					
					String Mer_terminalId  = item.get(120);
					System.out.println("Mer_terminalId: " + Mer_terminalId);
					
					String trans_mandat_amt  = item.get(121);
					System.out.println("trans_mandat_amt: " + trans_mandat_amt);

					String Tran_CrncyCode = item.get(122);
					System.out.println("Tran_CrncyCode: " + Tran_CrncyCode); 
					
					String mer_invoice_no  = item.get(123);
					System.out.println("mer_invoice_no: " + mer_invoice_no);
					
					String mer_invoice_date = item.get(124);
					System.out.println("mer_invoice_date: " + mer_invoice_date);
					Date inv_date = null; 
					if (mer_invoice_date != null && !mer_invoice_date.isEmpty()) {
					    try {
					    	inv_date = dateFormat.parse(mer_invoice_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("inv_date: " + inv_date);
					
					String mer_invoice_name  = item.get(125);
					System.out.println("mer_invoice_name: " + mer_invoice_name);
					
					String QR_expirydate = item.get(126);
					System.out.println("QR_expirydate: " + QR_expirydate);
					Date date_value = null; 
					if (QR_expirydate != null && !QR_expirydate.isEmpty()) {
					    try {
					        date_value = dateFormat.parse(QR_expirydate);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value: " + date_value);
					
					String mer_amt  = item.get(127);
					System.out.println("mer_amt: " + mer_amt);
					
					String Mer_pincode = item.get(128);
					System.out.println("Mer_pincode: " + Mer_pincode); 
					
					String Mer_Tier  = item.get(129);
					System.out.println("Mer_Tier: " + Mer_Tier);  

					String Mer_Trantype = item.get(130);
					System.out.println("Mer_Trantype: " + Mer_Trantype);

					String QR_medium = item.get(131);
					System.out.println("QR_medium: " + QR_medium); 
					
					String tip_conv_upi  = item.get(132);
					System.out.println("tip_conv_upi: " + tip_conv_upi);  

					String conv_fee_type_upi = item.get(133);
					System.out.println("conv_fee_type_upi: " + conv_fee_type_upi);

					String value_conv_fee_upi = item.get(134);
					System.out.println("value_conv_fee_upi: " + value_conv_fee_upi); 
					
					String bank_name1 = item.get(135);
					System.out.println("bank_name1: " + bank_name1); 
					
					String bank_branch1 = item.get(136);
					System.out.println("bank_branch1: " + bank_branch1);
					
					String iban1 = item.get(137);
					System.out.println("iban1: " + iban1); 
					
					String settlement_freq1 = item.get(138);
					System.out.println("settlement_freq1: " + settlement_freq1);
					
					String settlement_day1 = item.get(139);
					System.out.println("settlement_day1: " + settlement_day1);
					
					String settlement_date1 = item.get(139);
					System.out.println("settlement_date1: " + settlement_date1);
					
					String bank_acctno1 = item.get(140);
					System.out.println("bank_acctno1: " + bank_acctno1); 
					
					String bank_acctName1 = item.get(141);
					System.out.println("bank_acctName1: " + bank_acctName1);
					
					String currency1 = item.get(142);
					System.out.println("currency1: " + currency1);
					
					String bank_address1 = item.get(143);
					System.out.println("bank_address1: " + bank_address1);
					
					String last_settle_dt1 = item.get(144);
					System.out.println("last_settle_dt1: " + last_settle_dt1);
					Date last_dt1 = null; 
					if (last_settle_dt1 != null && !last_settle_dt1.isEmpty()) {
					    try {
					    	last_dt1 = dateFormat.parse(last_settle_dt1);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("last_dt1: " + last_dt1);
					
					String next_settle_dt1 = item.get(145);
					System.out.println("next_settle_dt1: " + next_settle_dt1);
					Date next_dt1 = null; 
					if (next_settle_dt1 != null && !next_settle_dt1.isEmpty()) {
					    try {
					    	next_dt1 = dateFormat.parse(next_settle_dt1);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("next_dt1: " + next_dt1);
					
					String lien_amt1 = item.get(146);
					System.out.println("lien_amt1: " + lien_amt1); 
					
					String hold_amt1 = item.get(147);
					System.out.println("hold_amt1: " + hold_amt1);   
					
					String bank_name2 = item.get(148);
					System.out.println("bank_name2: " + bank_name2); 
					
					String bank_branch2 = item.get(149);
					System.out.println("bank_branch2: " + bank_branch2);
					
					String iban2 = item.get(150);
					System.out.println("iban2: " + iban2); 
					
					String settlement_freq2 = item.get(151);
					System.out.println("settlement_freq2: " + settlement_freq2);
					
					String settlement_day2 = item.get(152);
					System.out.println("settlement_day2: " + settlement_day2);
					
					String settlement_date2 = item.get(152);
					System.out.println("settlement_date2: " + settlement_date2);
					
					String bank_acctno2 = item.get(153);
					System.out.println("bank_acctno2: " + bank_acctno2); 
					
					String bank_acctName2 = item.get(154);
					System.out.println("bank_acctName2: " + bank_acctName2);
					
					String currency2 = item.get(155);
					System.out.println("currency2: " + currency2);
					
					String bank_address2 = item.get(156);
					System.out.println("bank_address2: " + bank_address2);
					
					String last_settle_dt2 = item.get(157);
					System.out.println("last_settle_dt2: " + last_settle_dt2);
					Date last_dt2 = null; 
					if (last_settle_dt2 != null && !last_settle_dt2.isEmpty()) {
					    try {
					    	last_dt2 = dateFormat.parse(last_settle_dt2);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("last_dt2: " + last_dt2);
					
					String next_settle_dt2 = item.get(158);
					System.out.println("next_settle_dt2: " + next_settle_dt2);
					Date next_dt2 = null; 
					if (next_settle_dt2 != null && !next_settle_dt2.isEmpty()) {
					    try {
					    	next_dt2 = dateFormat.parse(next_settle_dt2);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("next_dt2: " + next_dt2);
					
					String lien_amt2 = item.get(159);
					System.out.println("lien_amt2: " + lien_amt2); 
					
					String hold_amt2 = item.get(160);
					System.out.println("hold_amt2: " + hold_amt2);
					
					String fee_desc = item.get(161);
					System.out.println("fee_desc: " + fee_desc); 
					
					String fee_deriv = item.get(162);
					System.out.println("fee_deriv: " + fee_deriv);
					
					String amt_percent_slab = item.get(163);
					System.out.println("amt_percent_slab: " + amt_percent_slab); 
					
					String fee_freq = item.get(164);
					System.out.println("fee_freq: " + fee_freq);
					
					String fee_type = item.get(165);
					System.out.println("fee_type: " + fee_type);  
					
					PO.setMerchant_legal_id(merchant_id);
					PO.setMerchant_id(merchant_id); 
					PO.setMerchant_corp_name(mer_corpName);
					PO.setMerchant_name(merchant_name);
					PO.setMerchant_addr(regaddress1);
					PO.setMerchant_mode(mer_mode);
					PO.setMerchant_addr_2(regaddress2);
					PO.setMerchant_addr_3(regaddress3);
					PO.setMer_partner(merPartner);
					PO.setBrn_no(brn_no);
					PO.setBrn_date(date_value1);
					PO.setTransaction_amount(tran_amt);
					PO.setMerchant_city(mer_city); 
					PO.setStatus_enable(mer_status);
					PO.setStatus_disable(mer_status); 
					PO.setChargeback_approval(chargeback_approval);
					PO.setChargeback_amount(chargeback_amt);
					
					PO.setMer_user_id_r1(repid1);
					PO.setMer_cont_pers(contpers1);
					PO.setPh_countrycode_r1(ph_countrycode1);
					PO.setMer_ph_no(phno1);
					PO.setOfc_countrycode_r1(ofc_countrycode1);
					PO.setMer_ofc_no(ofcno1);
					PO.setMer_email_addr_r1(emailid1);
					PO.setMer_notifi_mode(notify_mode1);
					
					PO.setMer_user_id_r2(repid1);
					PO.setMer_cont_pers_r2(contpers1);
					PO.setPh_countrycode_r2(ph_countrycode1);
					PO.setMer_ph_no_r2(phno2);
					PO.setOfc_countrycode_r2(ofc_countrycode2);
					PO.setMer_ofc_no_r2(ofcno2);
					PO.setMer_email_addr_r2(emailid2);
					PO.setMer_notifi_mode_r2(notify_mode2);

					PO.setMer_user_id_r3(repid3);
					PO.setMer_cont_pers_r3(contpers3);
					PO.setPh_countrycode_r3(ph_countrycode3);
					PO.setMer_ph_no_r3(phno3);
					PO.setOfc_countrycode_r3(ofc_countrycode3);
					PO.setMer_ofc_no_r3(ofcno3);
					PO.setMer_email_addr_r3(emailid3);
					PO.setMer_notifi_mode_r3(notify_mode3);
					
					PO.setMer_user_id_r4(repid4);
					PO.setMer_cont_pers_r4(contpers4);
					PO.setPh_countrycode_r4(ph_countrycode4);
					PO.setMer_ph_no_r4(phno4);
					PO.setOfc_countrycode_r4(ofc_countrycode4);
					PO.setMer_ofc_no_r4(ofcno4);
					PO.setMer_email_addr_r4(emailid4);
					PO.setMer_notifi_mode_r4(notify_mode4);
					
					PO.setMer_user_id_r5(repid5);
					PO.setMer_cont_pers_r5(contpers5);
					PO.setPh_countrycode_r5(ph_countrycode5);
					PO.setMer_ph_no_r5(phno5);
					PO.setOfc_countrycode_r5(ofc_countrycode5);
					PO.setMer_ofc_no_r5(ofcno5);
					PO.setMer_email_addr_r5(emailid5);
					PO.setMer_notifi_mode_r5(notify_mode5);
					
					PO.setMer_user_id_r6(repid6);
					PO.setMer_cont_pers_r6(contpers6);
					PO.setPh_countrycode_r6(ph_countrycode6);
					PO.setMer_ph_no_r6(phno6);
					PO.setOfc_countrycode_r6(ofc_countrycode6);
					PO.setMer_ofc_no_r6(ofcno6);
					PO.setMer_email_addr_r6(emailid6);
					PO.setMer_notifi_mode_r6(notify_mode6);
					
					PO.setMer_user_id_r7(repid7);
					PO.setMer_cont_pers_r7(contpers7);
					PO.setPh_countrycode_r7(ph_countrycode7);
					PO.setMer_ph_no_r7(phno7);
					PO.setOfc_countrycode_r7(ofc_countrycode7);
					PO.setMer_ofc_no_r7(ofcno7);
					PO.setMer_email_addr_r7(emailid7);
					PO.setMer_notifi_mode_r7(notify_mode7);

					PO.setMer_user_id_r8(repid8);
					PO.setMer_cont_pers_r8(contpers8);
					PO.setPh_countrycode_r8(ph_countrycode8);
					PO.setMer_ph_no_r8(phno8);
					PO.setOfc_countrycode_r8(ofc_countrycode8);
					PO.setMer_ofc_no_r8(ofcno8);
					PO.setMer_email_addr_r8(emailid8);
					PO.setMer_notifi_mode_r8(notify_mode8);

					PO.setMer_user_id_r9(repid9);
					PO.setMer_cont_pers_r9(contpers9);
					PO.setPh_countrycode_r9(ph_countrycode9);
					PO.setMer_ph_no_r9(phno9);
					PO.setOfc_countrycode_r9(ofc_countrycode9);
					PO.setMer_ofc_no_r9(ofcno9);
					PO.setMer_email_addr_r9(emailid9);
					PO.setMer_notifi_mode_r9(notify_mode9);

					PO.setMer_user_id_r10(repid10);
					PO.setMer_cont_pers_r10(contpers10);
					PO.setPh_countrycode_r10(ph_countrycode10);
					PO.setMer_ph_no_r10(phno10);
					PO.setOfc_countrycode_r10(ofc_countrycode10);
					PO.setMer_ofc_no_r10(ofcno10);
					PO.setMer_email_addr_r10(emailid10);
					PO.setMer_notifi_mode_r10(notify_mode10);
					 
					PO.setMerchant_id(mer_bipsno);
					PO.setMerchant_terminal(Mer_terminalId);
					PO.setMerchant_type(mer_type);
					PO.setMerchant_location(outlet_location);
					PO.setMerchant_cat_code(Mer_Categorycode);
					PO.setMerchant_cat_desc(Mer_Categorydesc);
					PO.setMerchant_out_addr_1(address1);
					PO.setMerchant_out_addr_2(address2);
					PO.setMerchant_out_addr_3(address3);
					PO.setType_maucas(type_qraccept);
					//PO.setType_upi(type_qraccept);
					PO.setTip_or_conv_indicator(tip_conv_indic);
					PO.setConv_fees_type(conv_fee_type);
					PO.setValue_conv_fees(value_conv_fee);
					PO.setLoyalty_number(loyalty_num);
					PO.setPurpose_of_tran(purp_of_tran);
					PO.setStatic_field(static_dynamic);
					PO.setVersion(version);
					PO.setModes(modes);
					PO.setPurpose(purpose);
					PO.setOrgid(original_id);
					PO.setTid(tran_id);
					PO.setTr(tran_ref);
					PO.setTn(tran_note);
					PO.setPa(payee_upiid);
					PO.setMsid(store_id);
					PO.setMtid(Mer_terminalId);
					PO.setBam(trans_mandat_amt);
					PO.setCurr(Tran_CrncyCode);
					PO.setInvoiceno(mer_invoice_no);
					PO.setInvoicedate(inv_date);
					PO.setInvoicename(mer_invoice_name);
					PO.setQrexpire(date_value);
					PO.setAm(mer_amt);
					PO.setPincode(Mer_pincode);
					PO.setTier(Mer_Tier);
					PO.setTran_type(Mer_Trantype);
					PO.setQrmedium(QR_medium);
					PO.setTip_or_conv_indicator_upi(tip_conv_upi);
					PO.setConv_fees_type_upi(conv_fee_type_upi);
					PO.setValue_conv_fees_upi(value_conv_fee_upi);
					PO.setBank_name(bank_name1);
					PO.setBank_branch(bank_branch1);
					PO.setIban(iban1);
					PO.setSettlement_frequency(settlement_freq1);
					PO.setSettlement_day(settlement_day1);
					PO.setSettlement_date(settlement_date1);
					PO.setBank_account_no(bank_acctno1);
					PO.setBank_name(bank_name1);
					PO.setCurrency(currency1); 
					PO.setDetailed_address1(bank_address1);
					PO.setLast_settlementdate1(last_dt1);
					PO.setNext_settlementdate1(next_dt1);
					PO.setLien_amount1(lien_amt1);
					PO.setHold_amount1(hold_amt1);
					PO.setBank_name2(bank_name2);
					PO.setBank_branch2(bank_branch2);
					PO.setIban2(iban2);
					PO.setSettlement_frequency2(settlement_freq2);
					PO.setSettlement_day2(settlement_day2);
					PO.setSettlement_date2(settlement_date2);
					PO.setBank_account_no2(bank_acctno2);
					PO.setBank_name2(bank_name2);
					PO.setCurrency2(currency2);
					PO.setDetailed_address2(bank_address2);
					PO.setLast_settlementdate2(last_dt2);
					PO.setNext_settlementdate2(next_dt2);
					PO.setLien_amount2(lien_amt2);
					PO.setHold_amount2(hold_amt2);
					
					PO.setEntity_flg('N'); 
					PO.setDel_flag("N");
					PO.setHr_holdreject_flg('N');
					PO.setModify_flg('N');
					PO.setEntry_user(userid);
					PO.setEntry_time(new Date());

					merchantMasterModRep.save(PO);
					
					BIPS_Unit_Mangement_Entity UN = new BIPS_Unit_Mangement_Entity(); 
					
					String unit_id = item.get(76);
					System.out.println("unit_id: " + unit_id);
					
					String unit_type = item.get(77);
					System.out.println("unit_type: " + unit_type); 
					
					String unit_brn_no = item.get(78);
					System.out.println("unit_brn_no: " + unit_brn_no); 
					
					String unit_brn_date = item.get(79);
					System.out.println("brn_date: " + unit_brn_date);  
					Date date_value2 = null;

					if (unit_brn_date != null && !unit_brn_date.isEmpty()) {
					    try {
					    	date_value2 = dateFormat.parse(unit_brn_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value2: " + date_value2);
					
					String unit_name = item.get(80);
					System.out.println("unit_name: " + unit_name);
					
					String unit_location = item.get(81);
					System.out.println("unit_location: " + unit_location); 
					
					String unit_city = item.get(82);
					System.out.println("unit_city: " + unit_city); 
					
					String unit_country = item.get(83);
					System.out.println("unit_country: " + unit_country); 
					
					String unit_countrycode_phone_no = item.get(84);
					System.out.println("unit_countrycode_phone_no: " + unit_countrycode_phone_no); 
					
					String unit_phone_no = item.get(85);
					System.out.println("unit_phone_no: " + unit_phone_no); 
					
					String unit_head = item.get(86);
					System.out.println("unit_head: " + unit_head); 
					
					String unit_designation = item.get(87);
					System.out.println("unit_designation: " + unit_designation); 
					
					String contact_pers1 = item.get(88);
					System.out.println("contact_pers1: " + contact_pers1); 
					
					String countrycode_pers_1 = item.get(89);
					System.out.println("countrycode_pers_1: " + countrycode_pers_1); 
					 
					String mob_no1 = item.get(90);
					System.out.println("mob_no1: " + mob_no1);
					
					String email_id1 = item.get(91);
					System.out.println("email_id1: " + email_id1);  
					
					UN.setUnit_id(unit_id);
					UN.setUnit_type(unit_type);
					UN.setBrn_no(unit_brn_no);
					UN.setMerchant_user_id(merchant_id);
					UN.setMerchant_name(merchant_name);
					UN.setBrn_date(date_value2);
					UN.setUnit_name(unit_name);
					UN.setLocation_detail(unit_location);
					UN.setCity(unit_city);
					UN.setCountry(unit_country);
					UN.setPh_countrycode(unit_countrycode_phone_no);
					UN.setPhone_no(unit_phone_no);
					UN.setBranch_head(unit_head);
					UN.setDesignation(unit_designation);
					UN.setContact_person1_name(contact_pers1);
					UN.setCp1_countrycode(countrycode_pers_1);
					UN.setEmail_id(email_id1);
					//UN.setContact_person1_mobile(mob_no1);
					UN.setDel_flg("N");
					
					bIPS_UnitManagement_Repo.save(UN);
					
					BIPS_Mer_User_Management_Entity US = new BIPS_Mer_User_Management_Entity(); 
					
					String user_ids = item.get(92);
					System.out.println("user_ids: " + user_ids); 
					
					String user_name = item.get(93);
					System.out.println("user_name: " + user_name); 
					
					String user_designation = item.get(94);
					System.out.println("user_designation: " + user_designation); 
					
					String user_role = item.get(95);
					System.out.println("user_role: " + user_role); 
					
					String pass_exp_date = item.get(96);
					System.out.println("pass_exp_date: " + pass_exp_date);  
					Date date_value3 = null;

					if (pass_exp_date != null && !pass_exp_date.isEmpty()) {
					    try {
					    	date_value3 = dateFormat.parse(pass_exp_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value3: " + date_value3);
					
					String acct_exp_date = item.get(97);
					System.out.println("acct_exp_date: " + acct_exp_date); 
					Date date_value4 = null;

					if (acct_exp_date != null && !acct_exp_date.isEmpty()) {
					    try {
					    	date_value4 = dateFormat.parse(acct_exp_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value4: " + date_value4);
					
					String user_status = item.get(98);
					System.out.println("user_status: " + user_status); 
					
					String mob_countrycode = item.get(99);
					System.out.println("mob_countrycode: " + mob_countrycode); 
					
					String mobile_no = item.get(100);
					System.out.println("mobile_no: " + mobile_no); 
					 
					String email_id = item.get(101);
					System.out.println("email_id: " + email_id); 
					 
					US.setMerchant_user_id(merchant_id);
					US.setMerchant_name(merchant_name);
					US.setUser_id(user_ids);
					US.setUser_name(user_name);
					US.setUser_designation(user_designation);
					US.setUser_role(user_role);
					US.setPassword_expiry_date1(date_value3);
					US.setAccount_expiry_date1(date_value4);
					US.setUser_status1(user_status);
					US.setCountrycode(mob_countrycode);
					US.setMobile_no1(mobile_no);
					US.setEmail_address1(email_id);
					US.setDel_flag1("N");
					US.setUnit_id_u(unit_id);
					US.setUnit_name_u(unit_name);
					
					bIPS_MerUserManagement_Repo.save(US);
					
					BIPS_Mer_Device_Management_Entity DV = new BIPS_Mer_Device_Management_Entity(); 
					
					String device_id = item.get(102);
					System.out.println("device_id: " + device_id);
					
					String device_name = item.get(103);
					System.out.println("device_name: " + device_name); 
					
					String device_identification = item.get(104);
					System.out.println("device_identification: " + device_identification); 
					
					String device_modal = item.get(105);
					System.out.println("device_modal: " + device_modal); 
					
					String device_location = item.get(106);
					System.out.println("device_location: " + device_location); 
					
					String device_status = item.get(107);
					System.out.println("device_status: " + device_status); 
					
					String terminal_id = item.get(108);
					System.out.println("terminal_id: " + terminal_id); 
					
					DV.setMerchant_user_id(merchant_id);
					DV.setMerchant_name(merchant_name);
					DV.setDevice_id(device_id);
					DV.setDevice_name(device_name);
					DV.setDevice_identification_no(device_identification);
					DV.setDevice_model(device_modal);
					DV.setLocation(device_location);
					DV.setDevice_status(device_status);
					DV.setTerminal_id(terminal_id);
					DV.setDel_flg("N");
					DV.setUnit_id_d(unit_id);
					DV.setUnit_name_d(unit_name);
					
					BIPS_MerDeviceManagement_Repo.save(DV);
					


					msg = "Excel Data Uploaded Successfully";
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = "Merchant File has not been uploaded";
			}
		}
		return msg;

	}
	
	private boolean isRowEmpty(Row row) {
		boolean isEmpty = true;
		DataFormatter dataFormatter = new DataFormatter();

		if (row != null) {
			for (Cell cell : row) {
				if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
					isEmpty = false;
					break;
				}
			}
		}
		return isEmpty;
	}
	
	public String Uploadunit(String screenId, MultipartFile file, String userid,String merchant_id,String merchant_name,
			MerchantMasterMod MerchantMasterMod)
			throws SQLException, FileNotFoundException, IOException, NullPointerException {
		System.out.println("Entering third Service Succesfully of GST EXCEL UPLOAD");
		System.out.println("MerIdusing " + merchant_id);
		System.out.println("MerNameusing " + merchant_name);

		String fileName = file.getOriginalFilename();

		String fileExt = "";
		String msg = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			fileExt = fileName.substring(i + 1);
		}

		if (fileExt.equals("xlsx") || fileExt.equals("xls")) {

			try {	
				Workbook workbook = WorkbookFactory.create(file.getInputStream());

				List<HashMap<Integer, String>> mapList = new ArrayList<HashMap<Integer, String>>();
				for (Sheet s : workbook) {
					for (Row r : s) {

						if (!isRowEmpty(r)) {
							if (r.getRowNum() == 0) {
								continue;
							}

							HashMap<Integer, String> map = new HashMap<>();

							for (int j = 0; j < 200; j++) {

								Cell cell = r.getCell(j);
								DataFormatter formatter = new DataFormatter();
								String text = formatter.formatCellValue(cell);
								map.put(j, text);
							}
							mapList.add(map);

						}

					}

				}

				for (HashMap<Integer, String> item : mapList) { 
					
					BIPS_Unit_Mangement_Entity UN = new BIPS_Unit_Mangement_Entity(); 
					
					String datePattern = "MM/dd/yy"; // Correct pattern for dates like "12/25/24"
					SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
					
					String unit_id = item.get(0);
					System.out.println("unit_id: " + unit_id);
					
					String unit_type = item.get(1);
					System.out.println("unit_type: " + unit_type); 
					
					String unit_brn_no = item.get(2);
					System.out.println("unit_brn_no: " + unit_brn_no); 
					
					String unit_brn_date = item.get(3);
					System.out.println("brn_date: " + unit_brn_date);  
					Date date_value2 = null;

					if (unit_brn_date != null && !unit_brn_date.isEmpty()) {
					    try {
					    	date_value2 = dateFormat.parse(unit_brn_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value2: " + date_value2);
					
					String unit_name = item.get(4);
					System.out.println("unit_name: " + unit_name);
					
					String unit_location = item.get(5);
					System.out.println("unit_location: " + unit_location); 
					
					String unit_city = item.get(6);
					System.out.println("unit_city: " + unit_city); 
					
					String unit_country = item.get(7);
					System.out.println("unit_country: " + unit_country); 
					
					String unit_countrycode_phone_no = item.get(8);
					System.out.println("unit_countrycode_phone_no: " + unit_countrycode_phone_no); 
					
					String unit_phone_no = item.get(9);
					System.out.println("unit_phone_no: " + unit_phone_no); 
					
					String unit_head = item.get(10);
					System.out.println("unit_head: " + unit_head); 
					
					String unit_designation = item.get(11);
					System.out.println("unit_designation: " + unit_designation); 
					
					String contact_pers1 = item.get(12);
					System.out.println("contact_pers1: " + contact_pers1); 
					
					String countrycode_pers_1 = item.get(13);
					System.out.println("countrycode_pers_1: " + countrycode_pers_1);  
					
					BigDecimal mob_no1 = new BigDecimal(item.get(14));
					System.out.println("mob_no1: " + mob_no1);
					
					String email_id1 = item.get(15);
					System.out.println("email_id1: " + email_id1);  
					
					UN.setUnit_id(unit_id);
					UN.setUnit_type(unit_type);
					UN.setBrn_no(unit_brn_no);
					UN.setMerchant_user_id(merchant_id);
					UN.setMerchant_name(merchant_name);
					UN.setBrn_date(date_value2);
					UN.setUnit_name(unit_name);
					UN.setLocation_detail(unit_location);
					UN.setCity(unit_city);
					UN.setCountry(unit_country);
					UN.setPh_countrycode(unit_countrycode_phone_no);
					UN.setPhone_no(unit_phone_no);
					UN.setBranch_head(unit_head);
					UN.setDesignation(unit_designation);
					UN.setContact_person1_name(contact_pers1);
					UN.setCp1_countrycode(countrycode_pers_1);
					UN.setContact_person1_mobile(mob_no1);
					UN.setContact_person1_email(email_id1);
					
					UN.setDel_flg("N");
					UN.setEntry_flag("N");
					UN.setModify_flag("N");
					UN.setEntry_user(userid);
					UN.setEntry_time(new Date());
					UN.setModify_user(userid);
					UN.setModify_time(new Date());
					
					bIPS_UnitManagement_Repo.save(UN);
					msg = "Excel Data Uploaded Successfully";
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = "Unit File has not been uploaded";
			}
		}
		return msg;

	}
	
	public String Uploaduser(String screenId, MultipartFile file, String userid, String merchant_id, String merchant_name,
			MerchantMasterMod MerchantMasterMod)
			throws SQLException, FileNotFoundException, IOException, NullPointerException {
		System.out.println("Entering third Service Succesfully of GST EXCEL UPLOAD");

		String fileName = file.getOriginalFilename();

		String fileExt = "";
		String msg = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			fileExt = fileName.substring(i + 1);
		}

		if (fileExt.equals("xlsx") || fileExt.equals("xls")) {

			try {	
				Workbook workbook = WorkbookFactory.create(file.getInputStream());

				List<HashMap<Integer, String>> mapList = new ArrayList<HashMap<Integer, String>>();
				for (Sheet s : workbook) {
					for (Row r : s) {

						if (!isRowEmpty(r)) {
							if (r.getRowNum() == 0) {
								continue;
							}

							HashMap<Integer, String> map = new HashMap<>();

							for (int j = 0; j < 200; j++) {

								Cell cell = r.getCell(j);
								DataFormatter formatter = new DataFormatter();
								String text = formatter.formatCellValue(cell);
								map.put(j, text);
							}
							mapList.add(map);

						}

					}

				}

				for (HashMap<Integer, String> item : mapList) {
					String password = env.getProperty("user.password");
					BIPS_Mer_User_Management_Entity US = new BIPS_Mer_User_Management_Entity(); 
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					
					String datePattern = "MM/dd/yy"; // Correct pattern for dates like "12/25/24"
					SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
					
					String user_ids = item.get(0);
					System.out.println("user_ids: " + user_ids); 
					
					String user_name = item.get(1);
					System.out.println("user_name: " + user_name); 
					
					String user_designation = item.get(2);
					System.out.println("user_designation: " + user_designation); 
					
					String user_role = item.get(3);
					System.out.println("user_role: " + user_role); 
					
					String pass_exp_date = item.get(4);
					System.out.println("pass_exp_date: " + pass_exp_date);  
					Date date_value3 = null;

					if (pass_exp_date != null && !pass_exp_date.isEmpty()) {
					    try {
					    	date_value3 = dateFormat.parse(pass_exp_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value3: " + date_value3);
					
					String acct_exp_date = item.get(5);
					System.out.println("acct_exp_date: " + acct_exp_date); 
					Date date_value4 = null;

					if (acct_exp_date != null && !acct_exp_date.isEmpty()) {
					    try {
					    	date_value4 = dateFormat.parse(acct_exp_date);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value4: " + date_value4);
					
					String user_status = item.get(6);
					System.out.println("user_status: " + user_status); 
					
					String mob_countrycode = item.get(7);
					System.out.println("mob_countrycode: " + mob_countrycode); 
					
					String mobile_no = item.get(8);
					System.out.println("mobile_no: " + mobile_no); 
					 
					String email_id = item.get(9);
					System.out.println("email_id: " + email_id); 
					
					String unit_id = item.get(10);
					System.out.println("unit_id: " + unit_id); 
					 
					String unit_name = item.get(11);
					System.out.println("unit_name: " + unit_name); 

					String makerorchecker = item.get(12);
					System.out.println("makerorchecker: " + makerorchecker);  
					
					String unit_type = item.get(13);
					System.out.println("unit_type: " + unit_type);  
					
					String user_disable_fromdate = item.get(14);
					System.out.println("user_disable_fromdate: " + user_disable_fromdate); 
					Date date_value5 = null; 
					if (user_disable_fromdate != null && !user_disable_fromdate.isEmpty()) {
					    try {
					    	date_value5 = dateFormat.parse(user_disable_fromdate);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value5: " + date_value5); 
					
					String user_disable_todate = item.get(15);
					System.out.println("user_disable_todate: " + user_disable_todate);
					Date date_value6 = null; 
					if (user_disable_fromdate != null && !user_disable_todate.isEmpty()) {
					    try {
					    	date_value6 = dateFormat.parse(user_disable_todate);
					    } catch (ParseException e) {
					        e.printStackTrace();
					    }
					} else {
					    System.out.println("Record Date is null");
					} 
					System.out.println("date_value6: " + date_value6); 
					 
					String alter_countrycode = item.get(16);
					System.out.println("alter_countrycode: " + alter_countrycode); 
 
					BigDecimal alter_mobno = new BigDecimal(item.get(17));
					System.out.println("alter_mobno: " + alter_mobno); 
					
					String alter_emailid = item.get(18);
					System.out.println("alter_emailid" + alter_emailid); 
					 
					String alter_devid1 = item.get(19);
					System.out.println("alter_devid1: " + alter_devid1); 
					
					String alter_devid2 = item.get(20);
					System.out.println("alter_devid2: " + alter_devid2); 
					 
					String default_devid = item.get(21);
					System.out.println("default_devid: " + default_devid); 

					String remarks = item.get(22);
					System.out.println("remarks: " + remarks); 
					 
					US.setMerchant_user_id(merchant_id);
					US.setMerchant_name(merchant_name);
					US.setUser_id(user_ids);
					US.setUser_name(user_name);
					US.setUser_designation(user_designation);
					US.setUser_role(user_role);
					US.setPassword_expiry_date1(date_value3);
					US.setAccount_expiry_date1(date_value4);
					US.setUser_status1(user_status);
					US.setCountrycode(mob_countrycode);
					US.setMobile_no1(mobile_no);
					US.setEmail_address1(email_id);
					US.setUnit_id_u(unit_id);
					US.setUnit_name_u(unit_name);
					US.setMake_or_checker(makerorchecker);
					US.setUnit_type_u(unit_type);
					US.setUser_disable_from_date1(date_value5);
					US.setUser_disable_to_date1(date_value6);
					US.setAlt_countrycode(alter_countrycode);
					US.setAlternate_mobile_no1(alter_mobno);
					US.setAlternate_email_id1(alter_emailid);
					US.setAlternative_device_id1(alter_devid1);
					US.setAlternative_device_id2(alter_devid2);
					US.setDefault_device_id(default_devid);
					US.setRemarks(remarks);
					
					US.setDel_flag1("N");
					US.setEntry_user(userid);
					US.setEntry_time(new Date());
					US.setModify_user(userid);
					US.setModify_time(new Date());
					US.setLogin_status1("N");
					US.setLogin_channel1("WEB");	
					US.setEntry_flag("N");
					US.setModify_flag("N");
					US.setNo_of_attmp("0");
					US.setUser_locked_flg("N");
					US.setUser_category("USER");
					US.setPassword_life1("180");
					US.setPassword1(encryptedPassword);
					US.setUser_disable_flag1("N");
					
					bIPS_MerUserManagement_Repo.save(US); 
					msg = "Excel Data Uploaded Successfully";
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = "User File has not been uploaded";
			}
		}
		return msg;

	}
	
	public String Uploaddevice(String screenId, MultipartFile file, String userid, String merchant_id, String merchant_name,
			MerchantMasterMod MerchantMasterMod)
			throws SQLException, FileNotFoundException, IOException, NullPointerException {
		System.out.println("Entering third Service Succesfully of GST EXCEL UPLOAD");

		String fileName = file.getOriginalFilename();

		String fileExt = "";
		String msg = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			fileExt = fileName.substring(i + 1);
		}

		if (fileExt.equals("xlsx") || fileExt.equals("xls")) {

			try {	
				Workbook workbook = WorkbookFactory.create(file.getInputStream());

				List<HashMap<Integer, String>> mapList = new ArrayList<HashMap<Integer, String>>();
				for (Sheet s : workbook) {
					for (Row r : s) {

						if (!isRowEmpty(r)) {
							if (r.getRowNum() == 0) {
								continue;
							}

							HashMap<Integer, String> map = new HashMap<>();

							for (int j = 0; j < 200; j++) {

								Cell cell = r.getCell(j);
								DataFormatter formatter = new DataFormatter();
								String text = formatter.formatCellValue(cell);
								map.put(j, text);
							}
							mapList.add(map);

						}

					}

				}

				for (HashMap<Integer, String> item : mapList) { 
					
					BIPS_Mer_Device_Management_Entity DV = new BIPS_Mer_Device_Management_Entity(); 
					
					String device_id = item.get(0);
					System.out.println("device_id: " + device_id);
					
					String device_name = item.get(1);
					System.out.println("device_name: " + device_name); 
					 
					String device_identification = item.get(2);
					System.out.println("device_identification: " + device_identification); 
					
					String device_modal = item.get(3);
					System.out.println("device_modal: " + device_modal); 
					
					String device_location = item.get(4);
					System.out.println("device_location: " + device_location); 
					
					String device_status = item.get(5);
					System.out.println("device_status: " + device_status); 
					
					String terminal_id = item.get(6);
					System.out.println("terminal_id: " + terminal_id); 

					String unit_id = item.get(7);
					System.out.println("unit_id: " + unit_id); 
					 
					String unit_name = item.get(8);
					System.out.println("unit_name: " + unit_name); 
					// Validate mandatory fields
				    if (device_id == null || device_id.isEmpty()) {
				        return "Field 'Device Id' cannot be null or empty.";
				    }
				    if (device_name == null || device_name.isEmpty()) {
				        return "Field 'Device Name' cannot be null or empty.";
				    }
				    if (device_identification == null || device_identification.isEmpty()) {
				        return "Field 'Device Identification' cannot be null or empty.";
				    }
				    if (device_modal == null || device_modal.isEmpty()) {
				        return "Field 'Device Modal' cannot be null or empty.";
				    }
				    if (device_location == null || device_location.isEmpty()) {
				        return "Field 'Device Location' cannot be null or empty.";
				    }
				    if (device_status == null || device_status.isEmpty()) {
				        return "Field 'Device Status' cannot be null or empty.";
				    }
				    if (terminal_id == null || terminal_id.isEmpty()) {
				        return "Field 'Terminal Id' cannot be null or empty.";
				    }
				    if (unit_id == null || unit_id.isEmpty()) {
				        return "Field 'Unit Id' cannot be null or empty.";
				    }
				    if (unit_name == null || unit_name.isEmpty()) {
				        return "Field 'Unit Name' cannot be null or empty.";
				    }
				    
				    String device_mechine_id = item.get(9);
				    String device_make = item.get(10);
				    String store_id = item.get(11);
				    String finger_print_enable = item.get(12);
				    String finger_recogn_enable = item.get(13);
				    String default_user_id = item.get(14);
				    String alternative_user_id = item.get(15);
				    String approved_user = item.get(16);
				    String defined_user = item.get(17);
				    String user1 = item.get(18);
				    String user2 = item.get(19);
				    String unit_typpe = item.get(20);
				    String remarks = item.get(21);
				    
				    
				    
					DV.setMerchant_user_id(merchant_id);
					DV.setMerchant_name(merchant_name);
					DV.setDevice_id(device_id);
					DV.setDevice_name(device_name);
					DV.setDevice_identification_no(device_identification);
					DV.setDevice_model(device_modal);
					DV.setLocation(device_location);
					DV.setDevice_status(device_status);
					DV.setTerminal_id(terminal_id);
					DV.setUnit_id_d(unit_id);
					DV.setUnit_name_d(unit_name);
					
					DV.setDevice_machine_id(device_mechine_id);
					DV.setDevice_make(device_make);
					DV.setStore_id(store_id);
					DV.setFingerprint_enable(finger_print_enable);
					DV.setFace_recognition_enabled(finger_print_enable);
					DV.setDefault_user_id(default_user_id);
					DV.setAlternate_user_id(alternative_user_id);
					DV.setApproved_user(approved_user);
					DV.setDefined_user(defined_user);
					DV.setUser1(user1);
					DV.setUser2(user2);
					DV.setUnit_type_d(unit_typpe);
					DV.setRemark(remarks);
					DV.setDel_flg("N");
					DV.setDisable_flag("N");
					DV.setFingerprint_enable("YES");
					DV.setFace_recognition_enabled("YES");
					DV.setEntry_user(userid);
					DV.setEntry_time(new Date());
					DV.setModify_user(userid);
					DV.setModify_time(new Date());
					DV.setEntry_flag("N");
					DV.setModify_flag("N");
					
					
					BIPS_MerDeviceManagement_Repo.save(DV);  
					msg = "Excel Data Uploaded Successfully";
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = "Device File has not been uploaded";
			}
		}
		return msg;

	}

}
