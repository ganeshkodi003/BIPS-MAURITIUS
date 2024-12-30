package com.bornfire.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.AccessandRolesRepository;
import com.bornfire.entity.BIPS_Password_Management_Entity;
import com.bornfire.entity.IPSAccessRole;
import com.bornfire.entity.IPSAccessRoleModTable;
import com.bornfire.entity.IPSAccessRoleModTableRep;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;

@Service
@ConfigurationProperties("output")
@Transactional
public class IPSAccessRoleService {

	@Autowired
	AccessandRolesRepository accessandrolesrepository;

	@Autowired
	IPSAccessRoleModTableRep ipsAccessRoleMosRep;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	@Autowired
	SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public List<Object[]> getdetail(String fromDate, String toDate) {

		List<Object[]> list = (List<Object[]>) sessionFactory.getCurrentSession()
				.createNativeQuery("select a.x_request_id,a.RECEIVERPARTICIPANT_BIC,(select bank_name "
						+ "from BIPS_OTHER_BANK_AGENT_TABLE where bank_agent=a.receiverparticipant_bic)BANK_NAME,a.SCHM_NAME,"
						+ "a.IDENTIFICATION,a.PSU_ID_TYPE,a.PSU_ID,a.PHONE_NUMBER,a.READ_BALANCE,"
						+ "a.READ_DEBIT_ACCT,a.READ_TRAN_DETAILS,a.READ_ACCT_DETAILS,a.CREATE_DATE,a.del_flg,a.del_time from BIPS_CONSENT_OUTWARD_ACCESS_TABLE a "
						+ "where trunc(a.create_date) between '" + fromDate + "' and '" + toDate
						+ "' order by a.create_date asc")
				.getResultList();

		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getdetailCoI(String tranDate) {

		List<Object[]> list = (List<Object[]>) sessionFactory.getCurrentSession().createNativeQuery(
				"select a.x_request_id,a.RECEIVERPARTICIPANT_BIC,(select bank_name from BIPS_OTHER_BANK_AGENT_TABLE where bank_agent=a.receiverparticipant_bic)BANK_NAME,a.INQUIRY_TYPE,a.ACCOUNT_ID,a.PSU_ID_TYPE,a.PSU_ID,a.STATUS,a.STATUS_ERROR"
						+ " from BIPS_CONSENT_OUTWARD_INQUIRY_TABLE a where (a.Inquiry_type='ReadBalances' OR Inquiry_type='ReadBalances' OR a.Inquiry_type='ReadAccountsDetails' OR a.Inquiry_type='ReadTransactionsDetails') and trunc(entry_time)='"
						+ tranDate + "' order by a.entry_time asc")
				.getResultList();

		return list;

	}

	public String addPARAMETER(IPSAccessRole accessRole, String USERID, String formmode, String adminValue,
			String userprofilevalue, String accessValue, String ipsparavalue, String servchargeValue,
			String walletfeesValue, String merchantcategValue, String notificationValue, String auditValue,
			String useractivityValue, String serviceaudValue, String chargebackValue, String transactionmonitoringValue,
			String merchantmastValue, String finalString, String referenceValue, String settlementAccountValue,
			String bankBranchDetailValue,String reportsValue) {

		String msg = "";

		if (formmode.equals("add")) {
			IPSAccessRoleModTable up = new IPSAccessRoleModTable(accessRole);
			String count = accessandrolesrepository.getusercount(up.getRole_id());
			if (count.equals("0")) {
				up.setDel_flg("N");
				up.setModify_flg("N");
				up.setEntity_flg("N");
				up.setNew_role_flg("Y");
				up.setEntry_user(USERID);
				up.setEntry_time(new Date());
				up.setAdmin(adminValue);
				up.setUser_profile_maintenance(userprofilevalue);
				up.setAccess_role(accessValue);
				up.setAudit_operations(auditValue);
				up.setIps_parameter(ipsparavalue);
				up.setMenulist(finalString);
				up.setService_charges_fees(servchargeValue);
				up.setWallet_fees_charges(walletfeesValue);
				up.setMerchant_category_code(merchantcategValue);
				up.setNotification_parameter(notificationValue);
				up.setUser_activity_audits(useractivityValue);
				up.setMerchant_master(merchantmastValue);
				up.setService_audits(serviceaudValue);
				up.setCharge_back(chargebackValue);
				up.setTransaction_monitoring(transactionmonitoringValue);
				up.setReference_code(referenceValue);
				up.setSettlement_account(settlementAccountValue);
				up.setBank_branch_master(bankBranchDetailValue);
				up.setReports(reportsValue);
				ipsAccessRoleMosRep.save(up);
				msg = "Role Created Successfully";

				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(USERID);
				audit.setFunc_code("ROLE CREATION");
				audit.setRemarks(up.getRole_id() + " : ROLE Created Successfully");
				audit.setAudit_table("BIPS_ACCESS_ROLE_TABLE");
				audit.setAudit_screen("Access and Role-Add");
				audit.setEvent_id(USERID);
				audit.setEvent_name(USERID);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);

			} else {
				msg = "Role Already Exist";
			}
		} else if (formmode.equals("edit")) {
			IPSAccessRole up = accessRole;
			IPSAccessRoleModTable up1 = new IPSAccessRoleModTable(up);
			Optional<IPSAccessRole> user = accessandrolesrepository.findById(accessRole.getRole_id());
			IPSAccessRole user1 = user.get();
			if (isNullCheck(up.getRole_desc()).equals(isNullCheck(user1.getRole_desc()))
					&& isNullCheck(up.getPermissions()).equals(isNullCheck(user1.getPermissions()))
					&& isNullCheck(up.getRemarks()).equals(isNullCheck(user1.getRemarks()))
					&& isNullCheck(up.getWork_class()).equals(isNullCheck(user1.getWork_class()))
					&& finalString.equals(isNullCheck(user1.getMenulist()))) {
				msg = "No any modification done";
			} else {
				up1.setAdmin(adminValue);
				if (!finalString.equals("")) {
					up1.setMenulist(finalString);
				} else {
					up1.setMenulist(user.get().getMenulist());
				}
				up1.setDel_flg("N");
				up1.setModify_flg("Y");
				up1.setEntity_flg("N");
				up1.setAdmin(adminValue);
				up1.setNew_role_flg("N");
				up1.setAudit_operations(auditValue);
				up1.setEntry_user(user1.getEntry_user());
				up1.setEntry_time(user1.getEntry_time());
				up1.setAuth_user(user1.getAuth_user());
				up1.setAuth_time(user1.getAuth_time());
				up1.setModify_user(USERID);
				up1.setModify_time(new Date());
				Session session = sessionFactory.getCurrentSession();
				session.saveOrUpdate(up1);
				user1.setEntity_flg("N");
				accessandrolesrepository.save(user1);
				msg = "Role Edited Successfully";
				StringBuilder stringBuilder = new StringBuilder();
				if (isNullCheck(up.getRole_desc()).equals(isNullCheck(user1.getRole_desc()))
						|| isNullCheck(up.getPermissions()).equals(isNullCheck(user1.getPermissions()))
						|| isNullCheck(up.getRemarks()).equals(isNullCheck(user1.getRemarks()))
						|| isNullCheck(up.getWork_class()).equals(isNullCheck(user1.getWork_class()))
						|| finalString.equals(isNullCheck(user1.getMenulist()))) {

					if (!isNullCheck(up.getRole_desc()).equals(isNullCheck(user1.getRole_desc()))) {
						stringBuilder = stringBuilder
								.append("Role Desc+" + user1.getRole_desc() + "+" + up.getRole_desc() + "||");
					}

					if (!isNullCheck(up.getPermissions()).equals(isNullCheck(user1.getPermissions()))) {
						stringBuilder = stringBuilder
								.append("Permission+" + user1.getPermissions() + "+" + up.getPermissions() + "||");
					}

					if (!isNullCheck(up.getRemarks()).equals(isNullCheck(user1.getRemarks()))) {
						stringBuilder = stringBuilder
								.append("Remarks+" + user1.getRemarks() + "+" + up.getRemarks() + "||");
					}
					if (!isNullCheck(up.getWork_class()).equals(isNullCheck(user1.getWork_class()))) {
						stringBuilder = stringBuilder
								.append("Work Class+" + user1.getWork_class() + "+" + up.getWork_class() + "||");
					}

					if (!finalString.equals(isNullCheck(user1.getMenulist()))) {
						if (!finalString.equals(user1.getMenulist()))
							stringBuilder = stringBuilder
									.append("Menu List+" + user1.getMenulist() + "+" + finalString + "||");
					}

					String audit_ref_no = sequence.generateRequestUUId();

					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(USERID);
					audit.setFunc_code("ACCESS ROLE MODIFICATION");
					audit.setRemarks(up.getRole_id() + " : Role Modified Successfully");
					audit.setAudit_table("BIPS_ACCESS_ROLE_TABLE");
					audit.setAudit_screen("Access and Role-Modify");
					audit.setEvent_id(up.getRole_id()); // audit.setEvent_name(up.getUsername());
					String modiDetails = stringBuilder.toString();
					audit.setModi_details(modiDetails);
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);

				}
			}
		} else if (formmode.equals("delete")) {

			Optional<IPSAccessRole> user = accessandrolesrepository.findById(accessRole.getRole_id());
			BigDecimal bg = accessandrolesrepository.userrolecount(accessRole.getRole_id());
			BigDecimal bs = new BigDecimal(0);
			if (bg.equals(bs)) {
				IPSAccessRole accessRoleTable = user.get();
				accessRoleTable.setDel_flg("Y");
				accessRoleTable.setEntity_flg("N");
				accessandrolesrepository.save(accessRoleTable);
				msg = "Role Deleted Successfully";

				String audit_ref_no = sequence.generateRequestUUId();
				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(USERID);
				audit.setFunc_code("ACCESS ROLE DELETE");
				audit.setRemarks(accessRole.getRole_id() + " : Role Deleted Successfully");
				audit.setAudit_table("BIPS_ACCESS_ROLE_TABLE");
				audit.setAudit_screen("Access and Role-Delete");
				audit.setEvent_id(accessRole.getRole_id());
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);
				ipsAuditTableRep.save(audit);

			} else {
				msg = "This Role already assigned to Users,Can't delete";
			}

		} else if (formmode.equals("verify")) {
			String verifyAccessRole = verifyAccessRole(accessRole.getRole_id(), USERID);
			Optional<IPSAccessRoleModTable> dat = ipsAccessRoleMosRep.findById(accessRole.getRole_id());
			if (verifyAccessRole.equals("0")) {
				String audit_ref_no = sequence.generateRequestUUId();
				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(USERID);
				audit.setFunc_code("ACCESS ROLE VERIFICATION");
				audit.setRemarks(dat.get().getRole_id() + " : Role Verified Successfully");
				audit.setAudit_table("BIPS_ACCESS_ROLE_TABLE");
				audit.setAudit_screen("Access and Role-Verify");
				audit.setEvent_id(dat.get().getRole_id());
				audit.setAuth_user(USERID);
				audit.setAuth_time(new Date());
				if (!dat.get().getNew_role_flg().equals("Y")) {
					IPSAuditTable audit1 = ipsAuditTableRep.getModifyList1(dat.get().getRole_id(),
							"ACCESS ROLE MODIFICATION");
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

				msg = "Role Verified Successfully";
				ipsAccessRoleMosRep.deleteById(accessRole.getRole_id());

				return msg;
			}
		} else if (formmode.equals("cancel")) {
			IPSAccessRoleModTable up2 = ipsAccessRoleMosRep.findByIDcustom(accessRole.getRole_id());

			//System.out.println("CancelAcc" + up2.getNew_role_flg());
			if (up2.getNew_role_flg().equals("Y")) {
				msg = "Role Cancelled Successfully";
				ipsAccessRoleMosRep.deleteById(accessRole.getRole_id());
				return msg;
			} else {
				String status = cancelRoleModification(accessRole.getRole_id(), USERID);
				Optional<IPSAccessRoleModTable> dat = ipsAccessRoleMosRep.findById(accessRole.getRole_id());

				if (status.equals("0")) {

					String audit_ref_no = sequence.generateRequestUUId();
					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(USERID);
					audit.setFunc_code("ACCESS ROLE CANCEL");
					audit.setRemarks(dat.get().getRole_id() + " : Role Cancelled Successfully");
					audit.setAudit_table("BIPS_ACCESS_ROLE_TABLE");
					audit.setAudit_screen("Access and Role-Cancel");
					audit.setEvent_id(dat.get().getRole_id());
					audit.setAuth_user(USERID);
					audit.setAuth_time(new Date());
					// audit.setEvent_name(up.getUsername());
					if (!dat.get().getNew_role_flg().equals("Y")) {
						IPSAuditTable audit1 = ipsAuditTableRep.getModifyList1(dat.get().getRole_id(),
								"ACCESS ROLE MODIFICATION");
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

					msg = "Role Cancelled Successfully";
					ipsAccessRoleMosRep.deleteById(accessRole.getRole_id());
					return msg;
				}
			}

		}
		return msg;
	}

	private String cancelRoleModification(String role_id, String USERID) {
		Optional<IPSAccessRole> user = accessandrolesrepository.findById(role_id);
		IPSAccessRole user1 = user.get();
		user1.setDel_flg("N");
		user1.setModify_flg("N");
		user1.setEntity_flg("Y");
		user1.setNew_role_flg("N");
		user1.setAuth_user(USERID);
		user1.setAuth_time(new Date());

		accessandrolesrepository.save(user1);
		return "0";
	}

	private String verifyAccessRole(String role_id, String USERID) {
		Optional<IPSAccessRoleModTable> user = ipsAccessRoleMosRep.findById(role_id);
		IPSAccessRoleModTable user1 = user.get();
		user1.setDel_flg("N");
		user1.setModify_flg("N");
		user1.setEntity_flg("Y");
		user1.setNew_role_flg("N");
		user1.setAuth_user(USERID);
		user1.setAuth_time(new Date());
		IPSAccessRole verifiedData = new IPSAccessRole(user1);
		accessandrolesrepository.save(verifiedData);
		return "0";
	}

	public IPSAccessRole getRoleId(String id) {
		Session session = sessionFactory.getCurrentSession();
		Query<IPSAccessRole> query = session
				.createQuery(" from IPSAccessRole where role_id=?1 and NVL(DEL_FLG,1) <> 'Y'", IPSAccessRole.class);
		query.setParameter(1, id);
		List<IPSAccessRole> result = query.getResultList();
		if (!result.isEmpty()) {
			return result.get(0);
		} else {
			return new IPSAccessRole();
		}

	}

	public IPSAccessRole getRoleMenu(String id) {
		Session session = sessionFactory.getCurrentSession();
		Query<IPSAccessRole> query = session.createQuery("from IPSAccessRole where role_id=?1", IPSAccessRole.class);
		query.setParameter(1, id);
		List<IPSAccessRole> result = query.getResultList();
		if (!result.isEmpty()) {
			return result.get(0);
		} else {
			return new IPSAccessRole();
		}
	}

	public BIPS_Password_Management_Entity getRoleMenus(String id) {
		Session session = sessionFactory.getCurrentSession();
		Query<BIPS_Password_Management_Entity> query = session.createQuery(
				" from BIPS_Password_Management_Entity where merchant_rep_id=?1",
				BIPS_Password_Management_Entity.class);
		query.setParameter(1, id);
		//System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" + query);
		List<BIPS_Password_Management_Entity> result = query.getResultList();
		if (!result.isEmpty()) {
			return result.get(0);
		} else {
			return new BIPS_Password_Management_Entity();
		}

	}

	public String deleteRole(String userid) {
		Session hs = sessionFactory.getCurrentSession();
		@SuppressWarnings("rawtypes")
		Query qr;
		qr = hs.createQuery("select count(*) from UserProfile where role_id= ?1");
		qr.setParameter(1, userid);
		long count = (long) qr.getSingleResult();
		String msg = "";
		if (count == 0) {
			Optional<IPSAccessRole> user = accessandrolesrepository.findById(userid);
			IPSAccessRole reg = user.get();
			reg.setDel_flg("Y");
			accessandrolesrepository.save(reg);
			msg = "Role Deleted Successfully";
		} else {
			msg = "This role has been assigned to an User.Cannot Delete ";
		}
		return msg;
	}

	public List<IPSAccessRole> rulelist() {
		List<IPSAccessRole> list = accessandrolesrepository.finsAllData();
		for (IPSAccessRole a : list) {
			String  menu=	a.getMenulist();
			String caps=	menu.replaceAll("([a-z])([A-Z])", "$1 $2");
			a.setMenulist(caps);
		
		}
		
		return list;
	}

	public IPSAccessRoleModTable getModifyData(String roleID) {
		Optional<IPSAccessRoleModTable> list = ipsAccessRoleMosRep.findById(roleID);
		if (list.isPresent()) {
			
			return list.get();
		}
		return null;
	}

	public IPSAccessRoleModTable getViewNewData(String roleID) {
		Optional<IPSAccessRoleModTable> list = ipsAccessRoleMosRep.findById(roleID);
		if (list.isPresent()) {
			return list.get();
		} else {
			return new IPSAccessRoleModTable();
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
}