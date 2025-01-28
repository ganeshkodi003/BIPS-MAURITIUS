package com.bornfire.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;
import com.bornfire.entity.IPSChargesAndFees;
import com.bornfire.entity.IPSChargesAndFeesRepository;
import com.bornfire.entity.LoginSecurity;
import com.bornfire.entity.LoginSecurityRepository;
import com.bornfire.entity.MerchantChargesAndFees;
import com.bornfire.entity.MerchantChargesAndFeesRepositry;
import com.bornfire.entity.MerchantChargesandFeesMod;
import com.bornfire.entity.MerchantChargesandFeesModRep;

@Service
@ConfigurationProperties("output")
@Transactional
public class LoginSecurityServices {

	@Autowired
	LoginSecurityRepository loginSecurityRepository;

	@Autowired
	IPSChargesAndFeesRepository ipsChargesAndFeesRep;
	
	@Autowired
	MerchantChargesAndFeesRepositry merchantChargesAndFeesRep;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

		
	@Autowired
	MerchantChargesandFeesModRep merchantChargesAndFeesModRep;

	public String addUser(LoginSecurity loginSecurity, String formmode) {
		//System.out.println("hihihih");
		String msg = "";
		Session hs = sessionFactory.getCurrentSession();
		if (formmode.equals("submit")) {
			LoginSecurity up = loginSecurity;
			up.setRef_num("1");
			hs.saveOrUpdate(up);
			up.setEntity_flg("N");
			loginSecurityRepository.save(up);
			msg = "Login Security Submitted Successfully";

		}
		return msg;

	}

	public String addFees(IPSChargesAndFees ipsChargesAndFees, String formmode,String User) {
		String msg = "";
		try {
		if (formmode.equals("add")) {

			ipsChargesAndFees.setEntity_flg("N");
			ipsChargesAndFees.setDel_flg("N");
			ipsChargesAndFees.setModify_flg("N");
			ipsChargesAndFees.setEntry_user(User);
			ipsChargesAndFees.setEntry_time(new Date());
			ipsChargesAndFeesRep.save(ipsChargesAndFees);
			msg = "Added Successfully";
			
			String audit_ref_no = sequence.generateRequestUUId();

			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(User);
			audit.setFunc_code("SERVICE CHARGES CREATION");
			audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Service Charges Created Successfully");
			audit.setAudit_table("BIPS_CHARGES_AND_FEES_TABLE");
			audit.setAudit_screen("Service Charges and Fees - Add");
			audit.setEvent_id(User);
			audit.setEvent_name(User);
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);

			ipsAuditTableRep.save(audit);

		} else if (formmode.equals("edit")) {
			Optional<IPSChargesAndFees> chargeList = ipsChargesAndFeesRep
					.findById(ipsChargesAndFees.getReference_number());

			if (chargeList.isPresent()) {
				IPSChargesAndFees ipsCharge = chargeList.get();

				if (ipsChargesAndFees.getDescription() == (ipsCharge.getDescription())
						&& ipsChargesAndFees.getType() == (ipsCharge.getType())
						&& ipsChargesAndFees.getCriteria() == (ipsCharge.getCriteria())
						&& ipsChargesAndFees.getPercentage() == (ipsCharge.getPercentage())
						&& ipsChargesAndFees.getFees() == (ipsCharge.getFees())
						&& ipsChargesAndFees.getMin() == (ipsCharge.getMin())
						&& ipsChargesAndFees.getMax() == (ipsCharge.getMax())
						&& ipsChargesAndFees.getPeriodicity() == (ipsCharge.getPeriodicity())
						&& ipsChargesAndFees.getLast_tried_date() == (ipsCharge.getLast_tried_date())
						&& ipsChargesAndFees.getNext_due_date() == (ipsCharge.getNext_due_date())
						&& ipsChargesAndFees.getPayable_to() == (ipsCharge.getPayable_to())
						&& ipsChargesAndFees.getAmount() == (ipsCharge.getAmount())
						&& ipsChargesAndFees.getCurrency() == (ipsCharge.getCurrency())
						&& ipsChargesAndFees.getScript_name() == (ipsCharge.getScript_name())) {
					msg = "No Any Modification Done";
				} else {
					ipsCharge.setDescription(ipsChargesAndFees.getDescription());
					
					//System.out.println(ipsChargesAndFees.getType());
					ipsCharge.setType(ipsChargesAndFees.getType());
					ipsCharge.setCriteria(ipsChargesAndFees.getCriteria());
					if (ipsChargesAndFees.getFees() == null) {
						ipsCharge.setFees(ipsChargesAndFees.getFees());
					} else {
						ipsCharge.setFees(new BigDecimal(ipsChargesAndFees.getFees().toString().replace(",", "")));
					}

					if (ipsChargesAndFees.getPercentage() == null) {
						ipsCharge.setPercentage(ipsChargesAndFees.getPercentage());
					} else {
						ipsCharge.setPercentage(
								new BigDecimal(ipsChargesAndFees.getPercentage().toString().replace(",", "")));
					}

					if (ipsChargesAndFees.getMin() == null) {
						ipsCharge.setMin(ipsChargesAndFees.getMin());
					} else {
						ipsCharge.setMin(new BigDecimal(ipsChargesAndFees.getMin().toString().replace(",", "")));
					}

					if (ipsChargesAndFees.getMax() == null) {
						ipsCharge.setMax(ipsChargesAndFees.getMax());
					} else {
						ipsCharge.setMax(new BigDecimal(ipsChargesAndFees.getMax().toString().replace(",", "")));
					}

					ipsCharge.setPeriodicity(ipsChargesAndFees.getPeriodicity());
					ipsCharge.setLast_tried_date(ipsChargesAndFees.getLast_tried_date());
					ipsCharge.setNext_due_date(ipsChargesAndFees.getNext_due_date());
					ipsCharge.setPayable_to(ipsChargesAndFees.getPayable_to());

					if (ipsChargesAndFees.getAmount() == null) {
						ipsCharge.setAmount(ipsChargesAndFees.getAmount());
					} else {
						ipsCharge.setAmount(new BigDecimal(ipsChargesAndFees.getAmount().toString().replace(",", "")));
					}

					ipsCharge.setCurrency(ipsChargesAndFees.getCurrency());
					ipsCharge.setScript_name(ipsChargesAndFees.getScript_name());

					ipsCharge.setEntity_flg("N");
					ipsCharge.setDel_flg("N");
					ipsCharge.setModify_flg("Y");
					ipsCharge.setModify_user(User);
					ipsCharge.setModify_time(new Date());
					
					
					ipsChargesAndFeesRep.save(ipsCharge);
					
					msg = "Modified Successfully";
					
					String audit_ref_no = sequence.generateRequestUUId();

					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(User);
					audit.setFunc_code("SERVICE CHARGES MODIFICATION");
					audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Service Charges Modified Successfully");
					audit.setAudit_table("BIPS_CHARGES_AND_FEES_TABLE");
					audit.setAudit_screen("Service Charges and Fees - Modify");
					audit.setEvent_id(User);
					audit.setEvent_name(User);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);
				}

			} else {
				msg = "Error Occured. Please Contact Administrator";
			}

		} else if (formmode.equals("verify")) {

			Optional<IPSChargesAndFees> chargeList = ipsChargesAndFeesRep
					.findById(ipsChargesAndFees.getReference_number());
			if(chargeList.isPresent()) {
				IPSChargesAndFees up = ipsChargesAndFees;
				up.setEntity_flg("Y");
				up.setDel_flg("N");
				up.setModify_flg("N");
				up.setEntry_user(chargeList.get().getEntry_user());
				up.setEntry_time(chargeList.get().getEntry_time());
				up.setModify_user(chargeList.get().getModify_user());
				up.setModify_time(chargeList.get().getModify_time());
				up.setVerify_user(User);
				up.setVerify_time(new Date());
				ipsChargesAndFeesRep.save(up);
				msg = "Verified Successfully";	
				 
				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(User);
				audit.setFunc_code("SERVICE CHARGES VERIFICATION");
				audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Service Charges Verified Successfully");
				audit.setAudit_table("BIPS_CHARGES_AND_FEES_TABLE");
				audit.setAudit_screen("Service Charges and Fees - Verify");
				audit.setEvent_id(User);
				audit.setEvent_name(User);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);
				
			}else {
				msg = "Error Occured. Please Contact Administrator";
			}
			
		}else if (formmode.equals("delete")) {

			
			ipsChargesAndFees.setEntity_flg("Y");
			ipsChargesAndFees.setDel_flg("Y");
			ipsChargesAndFees.setModify_flg("N");
			ipsChargesAndFeesRep.save(ipsChargesAndFees);
			msg = "Deleted Successfully";

		} 
		}catch(Exception e) {
			return "Something went wrong";
		}
		return msg;

	}

	public LoginSecurity getLoginSec() {
		LoginSecurity loginSecurity = new LoginSecurity();

		List<LoginSecurity> loginSecurityList = loginSecurityRepository.findAll();

		if (loginSecurityList.size() > 0) {
			loginSecurity = loginSecurityRepository.findAll().get(0);
		}
		return loginSecurity;
	}

	

	public String addFees(MerchantChargesandFeesMod ipsChargesAndFees, String formmode,String User,Date modifytime, Date entrytime) {
		String msg = "";
		try {
		if (formmode.equals("add")) {
			
			/*ipsChargesAndFees.setEntity_flg("N");
			ipsChargesAndFees.setDel_flg("N");
			ipsChargesAndFees.setModify_flg("Y");
			ipsChargesAndFees.setEntry_user(User);
			ipsChargesAndFees.setEntry_time(new Date());
			merchantChargesAndFeesRep.save(ipsChargesAndFees);
			msg = "Added Successfully";*/
			Optional<MerchantChargesandFeesMod> MerchantChargesandFeesMod = merchantChargesAndFeesModRep.findById(ipsChargesAndFees.getReference_number()); 
			Optional<MerchantChargesAndFees> chargeList = merchantChargesAndFeesRep
					.findById(ipsChargesAndFees.getReference_number());	
			if (!MerchantChargesandFeesMod.isPresent()) {
				if (!chargeList.isPresent()) {
					MerchantChargesandFeesMod Merfeemod = new MerchantChargesandFeesMod();
					Merfeemod.setReference_number(ipsChargesAndFees.getReference_number());
					Merfeemod.setDescription(ipsChargesAndFees.getDescription());
					Merfeemod.setType(ipsChargesAndFees.getType());
					Merfeemod.setAmount(ipsChargesAndFees.getAmount());
					Merfeemod.setPercentage(ipsChargesAndFees.getPercentage());
					Merfeemod.setCurrency(ipsChargesAndFees.getCurrency());
					Merfeemod.setMin(ipsChargesAndFees.getMin());
					Merfeemod.setMax(ipsChargesAndFees.getMax());
					Merfeemod.setGl_id(ipsChargesAndFees.getGl_id());
					Merfeemod.setScript_name(ipsChargesAndFees.getScript_name());
					Merfeemod.setTran_type(ipsChargesAndFees.getTran_type());
					Merfeemod.setTran_code(ipsChargesAndFees.getTran_code());
					Merfeemod.setRemarks1(ipsChargesAndFees.getRemarks1());
					Merfeemod.setRemarks2(ipsChargesAndFees.getRemarks2());
					Merfeemod.setCriteria(ipsChargesAndFees.getCriteria());
					Merfeemod.setBorne_by(ipsChargesAndFees.getBorne_by());
					Merfeemod.setEntity_flg("N");
					Merfeemod.setDel_flg("N");
					Merfeemod.setModify_flg("N");
					Merfeemod.setEntry_user(User);
					Merfeemod.setEntry_time(new Date());
					Merfeemod.setModify_user(User);
					Merfeemod.setModify_time(new Date());

					merchantChargesAndFeesModRep.save(Merfeemod);
					msg = "Added Successfully";
					
					
					String audit_ref_no = sequence.generateRequestUUId();

					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(User);
					audit.setFunc_code("MERCHANT CHARGES CREATION");
					audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Merchant Charges Created Successfully");
					audit.setAudit_table("BIPS_MERCHANT_CHARGES_AND_FEES_TABLE");
					audit.setAudit_screen("Merchant Charges and Fees-Add");
					audit.setEvent_id(User);
					audit.setEvent_name(User);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);
					
				} else {
					msg = "Fees Reference number already Exist";
				}
			}else {
				msg = "Fees Reference number already Exist";
			}
			
		} else if (formmode.equals("verify")) {

			Optional<MerchantChargesAndFees> chargeList = merchantChargesAndFeesRep
					.findById(ipsChargesAndFees.getReference_number());
			
			if(chargeList.isPresent()) {
				MerchantChargesAndFees up = chargeList.get();
			//	up.setReference_number(ipsChargesAndFees.getReference_number());
				up.setDescription(ipsChargesAndFees.getDescription());
				up.setType(ipsChargesAndFees.getType());
				up.setAmount(ipsChargesAndFees.getAmount());
				up.setPercentage(ipsChargesAndFees.getPercentage());
				up.setCurrency(ipsChargesAndFees.getCurrency());
				up.setMin(ipsChargesAndFees.getMin());
				up.setMax(ipsChargesAndFees.getMax());
				up.setGl_id(ipsChargesAndFees.getGl_id());
				up.setScript_name(ipsChargesAndFees.getScript_name());
				up.setTran_type(ipsChargesAndFees.getTran_type());
				up.setTran_code(ipsChargesAndFees.getTran_code());
				up.setRemarks1(ipsChargesAndFees.getRemarks1());
				up.setRemarks2(ipsChargesAndFees.getRemarks2());
				up.setCriteria(ipsChargesAndFees.getCriteria());
				up.setBorne_by(ipsChargesAndFees.getBorne_by());
				up.setEntity_flg("Y");
				up.setDel_flg("N");
				up.setModify_flg("N");
				up.setEntry_user(chargeList.get().getEntry_user());
				//System.out.println(up.getEntry_user());
				up.setEntry_time(chargeList.get().getEntry_time());
				//System.out.println(up.getEntry_time());
				up.setModify_user(chargeList.get().getModify_user());
				//System.out.println(up.getModify_user());
				//System.out.println("gggggggggggggggggggggggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhggggggggggggggg"+modifytime);
				up.setModify_time(modifytime);
				up.setEntry_time(entrytime);
				//System.out.println(up.getModify_time());
				up.setVerify_user(User);
				up.setVerify_time(new Date());
				merchantChargesAndFeesRep.save(up);
				
				merchantChargesAndFeesModRep.deleteById(ipsChargesAndFees.getReference_number());
				
				msg = "Verified Successfully";	
				
				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(User);
				audit.setFunc_code("MERCHANT CHARGES VERIFICATION");
				audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Merchant Charges Verified Successfully");
				audit.setAudit_table("BIPS_MERCHANT_CHARGES_AND_FEES_TABLE");
				audit.setAudit_screen("Merchant Charges and Fees - Verify");
				audit.setEvent_id(User);
				audit.setEvent_name(User);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);
			}else {
				
				Optional<MerchantChargesandFeesMod> chargeList1 = merchantChargesAndFeesModRep
						.findById(ipsChargesAndFees.getReference_number());

				MerchantChargesAndFees up = new MerchantChargesAndFees();
				up.setReference_number(ipsChargesAndFees.getReference_number());
				up.setDescription(ipsChargesAndFees.getDescription());
				up.setType(ipsChargesAndFees.getType());
				up.setAmount(ipsChargesAndFees.getAmount());
				up.setPercentage(ipsChargesAndFees.getPercentage());
				up.setCurrency(ipsChargesAndFees.getCurrency());
				up.setMin(ipsChargesAndFees.getMin());
				up.setMax(ipsChargesAndFees.getMax());
				up.setGl_id(ipsChargesAndFees.getGl_id());
				up.setScript_name(ipsChargesAndFees.getScript_name());
				up.setTran_type(ipsChargesAndFees.getTran_type());
				up.setTran_code(ipsChargesAndFees.getTran_code());
				up.setRemarks1(ipsChargesAndFees.getRemarks1());
				up.setRemarks2(ipsChargesAndFees.getRemarks2());
				up.setCriteria(ipsChargesAndFees.getCriteria());
				up.setBorne_by(ipsChargesAndFees.getBorne_by());
				up.setEntity_flg("Y");
				up.setDel_flg("N");
				up.setModify_flg("N");
				up.setEntry_user(chargeList1.get().getEntry_user());
				up.setEntry_time(chargeList1.get().getEntry_time());
				up.setModify_user(chargeList1.get().getModify_user());
				up.setModify_time(chargeList1.get().getModify_time());
				up.setVerify_user(User);
				up.setVerify_time(new Date());
				merchantChargesAndFeesRep.save(up);
				
				merchantChargesAndFeesModRep.deleteById(ipsChargesAndFees.getReference_number());
				
				msg = "Verified Successfully";	
			
				String audit_ref_no = sequence.generateRequestUUId();

				IPSAuditTable audit = new IPSAuditTable();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(User);
				audit.setFunc_code("MERCHANT CHARGES VERIFICATION");
				audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Merchant Charges Verified Successfully");
				audit.setAudit_table("BIPS_MERCHANT_CHARGES_AND_FEES_TABLE");
				audit.setAudit_screen("Merchant Charges and Fees - Verify");
				audit.setEvent_id(User);
				audit.setEvent_name(User);
				audit.setModi_details("-");
				audit.setAudit_ref_no(audit_ref_no);

				ipsAuditTableRep.save(audit);
				
			}
			
		}/*else if (formmode.equals("delete")) {

			
			ipsChargesAndFees.setEntity_flg("Y");
			ipsChargesAndFees.setDel_flg("Y");
			ipsChargesAndFees.setModify_flg("N");
			merchantChargesAndFeesRep.save(ipsChargesAndFees);
			msg = "Deleted Successfully";

		} */
		}catch(Exception e) {
			e.printStackTrace();
			return "Something went wrong";
		}
		return msg;

	}
	
	public String editFees(MerchantChargesAndFees ipsChargesAndFees, String formmode,String User ,String verifyuser,String a) {
		String msg = "";
		//System.out.println("ggggggggggggg"+User);
		//System.out.println("Uuuuuuuuuuuuuuuuuuuuuuuuuuu"+a);
		try {
		 if (formmode.equals("edit")) {
		//	MerchantChargesAndFees up = ipsChargesAndFees;
			Optional<MerchantChargesAndFees> chargeList = merchantChargesAndFeesRep
					.findById(ipsChargesAndFees.getReference_number());

			if (chargeList.isPresent()) {
				MerchantChargesAndFees ipsCharge = chargeList.get();
				MerchantChargesandFeesMod ipsCharge1 = new MerchantChargesandFeesMod();
				if (ipsChargesAndFees.getDescription().equals(ipsCharge.getDescription())
						&& ipsChargesAndFees.getType().equals(ipsCharge.getType())
						&& ipsChargesAndFees.getCriteria().equals(ipsCharge.getCriteria())
					//	&& ipsChargesAndFees.getMin().equals(ipsCharge.getMin())
					//	&& ipsChargesAndFees.getMax().equals(ipsCharge.getMax())
						&& ipsChargesAndFees.getCurrency().equals(ipsCharge.getCurrency())
						&& ipsChargesAndFees.getScript_name().equals(ipsCharge.getScript_name())
					//	&& ipsChargesAndFees.getTran_code().equals(ipsCharge.getTran_code())
						&& ipsChargesAndFees.getGl_id().equals(ipsCharge.getGl_id())
					//	&& ipsChargesAndFees.getTran_type().equals(ipsCharge.getTran_type())
						&& ipsChargesAndFees.getRemarks1().equals(ipsCharge.getRemarks1())
					//	&& ipsChargesAndFees.getRemarks2().equals(ipsCharge.getRemarks2())
						&& ipsChargesAndFees.getBorne_by().equals(ipsCharge.getBorne_by())) {
					msg = "No Any Modification Done";
				} else {
					ipsCharge1.setReference_number(ipsChargesAndFees.getReference_number());
					ipsCharge1.setDescription(ipsChargesAndFees.getDescription());
					
					//System.out.println(ipsChargesAndFees.getType());
					ipsCharge1.setType(ipsChargesAndFees.getType());
					ipsCharge1.setCriteria(ipsChargesAndFees.getCriteria());
					if (ipsChargesAndFees.getFees() == null) {
						ipsCharge1.setFees(ipsChargesAndFees.getFees());
					} else {
						ipsCharge1.setFees(new BigDecimal(ipsChargesAndFees.getFees().toString().replace(",", "")));
					}

					if (ipsChargesAndFees.getPercentage() == null) {
						ipsCharge1.setPercentage(ipsChargesAndFees.getPercentage());
					} else {
						ipsCharge1.setPercentage(
								new BigDecimal(ipsChargesAndFees.getPercentage().toString().replace(",", "")));
					}

					if (ipsChargesAndFees.getMin() == null) {
						ipsCharge1.setMin(ipsChargesAndFees.getMin());
					} else {
						ipsCharge1.setMin(new BigDecimal(ipsChargesAndFees.getMin().toString().replace(",", "")));
					}

					if (ipsChargesAndFees.getMax() == null) {
						ipsCharge1.setMax(ipsChargesAndFees.getMax());
					} else {
						ipsCharge1.setMax(new BigDecimal(ipsChargesAndFees.getMax().toString().replace(",", "")));
					}

					ipsCharge1.setPeriodicity(ipsChargesAndFees.getPeriodicity());
					ipsCharge1.setLast_tried_date(ipsChargesAndFees.getLast_tried_date());
					ipsCharge1.setNext_due_date(ipsChargesAndFees.getNext_due_date());
					ipsCharge1.setPayable_to(ipsChargesAndFees.getPayable_to());

					if (ipsChargesAndFees.getAmount() == null) {
						ipsCharge1.setAmount(ipsChargesAndFees.getAmount());
					} else {
						ipsCharge1.setAmount(new BigDecimal(ipsChargesAndFees.getAmount().toString().replace(",", "")));
					}

					ipsCharge1.setCurrency(ipsChargesAndFees.getCurrency());
					ipsCharge1.setScript_name(ipsChargesAndFees.getScript_name());
					ipsCharge1.setTran_code(ipsChargesAndFees.getTran_code());
					ipsCharge1.setGl_id(ipsChargesAndFees.getGl_id());
					ipsCharge1.setTran_type(ipsChargesAndFees.getTran_type());
					ipsCharge1.setRemarks1(ipsChargesAndFees.getRemarks1());
					ipsCharge1.setRemarks2(ipsChargesAndFees.getRemarks2());
					ipsCharge1.setBorne_by(ipsChargesAndFees.getBorne_by());
					ipsCharge1.setVerify_time(ipsCharge.getVerify_time());
					ipsCharge1.setEntry_time(ipsCharge.getEntry_time());
					ipsCharge1.setEntity_flg("N");
					ipsCharge1.setDel_flg("N");
					ipsCharge1.setModify_flg("Y");
					ipsCharge1.setModify_user(User);
					//System.out.println(ipsCharge1.getModify_user());
					ipsCharge1.setVerify_user(verifyuser);
					//System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffff"+a);
					
					//ipsChargesAndFees.setEntry_user(verifyuser);
					ipsCharge1.setEntry_user(a);
					ipsCharge1.setModify_time(new Date());
					
					merchantChargesAndFeesModRep.save(ipsCharge1);
				
					ipsChargesAndFees.setEntity_flg("N");
					ipsChargesAndFees.setModify_flg("Y");
					ipsChargesAndFees.setEntry_user(a);
					ipsChargesAndFees.setVerify_user(verifyuser);
					ipsChargesAndFees.setModify_user(User);
					
					merchantChargesAndFeesRep.save(ipsChargesAndFees);
					
					msg = "Modified Successfully";
					
					String audit_ref_no = sequence.generateRequestUUId();

					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(User);
					audit.setFunc_code("MERCHANT CHARGES MODIFICATION");
					audit.setRemarks(ipsChargesAndFees.getReference_number() + " : Merchant Charges Modified Successfully");
					audit.setAudit_table("BIPS_MERCHANT_CHARGES_AND_FEES_TABLE");
					audit.setAudit_screen("Merchant Charges and Fees - Modify");
					audit.setEvent_id(User);
					audit.setEvent_name(User);
					audit.setModi_details("-");
					audit.setAudit_ref_no(audit_ref_no);

					ipsAuditTableRep.save(audit);
				}

			} else {
				msg = "Error Occured. Please Contact Administrator";
			}

		}else if (formmode.equals("delete")) {

			merchantChargesAndFeesModRep.deleteById(ipsChargesAndFees.getReference_number());
			msg = "Deleted Successfully";

		} 
		}catch(Exception e) {
			e.printStackTrace();
			return "Something went wrong";
		}
		return msg;

	}
	
	




}