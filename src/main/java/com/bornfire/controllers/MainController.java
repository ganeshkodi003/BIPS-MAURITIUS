package com.bornfire.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.configuration.PasswordEncryption;
import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.AccessandRolesRepository;
import com.bornfire.entity.BIPS_Alert_Repo;
import com.bornfire.entity.BIPS_ChargeBack_Repo;
import com.bornfire.entity.BIPS_Charge_Back_Entity;
import com.bornfire.entity.BIPS_FeesCharges_Repo;
import com.bornfire.entity.BIPS_Fees_Charges_Entity;
import com.bornfire.entity.BIPS_MerDeviceManagement_Repo;
import com.bornfire.entity.BIPS_MerUserManagement_Repo;
import com.bornfire.entity.BIPS_Mer_Device_Management_Entity;
import com.bornfire.entity.BIPS_Mer_User_Management_Entity;
import com.bornfire.entity.BIPS_Notification_Repo;
import com.bornfire.entity.BIPS_OutwardTransMonitoring_Repo;
import com.bornfire.entity.BIPS_Outward_Trans_Monitoring_Entity;
import com.bornfire.entity.BIPS_PasswordManagement_Repo;
import com.bornfire.entity.BIPS_Password_Management_Entity;
import com.bornfire.entity.BIPS_RateMaint_Repo;
import com.bornfire.entity.BIPS_Rate_Maint_Entity;
import com.bornfire.entity.BIPS_ServiceReq_Repo;
import com.bornfire.entity.BIPS_Service_ReqMonitoring_Entity;
import com.bornfire.entity.BIPS_Service_ReqMonitoring_Repo;
import com.bornfire.entity.BIPS_Service_Req_Entity;
import com.bornfire.entity.BIPS_UnitManagement_Repo;
import com.bornfire.entity.BIPS_Unit_Mangement_Entity;
import com.bornfire.entity.BankAgentTableRep;
import com.bornfire.entity.BankAgentTmpTableRep;
import com.bornfire.entity.BankAndBranchBean;
import com.bornfire.entity.BankAndBranchRepository;
import com.bornfire.entity.DocumentMaster_Entity;
import com.bornfire.entity.DocumentMaster_Repo;
import com.bornfire.entity.DynamicFromValue;
import com.bornfire.entity.IPSAccessRole;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;
import com.bornfire.entity.IPSChargesAndFees;
import com.bornfire.entity.IPSChargesAndFeesRepository;
import com.bornfire.entity.IPSUserProfileMod;
import com.bornfire.entity.LoginSecurity;
import com.bornfire.entity.LoginSecurityRepository;
import com.bornfire.entity.MerchantCategoryCodeEntity;
import com.bornfire.entity.MerchantCategoryRep;
import com.bornfire.entity.MerchantChargesAndFeesRepositry;
import com.bornfire.entity.MerchantChargesandFeesModRep;
import com.bornfire.entity.MerchantFeesServiceRepo;
import com.bornfire.entity.MerchantFeesServiceRepoMod;
import com.bornfire.entity.MerchantID;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.MerchantMasterMod;
import com.bornfire.entity.MerchantMasterModRep;
import com.bornfire.entity.MerchantMasterRep;
import com.bornfire.entity.NotificationParms;
import com.bornfire.entity.NotificationParmsRep;
import com.bornfire.entity.ReferenceCodeEntity;
import com.bornfire.entity.ReferenceCodeRep;
import com.bornfire.entity.SettlementAccountRepository;
import com.bornfire.entity.Sign_Master_Entity;
import com.bornfire.entity.Sign_Master_Repo;
import com.bornfire.entity.SlabRateRepo;
import com.bornfire.entity.Two_factor_auth;
import com.bornfire.entity.Twofactorauth;
import com.bornfire.entity.UserProfile;
import com.bornfire.entity.UserProfileRep;
import com.bornfire.services.BIPSBankandBranchServices;
import com.bornfire.services.BankAndBranchMasterServices;
import com.bornfire.services.IPSAccessRoleService;
import com.bornfire.services.LoginSecurityServices;
import com.bornfire.services.MandateServices;
import com.bornfire.services.MerchantServices;
import com.bornfire.services.MonitorService;
import com.bornfire.services.NotificationParmsServices;
import com.bornfire.services.PasswordService;
import com.bornfire.services.SettlementAccountServices;
import com.bornfire.services.UserProfileService;

@Controller
@ConfigurationProperties("default")

public class MainController {
	// private static final Logger logger =
	// LoggerFactory.getLogger(MainController.class);

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	LoginSecurityRepository loginSecurityRepository;

	@Autowired
	SettlementAccountServices settlementAccountServices;

	@Autowired
	LoginSecurityServices loginSecurityServices;

	@Autowired
	MonitorService monitorService;

	@Autowired
	MandateServices mandateServices;

	@Autowired
	MerchantServices merchantServices;

	@Autowired
	NotificationParmsServices notificationParmsServices;

	@Autowired
	BankAgentTableRep bankAgentTableRep;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	IPSChargesAndFeesRepository ipsChargesAndFeesRep;

	@Autowired
	MerchantChargesAndFeesRepositry merchantChargesAndFeesRep;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	IPSAccessRoleService AccessRoleService;

	@Autowired
	NotificationParmsRep notificationParmsRep;

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	@Autowired
	private AccessandRolesRepository accessandrolesrepository;

	@Autowired
	BIPSBankandBranchServices bankandBranchServices;
	@Autowired
	BankAndBranchRepository bipsSolRepository;

	@Autowired
	MerchantCategoryRep merchantCategoryRep;

	@Autowired
	Environment env;

	@Autowired
	BankAgentTmpTableRep bankAgentTmpTableRep;

	@Autowired
	MerchantMasterRep merchantMasterRep;

	@Autowired
	MerchantMasterModRep merchantMasterModRep;

	@Autowired
	MerchantFeesServiceRepo merchantFeesServiceRepo;

	@Autowired
	MerchantFeesServiceRepoMod merchantFeesServiceRepoMod;

	@Autowired
	MerchantChargesandFeesModRep MerchantChargesandFeesModRep;

	@Autowired
	BIPS_PasswordManagement_Repo bIPS_PasswordManagement_Repo;

	@Autowired
	BIPS_MerUserManagement_Repo bIPS_MerUserManagement_Repo;

	@Autowired
	BIPS_MerDeviceManagement_Repo bIPS_MerDeviceManagement_Repo;

	@Autowired
	BIPS_RateMaint_Repo bIPS_RateMaint_Repo;

	@Autowired
	BIPS_FeesCharges_Repo bIPS_FeesCharges_Repo;

	@Autowired
	BIPS_ServiceReq_Repo bIPS_ServiceReq_Repo;

	@Autowired
	BIPS_Alert_Repo bIPS_Alert_Repo;

	@Autowired
	BIPS_Notification_Repo bIPS_Notification_Repo;

	@Autowired
	PasswordService passwordService;

	@Autowired
	BIPS_OutwardTransMonitoring_Repo bIPS_OutwardTransMonitoring_Repo;

	@Autowired
	BIPS_UnitManagement_Repo bIPS_UnitManagement_Repo;

	@Autowired
	BankAndBranchMasterServices bankAndBranchMasterServices;

	@Autowired
	com.bornfire.services.Passwordsendingmail Passwordsendingmail;

	@Autowired
	Two_factor_auth Two_factor_auth;

	@Autowired
	ReferenceCodeRep referenceCodeRep;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	BIPS_Service_ReqMonitoring_Repo bIPS_Service_ReqMonitoring_Repo;
	
	@Autowired
	Sign_Master_Repo Sign_Master_Repo;
	
	@Autowired
	DocumentMaster_Repo documentMaster_Repo;
	
	@Autowired
	SlabRateRepo slabRateRepo;
	

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	@RequestMapping("/")
	public ModelAndView firstPage() {
		return new ModelAndView("AStartpage");
	}

	@RequestMapping("")
	public ModelAndView firstPage1() {
		return new ModelAndView("AStartpage");
	}

	@RequestMapping("/logout")
	public String logout() {
		return "AStartpage.html";
	}

	// ****************************
	// IPSDashboard
	// ****************************

	@RequestMapping("/IPSDashboard")
	public String login() {
		return "IPSDashboard.html";
	}

	@RequestMapping(value = "/IPSDashboard", method = { RequestMethod.GET, RequestMethod.POST })
	public String dashboard(Model md, HttpServletRequest req, String merchant_id, Date tran_date)
			throws ParseException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String USERID = (String) req.getSession().getAttribute("USERID");
		String USERNAME = (String) req.getSession().getAttribute("USERNAME");

		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("USERID", USERID);
		md.addAttribute("USERNAME", USERNAME);
		md.addAttribute("PdfViewer", "Dashboard");

		System.out.println(merchantMasterRep.PiechartCount());
		UserProfile userProfile = userProfileRep.findById(USERID).orElse(new UserProfile());

		if ("Y".equals(userProfile.getAuthenticate_flg())) {
			md.addAttribute("authenticationValue", userProfile.getAuthenticate_flg());
		} else {
			md.addAttribute("authenticationValue", userProfile.getAuthenticate_flg());
		}

		int CountSuccess = bIPS_OutwardTransMonitoring_Repo.getTranSuccessCount();
		int CountFailure = bIPS_OutwardTransMonitoring_Repo.getTransFailureCount();
		int CountVerifiedMerchant = merchantMasterRep.getMerCount();
		int CountUnverifiedMerchant = merchantMasterModRep.getMerCount();
		int totalTransaction = bIPS_OutwardTransMonitoring_Repo.getTodayTotalTransaction();
		int totalTranAmount = bIPS_OutwardTransMonitoring_Repo.getamtTrans();
		md.addAttribute("totalMerchant", CountVerifiedMerchant + CountUnverifiedMerchant);
		md.addAttribute("CountVerifiedMerchant", CountVerifiedMerchant);
		md.addAttribute("CountUnverifiedMerchant", CountUnverifiedMerchant);
		md.addAttribute("countsuccess", CountSuccess);
		md.addAttribute("countfailure", CountFailure);
		md.addAttribute("totalTransaction", totalTransaction);
		md.addAttribute("totalamt", totalTranAmount);
		
		List<Object[]> rawList = merchantMasterRep.getMerIdName();
		List<Map<String, String>> merchantIdName = new ArrayList<>();

		for (Object[] row : rawList) {
		    Map<String, String> map = new HashMap<>();
		    map.put("Id", row[0].toString());
		    map.put("Name", row[1].toString());
		    merchantIdName.add(map);
		}
		
		md.addAttribute("merchantIds", merchantIdName);

		List<Object[]> rawList1 = merchantCategoryRep.getMerCategCode();
		List<Map<String, String>> merchantCategList = new ArrayList<>();

		for (Object[] row : rawList1) {
		    Map<String, String> map = new HashMap<>();
		    map.put("code", row[0].toString());
		    map.put("desc", row[1].toString());
		    merchantCategList.add(map);
		}

		md.addAttribute("merchantCateg", merchantCategList);


		return "IPSDashboard";
	}

	// ****************************
	// User Profile
	// ****************************

	@RequestMapping("/UserProfile")
	public String user() {
		return "UserProfile.html";
	}

	@RequestMapping(value = "UserProfile", method = { RequestMethod.GET, RequestMethod.POST })
	public String userprofile(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req)
			throws SQLException {

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		md.addAttribute("RuleIDType", accessandrolesrepository.roleidtype());
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "UserProfile");

		md.addAttribute("loginuser", userID);
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("userProfiles", userProfileService.getUsersList());
		} else if (formmode.equals("add")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("bankbranch", bipsSolRepository.BankandBranchList());
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
		} else if (formmode.equals("edit")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("userProfile", userProfileService.getUser(userid));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
		} else if (formmode.equals("verify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("userProfile", userProfileService.getUser2(userid));
		} else if (formmode.equals("view")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "view");
			md.addAttribute("userProfile", userProfileService.getUser(userid));
		} else if (formmode.equals("cancel")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "cancel");
			md.addAttribute("userProfile", userProfileService.getUser2(userid));
		} else if (formmode.equals("viewnew")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "viewnew");
			md.addAttribute("userProfile", userProfileService.getUser2(userid));
		} else if (formmode.equals("delete")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("userProfile", userProfileService.getUser(userid));
		}
		return "UserProfile";
	}

	@RequestMapping(value = "createUser", method = RequestMethod.POST)
	@ResponseBody
	public String createuser(@RequestParam("formmode") String formmode, @ModelAttribute UserProfile userprofile,
			@ModelAttribute IPSUserProfileMod userProfile1,
			@RequestParam(value = "file", required = false) MultipartFile file, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (file != null) {
			byte[] byteArr = file.getBytes();
			userProfile1.setPhoto(byteArr);
		}
		String msg = userProfileService.addUser(userProfile1, formmode, userid);
		return msg;
	}

	@RequestMapping(value = "editUser", method = RequestMethod.POST)
	@ResponseBody
	public String editUser(@RequestParam("formmode") String formmode, @ModelAttribute UserProfile userprofile,
			@ModelAttribute IPSUserProfileMod userProfile1,
			@RequestParam(value = "file", required = false) MultipartFile file, Model md, HttpServletRequest rq)
			throws ParseException, IOException {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (file != null) {
			byte[] byteArr = file.getBytes();
			userProfile1.setPhoto(byteArr);
		}
		String msg = userProfileService.editUser(userProfile1, formmode, userid);
		// System.out.println(msg);
		return msg;
	}

	@RequestMapping(value = "deleteUser", method = RequestMethod.POST)
	@ResponseBody
	public String deleteUser(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.deleteUser(userprofile, userid);
		// System.out.println(msg);
		return msg;
	}

	@RequestMapping(value = "verifyUser", method = RequestMethod.POST)
	@ResponseBody
	public String verifyUser(@RequestParam(required = false) String entryuser,
			@RequestParam(required = false) String entrytime, @ModelAttribute IPSUserProfileMod userProfile1, Model md,
			HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		Date systemDate = new Date();
		String msg = userProfileService.verifyUser(userProfile1, userid, entryuser, systemDate);
		md.addAttribute("modtable", userProfileService.deleteUser1(userProfile1, userid));
		return msg;
	}

	@RequestMapping(value = "cancelUser", method = RequestMethod.POST)
	@ResponseBody
	public String cancel(@ModelAttribute UserProfile userprofile, @RequestParam("userid") String userid,
			IPSUserProfileMod userProfilemoden, Model md, HttpServletRequest rq) {
		String inputUser = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("flagchange", userProfileService.cancelUserentity(userprofile, userid, inputUser));
		String msg = userProfileService.cancel(userProfilemoden, userprofile, userid);
		// System.out.println(msg);
		return msg;
	}

	// ****************************
	// Change Password
	// ****************************

	@RequestMapping(value = "changePassword", method = RequestMethod.POST)
	@ResponseBody
	public String changePassword(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
			@ModelAttribute UserProfile userProfile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.changePasswords(userProfile, oldpass, newpass, userid);
		// System.out.println(msg);
		return msg;
	}

	@RequestMapping(value = "changePasswordloginscrren", method = RequestMethod.POST)
	@ResponseBody
	public String changePasswordlogin(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
			@RequestParam("userid") String userid, @ModelAttribute UserProfile userProfile, Model md,
			HttpServletRequest rq) {
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.changePasswords(userProfile, oldpass, newpass, userid);
		// System.out.println(msg);
		return msg;
	}

	@RequestMapping(value = "changePasswordotp", method = RequestMethod.POST)
	@ResponseBody
	public String changePasswordotp(@RequestParam("newpass") String newpass, @RequestParam("userid") String userid,
			@ModelAttribute UserProfile userProfile, Model md, HttpServletRequest rq) {
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.changePasswordotpservice(userProfile, newpass, userid);
		return msg;
	}

	@RequestMapping(value = "getLoginSecurityData", method = RequestMethod.GET)
	@ResponseBody
	public LoginSecurity getLoginSecurityData(Model md, HttpServletRequest rq) {
		LoginSecurity msg = loginSecurityServices.getLoginSec();
		return msg;
	}

	@RequestMapping(value = "passwordReset1", method = RequestMethod.POST)
	@ResponseBody
	public String passwordReset1(@RequestParam("newpass") String newpass, @RequestParam("userid") String changeuserID,
			@ModelAttribute UserProfile userProfile, Model md, HttpServletRequest rq) {
		String loginuser = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.passwordReset1(userProfile, newpass, loginuser, changeuserID);
		// System.out.println(msg);
		return msg;
	}

	@RequestMapping(value = "passwordReset", method = RequestMethod.POST)
	@ResponseBody
	public String passwordReset(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.passwordReset(userprofile, userid);
		// System.out.println(msg);
		return msg;
	}

	// ****************************
	// Service Charges And Fees
	// ****************************

	@RequestMapping(value = "ServiceChargesAndFees")
	public String ServiceChargesAndFees(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String ref_num, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute IPSChargesAndFees ipsChargesAndFees, Model md, HttpServletRequest req) {

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		md.addAttribute("PdfViewer", "ServiceChargesandFees");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("serviceFees", ipsChargesAndFeesRep.findAllCustom());
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", formmode);
			String serviceRef = ipsChargesAndFeesRep.getServiceRef();
			String ServReference;
			if (serviceRef != null) {
				ServReference = "REF0" + (Integer.valueOf(serviceRef) + 1);
			} else {
				ServReference = "REF01";
			}
			md.addAttribute("ServiceRef", ServReference);
			md.addAttribute("ServiceType", referenceCodeRep.getReferenceList("MP01"));
			md.addAttribute("Periodicity", referenceCodeRep.getReferenceList("MP02"));
			md.addAttribute("user_id", userID);
		} else if (formmode.equals("edit")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("serviceFees", ipsChargesAndFeesRep.findByIdReferenceNum(ref_num));
		} else if (formmode.equals("delete")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("serviceFees", ipsChargesAndFeesRep.findByIdReferenceNum(ref_num));
		} else if (formmode.equals("verify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("menuname", "Service Charges And Fees - Verify");
			md.addAttribute("serviceFees", ipsChargesAndFeesRep.findByIdReferenceNum(ref_num));
		} else if (formmode.equals("viewpage")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("menuname", "Service Charges And Fees - View");
			md.addAttribute("serviceFees", ipsChargesAndFeesRep.findByIdReferenceNum(ref_num));
		}
		return "ServiceChargesAndFees";
	}

	@RequestMapping(value = "editChargesandFees")
	@ResponseBody
	public String IPSServiceFeesAdd(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute IPSChargesAndFees ipsChargesAndFees, Model md, HttpServletRequest req) throws IOException {
		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("user_id", userID);
		String msg = loginSecurityServices.addFees(ipsChargesAndFees, formmode, userID);
		md.addAttribute("ServiceFees", msg);
		return msg;
	}

	// ****************************
	// Unused Codes
	// ****************************

	@RequestMapping(value = "LoginSecurity")
	public String loginSecurity(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute LoginSecurity loginSecurity, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", formmode);
			LoginSecurity loginsec = loginSecurityServices.getLoginSec();
			if (loginsec.getCom_flg().equals("Y")) {
				md.addAttribute("compflg", true);
			} else {
				md.addAttribute("compflg", false);
			}
			md.addAttribute("loginsec", loginsec);
		} else if (formmode.equals("submit")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("formmode", formmode);
			String msg = loginSecurityServices.addUser(loginSecurity, formmode);
			md.addAttribute("loginSecurity", msg);
			return msg;
		}
		return "LoginSecurity";
	}

	@RequestMapping(value = "IPSLoginSecurity")
	@ResponseBody
	public String LoginSec(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute LoginSecurity loginSecurity, Model md, HttpServletRequest req) throws IOException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = loginSecurityServices.addUser(loginSecurity, formmode);
		md.addAttribute("loginSecurity", msg);
		return msg;
	}

	// ****************************
	// Access & Roles
	// ****************************

	@RequestMapping(value = "IPSAccessandRoles", method = { RequestMethod.GET, RequestMethod.POST })
	public String IPSAccessandRoles(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "AccessandRole");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menuname", "Access and Roles - List");
			md.addAttribute("formmode", "list");
			md.addAttribute("AccessandRoles", AccessRoleService.rulelist());
		} else if (formmode.equals("add")) {
			md.addAttribute("menuname", "Access and Roles - ADD");
			md.addAttribute("formmode", "add");
			md.addAttribute("Permissions", referenceCodeRep.getReferenceList("AO01"));
			md.addAttribute("Workclass", referenceCodeRep.getReferenceList("AO02"));
		} else if (formmode.equals("edit")) {
			md.addAttribute("menuname", "Access and Roles - EDIT");
			md.addAttribute("formmode", formmode);
			md.addAttribute("Permissions", referenceCodeRep.getReferenceList("AO01"));
			md.addAttribute("Workclass", referenceCodeRep.getReferenceList("AO02"));
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		} else if (formmode.equals("view")) {
			md.addAttribute("menuname", "Access and Roles - INQUIRY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		} else if (formmode.equals("viewnew")) {
			md.addAttribute("menuname", "Access and Roles - INQUIRY");
			md.addAttribute("formmode", "view");
			md.addAttribute("IPSAccessRole", AccessRoleService.getViewNewData(userid));
		} else if (formmode.equals("verify")) {
			md.addAttribute("menuname", "Access and Roles - VERIFY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getModifyData(userid));
		} else if (formmode.equals("delete")) {
			md.addAttribute("menuname", "Access and Roles - DELETE");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getModifyData(userid));
		} else if (formmode.equals("cancel")) {
			md.addAttribute("menuname", "Access and Roles - CANCEL");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getModifyData(userid));
		}
		return "IPSAccessandRoles";
	}

	@RequestMapping(value = "createAccessRole", method = RequestMethod.POST)
	@ResponseBody
	public String createAccessRoleEn(@RequestParam("formmode") String formmode,
			@RequestParam(value = "adminValue", required = false) String adminValue,
			@RequestParam(value = "auditValue", required = false) String auditValue,
			@RequestParam(value = "ipsparavalue", required = false) String ipsparavalue,
			@RequestParam(value = "finalString", required = false) String finalString,
			@RequestParam(value = "userprofilevalue", required = false) String userprofilevalue,
			@RequestParam(value = "accessValue", required = false) String accessValue,
			@RequestParam(value = "servchargeValue", required = false) String servchargeValue,
			@RequestParam(value = "walletfeesValue", required = false) String walletfeesValue,
			@RequestParam(value = "merchantcategValue", required = false) String merchantcategValue,
			@RequestParam(value = "notificationValue", required = false) String notificationValue,
			@RequestParam(value = "useractivityValue", required = false) String useractivityValue,
			@RequestParam(value = "chargebackValue", required = false) String chargebackValue,
			@RequestParam(value = "referenceValue", required = false) String referenceValue,
			@RequestParam(value = "transactionmonitoringValue", required = false) String transactionmonitoringValue,
			@RequestParam(value = "merchantmastValue", required = false) String merchantmastValue,
			@RequestParam(value = "serviceaudValue", required = false) String serviceaudValue,
			@RequestParam(value = "settlementAccountValue", required = false) String settlementAccountValue,
			@RequestParam(value = "bankBranchDetailValue", required = false) String bankBranchDetailValue,
			@RequestParam(value = "bankBranchDetailValue", required = false) String reportsValue,
			@RequestParam(value = "paymentValue", required = false) String paymentValue,
			@ModelAttribute IPSAccessRole accessRole, Model md, HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = AccessRoleService.addPARAMETER(accessRole, userid, formmode, adminValue, userprofilevalue,
				accessValue, ipsparavalue, servchargeValue, walletfeesValue, merchantcategValue, notificationValue,
				auditValue, useractivityValue, serviceaudValue, chargebackValue, transactionmonitoringValue,
				merchantmastValue, finalString, referenceValue, settlementAccountValue, bankBranchDetailValue,
				reportsValue,paymentValue);
		// System.out.println(msg);
		return msg;
	}

	@RequestMapping(value = "deleteAccessRole", method = RequestMethod.POST)
	@ResponseBody
	public String deleteAccessRoleEn(@RequestParam(value = "userid", required = false) String userid, Model md,
			HttpServletRequest rq) {

		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = AccessRoleService.deleteRole(userid);
		// System.out.println(msg);
		return msg;
	}

	// ****************************
	// Snap Chat
	// ****************************

	@RequestMapping(value = "snapShot")
	public String snapShot(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {

			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();/* 01-JUN-2024 */

			md.addAttribute("formmode", "list");
			md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTranDevlst(currentDateRef));
		}
		return "snapshot.html";
	}

	// ****************************
	// User Activity Audit
	// ****************************

	@RequestMapping(value = "userActivityAudit", method = { RequestMethod.GET, RequestMethod.POST })
	public String USERACTIVITIES(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Fromdate, @RequestParam(required = false) String Todate,
			@RequestParam(required = false) String ListFlg, Model md, HttpServletRequest req) throws ParseException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "UserAudit");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
		final Calendar cal = Calendar.getInstance();
		String currentDate = dateFormat.format(cal.getTime());
		String currentFormattedDate = dateFormat1.format(cal.getTime());

		if (ListFlg != null && ListFlg.equals("Y")) {
			try {
				Date fromDateParsed = dateFormat.parse(Fromdate);
				String formattedFromDate = dateFormat1.format(fromDateParsed);
				md.addAttribute("Fromdate", Fromdate);
				md.addAttribute("AuditList", monitorService.getauditListLocal(dateFormat1.parse(formattedFromDate)));
			} catch (ParseException e) {
				md.addAttribute("error", "Invalid Fromdate format. Please use dd/MM/yyyy.");
				return "IPSAudit";
			}
		} else {
			md.addAttribute("Fromdate", currentDate);
			md.addAttribute("AuditList", monitorService.getauditListLocal(dateFormat1.parse(currentFormattedDate)));
		}
		md.addAttribute("menuname", "User Activity Audits");
		md.addAttribute("auditflag", "auditflag");
		md.addAttribute("formmode", "list1");
		return "IPSAudit";
	}

	/*
	 * @RequestMapping(value = "IPSAuditInquiry", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public String BIPSAuditInquiry(@RequestParam(required =
	 * false) String formmode,
	 * 
	 * @RequestParam(required = false) String Fromdate, @RequestParam(required =
	 * false) Optional<Integer> page,
	 * 
	 * @RequestParam(value = "size", required = false) Optional<Integer> size, Model
	 * md, HttpServletRequest req) throws ParseException { String roleId = (String)
	 * req.getSession().getAttribute("ROLEID"); md.addAttribute("IPSRoleMenu",
	 * AccessRoleService.getRoleMenu(roleId)); // Date todate1 = new
	 * SimpleDateFormat("dd/MM/yyyy").parse(Todate); Date fromdate1 = new
	 * SimpleDateFormat("dd/MM/yyyy").parse(Fromdate); md.addAttribute("Fromdate",
	 * Fromdate); // md.addAttribute("Todate", Todate); md.addAttribute("AuditList",
	 * monitorService.getauditListLocal(fromdate1)); md.addAttribute("menuname",
	 * "User activities Audit Log"); md.addAttribute("auditflag", "auditflag");
	 * md.addAttribute("formmode", "list1"); return "IPSAudit"; }
	 */

	@RequestMapping(value = "serviceAudit", method = { RequestMethod.GET, RequestMethod.POST })
	public String serviceAudit(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req)
			throws ParseException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "ServiceAudit");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
		final Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		String date1 = dateFormat1.format(cal.getTime());
		// System.out.println(date + date1);
		md.addAttribute("menuname", "Service Audits");
		md.addAttribute("auditflag", "auditflag");
		md.addAttribute("formmode", "list");
		md.addAttribute("AuditList", monitorService.getAuditInquries(dateFormat1.parse(date1)));
		md.addAttribute("Fromdate", date);
		// md.addAttribute("Todate", date);
		return "IPSOperationAudit";
	}

	@RequestMapping(value = "IPSAuditOperation", method = { RequestMethod.GET, RequestMethod.POST })
	public String BIPSAuditOperation(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Fromdate, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req)
			throws ParseException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		Date fromdate1 = new SimpleDateFormat("dd/MM/yyyy").parse(Fromdate);
		// Date todate1 = new SimpleDateFormat("dd/MM/yyyy").parse(Todate);
		md.addAttribute("Fromdate", Fromdate);
		// md.addAttribute("Todate", Todate);
		md.addAttribute("AuditList", monitorService.getAuditInquries(fromdate1));
		md.addAttribute("menuname", "Operational Audit Log");
		md.addAttribute("auditflag", "auditflag");
		md.addAttribute("formmode", "list1");
		return "IPSOperationAudit";
	}

	private Date previousDay() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	// ****************************
	// Notification Parameter
	// ****************************

	@RequestMapping(value = "NotificationParms", method = { RequestMethod.GET, RequestMethod.POST })
	public String NotificationParmsData(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid,
			@RequestParam(value = "record_srl_no", required = false) String record_srl_no,
			@RequestParam(value = "tranType", required = false) String tranType,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "NotificationParam");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("noti", notificationParmsRep.getlst());
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", formmode);
			String notifyRef = notificationParmsRep.getNotifyRef();
			
			String NotifyReference;
			if (notifyRef != null) {
				NotifyReference = "NP0" + (Integer.valueOf(notifyRef) + 1);
			} else {
				NotifyReference = "NP01";
			}
			String seq = notificationParmsRep.getSequence();
			String seq1=  "EVT"+seq;
			System.out.println(seq1+"seq1");
			
			md.addAttribute("NotifyRef", NotifyReference);
			md.addAttribute("SMSFlag", referenceCodeRep.getReferenceList("MP06"));
			md.addAttribute("EmailFlag", referenceCodeRep.getReferenceList("MP07"));
			md.addAttribute("AlertFlag", referenceCodeRep.getReferenceList("MP08"));
			md.addAttribute("MANDATEID", sequence.generateMandateRefNo());
			md.addAttribute("SEQENCE",seq1);
		} else if (formmode.equals("edit")) {
			md.addAttribute("formmode", formmode);
		} else if (formmode.equals("verify")) {
			md.addAttribute("formmode", formmode);
			NotificationParms notificationParmsReg = notificationParmsServices.getNotificationParmsList(record_srl_no);
			md.addAttribute("notificationParmsProfile", notificationParmsReg);
		} else if (formmode.equals("view")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("RECORD_SRL_NO", record_srl_no);
			NotificationParms notificationParmsReg = notificationParmsServices.getNotificationParmsList(record_srl_no);
			md.addAttribute("notificationParmsProfile", notificationParmsReg);
		} else if (formmode.equals("delete")) {
			md.addAttribute("formmode", formmode);
			NotificationParms notificationParmsReg = notificationParmsServices.getNotificationParmsList(record_srl_no);
			md.addAttribute("notificationParmsProfile", notificationParmsReg);
		}
		return "NotificationParms";
	}

	@RequestMapping(value = "createNotificationParms", method = RequestMethod.POST)
	@ResponseBody
	public String createNotificationParms(@RequestParam("formmode") String formmode,
			@ModelAttribute UserProfile userprofile,
			@ModelAttribute com.bornfire.entity.NotificationParms notificationParmsReg,
			@RequestParam(value = "file", required = false) MultipartFile file, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = notificationParmsServices.createNotificationParms(notificationParmsReg, userid);
		return msg;
	}

	@RequestMapping(value = "editNotificationParms", method = RequestMethod.POST)
	@ResponseBody
	public String editNotificationParms(@RequestParam("formmode") String formmode,
			@ModelAttribute com.bornfire.entity.NotificationParms notificationParmsReg, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = notificationParmsServices.editNotificationParms(notificationParmsReg, userid);
		return msg;
	}

	@RequestMapping(value = "verifyNotificationParms", method = RequestMethod.POST)
	@ResponseBody
	public String verifyNotificationParms(@RequestParam("formmode") String formmode,
			@ModelAttribute com.bornfire.entity.NotificationParms notificationParmsReg, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = notificationParmsServices.verifyNotificationParms(notificationParmsReg, userid);
		return msg;
	}

	@RequestMapping(value = "deleteNotificationParms", method = RequestMethod.POST)
	@ResponseBody
	public String deleteNotificationParms(@RequestParam("formmode") String formmode,
			@ModelAttribute com.bornfire.entity.NotificationParms notificationParmsReg, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = notificationParmsServices.deleteNotificationParms(notificationParmsReg, userid);
		return msg;
	}

	// ****************************
	// Merchant Category Code
	// ****************************

	@RequestMapping(value = "MerchantCategoryCode")
	public String MerchantCategory(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String merchant_acct_no, @RequestParam(required = false) String userid,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute MerchantCategoryCodeEntity bankAgentTable, Model md, HttpServletRequest req)
			throws FileNotFoundException, SQLException, IOException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "MerchantCategoryCode");

		md.addAttribute("formmode", "list");
		String MerCode = merchantCategoryRep.getMerCode();
		int MerchantCode;
		if (MerCode != null) {
			MerchantCode = Integer.valueOf(MerCode) + 1;
		} else {
			MerchantCode = 7000;
		}
		md.addAttribute("MerchantRef", MerchantCode);
		md.addAttribute("merchantcategory", merchantCategoryRep.findAllCustom());
		return "MerchantCategory";
	}

	@RequestMapping(value = "createmerchantcategory", method = RequestMethod.POST)
	@ResponseBody
	public String createmerchantcategory(@RequestParam("formmode") String formmode,
			@ModelAttribute UserProfile userprofile, @RequestParam(required = false) String refcode,
			@RequestParam(required = false) String refdesc,
			@ModelAttribute MerchantCategoryCodeEntity merchantCategoryCodeEntity,
			@RequestParam(value = "file", required = false) MultipartFile file, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = notificationParmsServices.createmerchantcate(refcode, refdesc, userid, formmode);
		// System.out.println(msg);
		return msg;
	}

	// ****************************
	// Merchant Master
	// ****************************


	int count = 0;

	@RequestMapping(value = "merchantReg2")
	public String MerchantMaster(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String merchant_acct_no, @RequestParam(required = false) String userid,
			@RequestParam(required = false) Optional<Integer> page, @RequestParam(required = false) String mlid,
			@RequestParam(required = false) String mpcn, @RequestParam(required = false) String mn,
			@RequestParam(required = false) String mer_id, @RequestParam(required = false) String device_id,
			@RequestParam(required = false) String user_id, @RequestParam(required = false) String merchant_name,
			@RequestParam(required = false) String merchant_user_id,
			@RequestParam(required = false) String merchant_nam, @RequestParam(required = false) String merchant_id,
			@RequestParam(required = false) String merchant_id1,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute MerchantMaster bankAgentTable, Model md, HttpServletRequest req,
			@RequestParam(required = false) String acctcode,
			@RequestParam(value = "tranDate", required = false) String date, String Id, String mer_user_id_r1,
			String merchant_rep_id, String unit_id, Object msg)
			throws FileNotFoundException, SQLException, IOException, ParseException {

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "MerchantMaster");

		int count = this.count;

		String tranDate = "";
		if (date == null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			tranDate = dateFormat.format(previousDay());
		} else {
			tranDate = date;
		}
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("bankDetail", merchantMasterRep.findAllDataHr());
		} else if (formmode.equals("add")) {
			md.addAttribute("tranDate", tranDate);
			md.addAttribute("formmode", formmode);
			 // Fetch all merchant IDs from the database
		    List<MerchantMaster> existingMerchants = merchantMasterRep.CheckMerID(); // Assumes method exists

		    int maxSeq = 0;

		    // Find the highest sequence number
		    for (MerchantMaster merchantId : existingMerchants) {
		        if (merchantId.getMerchant_id().startsWith("M")) {
		            try {
		                int seq = Integer.parseInt(merchantId.getMerchant_id().substring(1)); // Extract number part
		                System.out.println(seq > maxSeq );
		                if (seq > maxSeq) {
		                    maxSeq = seq; // Update highest sequence
		                }
		            } catch (NumberFormatException e) {
		                // Skip invalid IDs
		            }
		        }
		    }

		    // Generate new sequence number
		    int newSeq = maxSeq + 1;
		    String formattedSeq = String.format("%04d", newSeq);
		    
		    // Construct new Merchant ID
		    String merchantIdSeq = "ZZZM" + formattedSeq;
		    String merchant = "M" + formattedSeq;

		    md.addAttribute("MerchantIdSeq", merchantIdSeq);

		    
			md.addAttribute("SlabRate", slabRateRepo.GetList());
			md.addAttribute("merchantcategory", merchantCategoryRep.findAllCustom());
			md.addAttribute("bankAgentName", bankAgentTableRep.findByCustomBankName());
			md.addAttribute("feeDesc", merchantChargesAndFeesRep.findAllCustomNew());
			md.addAttribute("BIPSMerchantnum", merchantServices.generateBIPSNumber());
			md.addAttribute("branchDet", merchantMasterRep.findAllCustom());
			// Reference Code Table
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			md.addAttribute("CurrencyCode", referenceCodeRep.getReferenceList("CC02"));
			md.addAttribute("Merchant_mode", referenceCodeRep.getReferenceList("MM01"));
			md.addAttribute("Merchant_city", referenceCodeRep.getReferenceList("MM02"));
			md.addAttribute("Merchant_notification", referenceCodeRep.getReferenceList("MM03"));
			md.addAttribute("Merchant_type", referenceCodeRep.getReferenceList("MM04"));
		} else if (formmode.equals("view")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("branchDet", merchantMasterRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchant_acct_no", merchant_acct_no);
			md.addAttribute("DocumentList", documentMaster_Repo.findByMer(merchant_acct_no));
			// md.addAttribute("merchantFeeDetailses",
			// merchantMasterRep.findByIdCustomvals(merchant_acct_no));
			md.addAttribute("merchantFeeDetailses", merchantFeesServiceRepo.merchantDetails(merchant_acct_no));
			md.addAttribute("bankAgentName", bankAgentTableRep.findByCustomBankName());
			// UNIT USER PASSWORD
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getPassList(merchant_acct_no));
			md.addAttribute("MerchantIdUse", merchant_acct_no);
			md.addAttribute("MerchantNaUse", merchant_nam);
			md.addAttribute("MerchantUnit", bIPS_UnitManagement_Repo.getUnitlist(merchant_acct_no));
			md.addAttribute("pro", bIPS_MerUserManagement_Repo.getUserManage1(merchant_acct_no));
			md.addAttribute("MerchantDevi", bIPS_MerDeviceManagement_Repo.getaddDevice(merchant_acct_no));
			md.addAttribute("propass", bIPS_PasswordManagement_Repo.getPassmer(merchant_acct_no));
			md.addAttribute("SignMerName", Sign_Master_Repo.getMerId(merchant_acct_no));
		} else if (formmode.equals("viewnew")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("branchDet", merchantMasterModRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchant_acct_no", merchant_acct_no);
			md.addAttribute("DocumentList", documentMaster_Repo.findByMer(merchant_acct_no));
			// md.addAttribute("merchantFeeDetailses",
			// merchantMasterModRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchantFeeDetailses", merchantFeesServiceRepo.merchantDetailsFromMod(merchant_acct_no));
			md.addAttribute("bankAgentName", bankAgentTableRep.findByCustomBankName());
			// UNIT USER PASSWORD
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getPassList(merchant_acct_no));
			md.addAttribute("MerchantDevi", bIPS_MerDeviceManagement_Repo.getaddDevice(merchant_acct_no));
			md.addAttribute("MerchantUnit", bIPS_UnitManagement_Repo.getUnitlist(merchant_acct_no));
			md.addAttribute("pro", bIPS_MerUserManagement_Repo.getUserManage1(merchant_acct_no));
			md.addAttribute("propass", bIPS_PasswordManagement_Repo.getPassmer(merchant_acct_no));
			md.addAttribute("MerchantIdUse", merchant_acct_no);
			md.addAttribute("MerchantNaUse", merchant_nam);
			md.addAttribute("SignMerName", Sign_Master_Repo.getMerId(merchant_acct_no));
		} else if (formmode.equals("edit")) {
			md.addAttribute("formmode", formmode);
			 
			md.addAttribute("branchDet", merchantMasterRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchant_acct_no", merchant_acct_no);
			md.addAttribute("branchDetails", merchantMasterRep.findByIdCustomvals(merchant_acct_no));
			md.addAttribute("merchantFeeDetails", merchantFeesServiceRepo.merchantDetails(merchant_acct_no));
			List<String> merchantFeeDetailsval = merchantChargesAndFeesRep.findAlldescval();
			md.addAttribute("merchantFeeDetailsval", merchantFeeDetailsval);
			List<String> merchantFeeDetailsvalues = merchantFeesServiceRepo.merchantfee();
			md.addAttribute("merchantFeeDetailsvalues", merchantFeeDetailsvalues);
			md.addAttribute("bankAgentName", bankAgentTableRep.findByCustomBankName());
			md.addAttribute("feeDesc", merchantChargesAndFeesRep.findAllCustom());
			md.addAttribute("merchantcategory", merchantCategoryRep.findAllCustom());
			md.addAttribute("bankAgentName", bankAgentTableRep.findByCustomBankName());
			md.addAttribute("feeDesc", merchantChargesAndFeesRep.findAllCustom());
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			// Reference Code Table
			md.addAttribute("Merchant_mode", referenceCodeRep.getReferenceList("MM01"));
			md.addAttribute("Merchant_city", referenceCodeRep.getReferenceList("MM02"));
			md.addAttribute("Merchant_notification", referenceCodeRep.getReferenceList("MM03"));
			md.addAttribute("Merchant_type", referenceCodeRep.getReferenceList("MM04"));
			md.addAttribute("DocumentList", documentMaster_Repo.findByAllMerchant(merchant_acct_no));
			md.addAttribute("SignMerName", Sign_Master_Repo.getMerId(merchant_acct_no));
			
		 

		 
		} else if (formmode.equals("delete")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("merchantFeeDetailses", merchantMasterModRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("branchDet", merchantMasterModRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchant_acct_no", merchant_acct_no);
			md.addAttribute("merchantFeeDetails", merchantFeesServiceRepo.merchantDetails(merchant_acct_no));
			md.addAttribute("DocumentList", documentMaster_Repo.findByMer(merchant_acct_no));
		} else if (formmode.equals("verify")) {
			md.addAttribute("Merchant_mode", referenceCodeRep.getReferenceList("MM01"));
			md.addAttribute("Merchant_city", referenceCodeRep.getReferenceList("MM02"));
			md.addAttribute("Merchant_notification", referenceCodeRep.getReferenceList("MM03"));
			md.addAttribute("Merchant_type", referenceCodeRep.getReferenceList("MM04"));
			md.addAttribute("formmode", formmode);
			md.addAttribute("user_id", userID);
			md.addAttribute("branchDet", merchantMasterModRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchant_acct_no", merchant_acct_no);
			md.addAttribute("merchantFeeDetails", merchantFeesServiceRepo.merchantDetailsFromMod(merchant_acct_no));
			md.addAttribute("branchDets", bIPS_UnitManagement_Repo.getUnitlist(merchant_acct_no));
			md.addAttribute("branchDetsunit", bIPS_UnitManagement_Repo.getUnitlist(merchant_acct_no));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			md.addAttribute("DocumentList", documentMaster_Repo.findByMer(merchant_acct_no));
			md.addAttribute("SignMerName", Sign_Master_Repo.getMerId(merchant_acct_no));
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("menuname", " Merchant -Upload");
			md.addAttribute("menu", "MMenupage");
			md.addAttribute("branchDet", merchantMasterRep.findByIdCustom(merchant_acct_no));
		} else if (formmode.equals("search")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("bankDetail", merchantMasterRep.ALLDATASEARCH(mpcn, mlid, mn));
		} else if (formmode.equals("MerMastadd")) {
			md.addAttribute("formmode", "MerMastadd");
			md.addAttribute("MerchantlegId", merchant_acct_no);
			md.addAttribute("MerchantNa", merchant_nam);
			
			int newSeq = 1;
			String formattedSeq = String.format("%04d", newSeq); // Format as "B0004"

			
			String merunitrid = merchant_acct_no + "B" + formattedSeq;
			md.addAttribute("merunitrid", merunitrid);
			
			String meruserrid = merchant_acct_no + "B" + formattedSeq + "U"+ formattedSeq;
			md.addAttribute("meruserrid", meruserrid);
			
			String merdevicerid = merchant_acct_no + "B" + formattedSeq +  "D" + formattedSeq ;
			md.addAttribute("merdevicerid", merdevicerid);
			

			md.addAttribute("count", count);
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			md.addAttribute("UnitType", referenceCodeRep.getReferenceList("MM11"));
			md.addAttribute("MakerorChecker", referenceCodeRep.getReferenceList("MM12"));
			md.addAttribute("UserStatus", referenceCodeRep.getReferenceList("MM13"));
			md.addAttribute("DeviceStatus", referenceCodeRep.getReferenceList("MM14"));
			md.addAttribute("branchDet", merchantMasterModRep.findByIdCustom1(merchant_acct_no));
		} else if (formmode.equals("MerMastview")) {
			md.addAttribute("formmode", "MerMastview");
			md.addAttribute("MerchantView", merchantMasterRep.getMerlst(merchant_id));
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getPassList(merchant_id));
			md.addAttribute("MerchantDevi", bIPS_MerDeviceManagement_Repo.getaddDevice(merchant_id));
			md.addAttribute("MerchantUnit", bIPS_UnitManagement_Repo.getUnitlist(merchant_id));
			md.addAttribute("pro", bIPS_MerUserManagement_Repo.getUserManage1(merchant_id));
			md.addAttribute("propass", bIPS_PasswordManagement_Repo.getPassmer(merchant_id));
			md.addAttribute("MerchantIdUse", merchant_id);
			md.addAttribute("MerchantNaUse", merchant_nam);
		} else if (formmode.equals("MerMastvi")) {
			md.addAttribute("formmode", "MerMastvi");
			md.addAttribute("user_id", userID);
			md.addAttribute("Meruserlist", bIPS_MerUserManagement_Repo.getuser(user_id));
		} else if (formmode.equals("MerMastvie")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "MerMastvie");
			md.addAttribute("Merdevicelist", bIPS_MerDeviceManagement_Repo.getdevice(device_id));
		} else if (formmode.equals("UsermAdd")) {
			md.addAttribute("formmode", "UsermAdd");
			md.addAttribute("MakerorChecker", referenceCodeRep.getReferenceList("MM12"));
			md.addAttribute("UserStatus", referenceCodeRep.getReferenceList("MM13"));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			md.addAttribute("pro", bIPS_MerUserManagement_Repo.getUserManage1(merUserId));
			md.addAttribute("MerdeviceId", bIPS_MerDeviceManagement_Repo.getdeviceId(merchant_id));
			md.addAttribute("MerchantUnitId", bIPS_UnitManagement_Repo.getpartUnitId(merchant_id));
			md.addAttribute("MerchantUnitName", bIPS_UnitManagement_Repo.getpartUnitName(merchant_id));
			md.addAttribute("MerchantUnitType", bIPS_UnitManagement_Repo.getpartUnitType(merchant_id));
			md.addAttribute("MerchantIdUse", merchant_id);
			md.addAttribute("MerchantNaUse", merchant_nam);
			List<BIPS_Mer_User_Management_Entity> existingMerchants = bIPS_MerUserManagement_Repo.getmerid(merchant_id); 
			int maxSeq = 0;  // Track the highest BXXXX
			String FirstMerchant = null;
			// Find the highest BXXXX for the given MXXXX
			for (BIPS_Mer_User_Management_Entity merchantId : existingMerchants) {
				System.out.println(merchantId.getMerchant_user_id()+"87568758");
			    if (merchantId.getMerchant_user_id().startsWith("M")) {
			        String lastMerchantId = merchantId.getUser_id(); // Example: "M001B003"
			        System.out.println(lastMerchantId);
			        try {
			            // Split the string at 'B' → ["M001", "003"]
			            String[] parts = lastMerchantId.split("U"); 
			            if (parts.length == 2) {
			                FirstMerchant = parts[0]; // Store "M001"
			                int seq = Integer.parseInt(parts[1]); // Extract BXXXX
			                if (seq > maxSeq) {
			                    maxSeq = seq; // Update highest sequence
			                }
			            }
			        } catch (NumberFormatException e) {
			            // Handle invalid unit IDs gracefully
			        }
			    }
			}

			// Generate the next BXXXX sequence
			int newSeq = maxSeq + 1;
			String formattedSeq = String.format("%04d", newSeq); // Format as "B0004"

			// Construct new Unit ID
			String newuserid = FirstMerchant + "U" + formattedSeq;

			md.addAttribute("UserId", newuserid);


		} else if (formmode.equals("UsermEdit")) {
			md.addAttribute("formmode", "UsermEdit");
			md.addAttribute("Meruserlist", bIPS_MerUserManagement_Repo.getuser(user_id));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
		} else if (formmode.equals("UsermVerify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UsermVerify");
			md.addAttribute("Meruserlist", bIPS_MerUserManagement_Repo.getuser(user_id));
		} else if (formmode.equals("DevicemAdd")) {
			md.addAttribute("formmode", "DevicemAdd");
			md.addAttribute("DeviceStatus", referenceCodeRep.getReferenceList("MM14"));
			md.addAttribute("Meruserid", bIPS_MerUserManagement_Repo.getuserid(merchant_id));
			md.addAttribute("MerchantUnitId", bIPS_UnitManagement_Repo.getpartUnitId(merchant_id));
			md.addAttribute("MerchantUnitName", bIPS_UnitManagement_Repo.getpartUnitName(merchant_id));
			md.addAttribute("MerchantUnitType", bIPS_UnitManagement_Repo.getpartUnitType(merchant_id));
			md.addAttribute("user_id", userID);
			md.addAttribute("MerchantIdDev", merchant_id);
			md.addAttribute("MerchantNaDev", merchant_nam);
			List<BIPS_Mer_Device_Management_Entity> existingMerchants = bIPS_MerDeviceManagement_Repo.getMerchantId(merchant_id); 
			int maxSeq = 0;  // Track the highest BXXXX
			String FirstMerchant = null;
			// Find the highest BXXXX for the given MXXXX
			for (BIPS_Mer_Device_Management_Entity merchantId : existingMerchants) {
				System.out.println(merchantId.getMerchant_user_id()+"87568758");
			    if (merchantId.getMerchant_user_id().startsWith("M")) {
			        String lastMerchantId = merchantId.getDevice_id(); // Example: "M001B003"
			        System.out.println(lastMerchantId);
			        try {
			            // Split the string at 'B' → ["M001", "003"]
			            String[] parts = lastMerchantId.split("D"); 
			            if (parts.length == 2) {
			                FirstMerchant = parts[0]; // Store "M001"
			                int seq = Integer.parseInt(parts[1]); // Extract BXXXX
			                if (seq > maxSeq) {
			                    maxSeq = seq; // Update highest sequence
			                }
			            }
			        } catch (NumberFormatException e) {
			            // Handle invalid unit IDs gracefully
			        }
			    }
			}

			// Generate the next BXXXX sequence
			int newSeq = maxSeq + 1;
			String formattedSeq = String.format("%04d", newSeq); // Format as "B0004"

			// Construct new Unit ID
			String deviceid = FirstMerchant + "U" + formattedSeq;

			md.addAttribute("DeviceId", deviceid);

		} else if (formmode.equals("DevicemEdit")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "DevicemEdit");
			md.addAttribute("Merdevicelist", bIPS_MerDeviceManagement_Repo.getdevice(device_id));
		} else if (formmode.equals("DevicemVerify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "DevicemVerify");
			md.addAttribute("Merdevicelist", bIPS_MerDeviceManagement_Repo.getdevice(device_id));
		} else if (formmode.equals("PassView")) {
			md.addAttribute("formmode", "PassView");
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getRepId(merchant_rep_id));
		} else if (formmode.equals("PassEdit")) {
			md.addAttribute("formmode", "PassEdit");
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getRepId(merchant_rep_id));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
		} else if (formmode.equals("PassVerify")) {
			md.addAttribute("formmode", "PassVerify");
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getRepId(merchant_rep_id));
		} else if (formmode.equals("UnitView")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitView");
			md.addAttribute("user_id", userID);
			md.addAttribute("MerUnit", bIPS_UnitManagement_Repo.getUnitId(unit_id));
		} else if (formmode.equals("UnitEdit")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitEdit");
			md.addAttribute("user_id", userID);
			md.addAttribute("MerUnit", bIPS_UnitManagement_Repo.getUnitId(unit_id));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
		} else if (formmode.equals("UnitmAdd")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitmAdd");
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			md.addAttribute("UnitType", referenceCodeRep.getReferenceList("MM11"));
			md.addAttribute("MerchantIdDev", merchant_id);
			md.addAttribute("MerchantNaDev", merchant_nam);
			
			// Retrieve all existing unit IDs linked to this Merchant ID
			List<BIPS_Unit_Mangement_Entity> existingMerchants = bIPS_UnitManagement_Repo.UnitId(merchant_id); 
			int maxSeq = 0;  // Track the highest BXXXX
			String FirstMerchant = null;
			// Find the highest BXXXX for the given MXXXX
			for (BIPS_Unit_Mangement_Entity merchantId : existingMerchants) {
				System.out.println(merchantId.getMerchant_user_id()+"87568758");
			    if (merchantId.getMerchant_user_id().startsWith("M")) {
			        String lastMerchantId = merchantId.getUnit_id(); // Example: "M001B003"
			        try {
			            // Split the string at 'B' → ["M001", "003"]
			            String[] parts = lastMerchantId.split("B"); 
			            if (parts.length == 2) {
			                FirstMerchant = parts[0]; // Store "M001"
			                int seq = Integer.parseInt(parts[1]); // Extract BXXXX
			                if (seq > maxSeq) {
			                    maxSeq = seq; // Update highest sequence
			                }
			            }
			        } catch (NumberFormatException e) {
			            // Handle invalid unit IDs gracefully
			        }
			    }
			}

			// Generate the next BXXXX sequence
			int newSeq = maxSeq + 1;
			String formattedSeq = String.format("%04d", newSeq); // Format as "B0004"

			// Construct new Unit ID
			String newUnitId = FirstMerchant + "B" + formattedSeq;

			md.addAttribute("UnitId", newUnitId);


		} else if (formmode.equals("UnitVerify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitVerify");
			md.addAttribute("user_id", userID);
			md.addAttribute("MerUnit", bIPS_UnitManagement_Repo.getUnitId(unit_id));
		} else if (formmode.equals("UploadFile")) {
			md.addAttribute("formmode", "UploadFile");
		} else if (formmode.equals("UploadUnit")) {
			md.addAttribute("formmode", "UploadUnit");
			md.addAttribute("MerId",merchant_id);
			System.out.println("MerId "+merchant_id);
			md.addAttribute("MerName",merchant_nam);
			System.out.println("MerName "+merchant_nam);
		} else if (formmode.equals("UploadUser")) {
			md.addAttribute("formmode", "UploadUser");
			md.addAttribute("MerId",merchant_id);
			System.out.println("MerId "+merchant_id);
			md.addAttribute("MerName",merchant_nam);
			System.out.println("MerName "+merchant_nam);
		} else if (formmode.equals("UploadDevice")) {
			md.addAttribute("formmode", "UploadDevice");
			md.addAttribute("MerId",merchant_id);
			System.out.println("MerId "+merchant_id);
			md.addAttribute("MerName",merchant_nam);
			System.out.println("MerName "+merchant_nam);
		} else if (formmode.equals("HoldReject")) {
			md.addAttribute("formmode", "HoldReject");
			md.addAttribute("bankDetail", merchantMasterModRep.holdrejectlist());
		}else if (formmode.equals("holdscrn")) {
			md.addAttribute("Merchant_mode", referenceCodeRep.getReferenceList("MM01"));
			md.addAttribute("Merchant_city", referenceCodeRep.getReferenceList("MM02"));
			md.addAttribute("Merchant_notification", referenceCodeRep.getReferenceList("MM03"));
			md.addAttribute("Merchant_type", referenceCodeRep.getReferenceList("MM04"));
			md.addAttribute("formmode", formmode);
			md.addAttribute("user_id", userID);
			md.addAttribute("branchDet", merchantMasterModRep.findByIdCustom(merchant_acct_no));
			md.addAttribute("merchant_acct_no", merchant_acct_no);
			md.addAttribute("merchantFeeDetails", merchantFeesServiceRepo.merchantDetailsFromMod(merchant_acct_no));
			md.addAttribute("branchDets", bIPS_UnitManagement_Repo.getUnitlist(merchant_acct_no));
			md.addAttribute("branchDetsunit", bIPS_UnitManagement_Repo.getUnitlist(merchant_acct_no));
			md.addAttribute("CountryCode", referenceCodeRep.getReferenceList("CC01"));
			md.addAttribute("DocumentList", documentMaster_Repo.findByMer(merchant_acct_no));
			md.addAttribute("SignMerName", Sign_Master_Repo.getMerId(merchant_acct_no));
		}
		return "IPSMerchantMaster";
	}

	@RequestMapping("verifyUserDet")
	@ResponseBody
	public String verifyUserDet(HttpServletRequest req, @RequestParam(required = false) String user_ids) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String response = bankAndBranchMasterServices.VerifyUserMer(user_ids, userID);
		return response;
	}

	@RequestMapping("verifyDevice")
	@ResponseBody
	public String verifyDevice(HttpServletRequest req, @RequestParam(required = false) String user_ids) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String response = bankAndBranchMasterServices.VerifyDeviceMer(user_ids, userID);
		return response;
	}

	@RequestMapping("verifyPassword")
	@ResponseBody
	public String verifyPassword(HttpServletRequest req, @RequestParam(required = false) String rep_id) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String response = bankAndBranchMasterServices.VerifyPassMer(rep_id, userID);
		return response;
	}

	@RequestMapping("verifyService")
	@ResponseBody
	public String verifyService(HttpServletRequest req, @RequestParam(required = false) String user_ids) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String response = bankAndBranchMasterServices.VerifyServiceMer(userID, user_ids);
		return response;
	}

	// ****************************
	// Service Charges And Fees
	// ****************************

	@RequestMapping(value = "MerchantServiceChargesAndFees")
	public String MerchantServiceChargesAndFees(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String ref_num, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute IPSChargesAndFees ipsChargesAndFees, Model md, HttpServletRequest req) throws SQLException {

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "MerchantChargesandFees");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("menuname", "Merchant Service Charges And Fees");
			md.addAttribute("menu", "MMenupage");
			md.addAttribute("serviceFees", merchantChargesAndFeesRep.findAllCustom());
		} else if (formmode.equals("add")) {
			md.addAttribute("menuname", "Merchant Service Charges And Fees - Add");
			md.addAttribute("formmode", formmode);
			String MerchantRef = merchantChargesAndFeesRep.getMerchantRef();
			String MerchantReference;
			if (MerchantRef != null) {
				MerchantReference = "REF0" + (Integer.valueOf(MerchantRef) + 1);
			} else {
				MerchantReference = "REF01";
			}
			md.addAttribute("MerchantRef", MerchantReference);
			md.addAttribute("FeeDerivation", referenceCodeRep.getReferenceList("MP03"));
			md.addAttribute("AccountType", referenceCodeRep.getReferenceList("MP04"));
			md.addAttribute("FeeType", referenceCodeRep.getReferenceList("MP05"));
			md.addAttribute("serviceFees", new IPSChargesAndFees());
			md.addAttribute("feestype", merchantChargesAndFeesRep.findAllVatfeesdata());
		} else if (formmode.equals("edit")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("user_id", userID);
			md.addAttribute("menuname", "Merchant Service Charges And Fees - Modify");
			md.addAttribute("serviceFees", merchantChargesAndFeesRep.findByIdReferenceNum(ref_num));
			md.addAttribute("feestype", merchantChargesAndFeesRep.findAllVatfeesdata());
		} else if (formmode.equals("delete")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", formmode);
			md.addAttribute("menuname", "Merchant Service Charges And Fees - Delete");
			md.addAttribute("serviceFees", MerchantChargesandFeesModRep.findByIdReferenceNum(ref_num));
		} else if (formmode.equals("verify")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("user_id", userID);
			md.addAttribute("menuname", "Merchant Service Charges And Fees - Verify");
			md.addAttribute("serviceFees", MerchantChargesandFeesModRep.findByIdReferenceNum(ref_num));
		} else if (formmode.equals("viewpage")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("menuname", "Merchant Service Charges And Fees - View");
			md.addAttribute("serviceFees", merchantChargesAndFeesRep.findByIdReferenceNum(ref_num));
		} else if (formmode.equals("viewnewpage")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("menuname", "Merchant Service Charges And Fees - View");
			md.addAttribute("serviceFees", MerchantChargesandFeesModRep.findByIdReferenceNum(ref_num));
		}
		return "MerchantServiceChargesAndFees";
	}

	@RequestMapping(value = "MerchanteditChargesandFees")
	@ResponseBody
	public String MerchanteditChargesandFees(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute IPSChargesAndFees ipsChargesAndFees, Model md, HttpServletRequest req) throws IOException {

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = loginSecurityServices.addFees(ipsChargesAndFees, formmode, userID);
		// System.out.println(msg);
		return msg;
	}

	// ****************************
	// Merchant User Management
	// ****************************

	@RequestMapping(value = "userManagement", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> userRegister(@RequestParam(required = false) String merchant_user_id, Model md,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest rq,
			@ModelAttribute BIPS_Mer_User_Management_Entity bIPS_Mer_User_Management_Entity, HttpServletRequest req)
			throws IOException {
		if (file != null) {
			byte[] byteArr = file.getBytes();
			bIPS_Mer_User_Management_Entity.setPhoto(byteArr);
		}
		String userID = (String) req.getSession().getAttribute("USERID");
		String password = env.getProperty("user.password");
		try {
			String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
			if ("Active".equals(bIPS_Mer_User_Management_Entity.getUser_status1())) {
				bIPS_Mer_User_Management_Entity.setUser_disable_flag1("N");
			} else {
				bIPS_Mer_User_Management_Entity.setUser_disable_flag1("Y");
			}
			bIPS_Mer_User_Management_Entity.setDel_flag1("N");
			bIPS_Mer_User_Management_Entity.setPassword_life1("180");
			bIPS_Mer_User_Management_Entity.setModify_flag("N");
			bIPS_Mer_User_Management_Entity.setEntry_flag("N");
			bIPS_Mer_User_Management_Entity.setEntry_time(new Date());
			bIPS_Mer_User_Management_Entity.setEntry_user(userID);
			bIPS_Mer_User_Management_Entity.setMerchant_user_id(merchant_user_id);
			bIPS_Mer_User_Management_Entity.setUser_locked_flg("N");
			bIPS_Mer_User_Management_Entity.setNo_of_attmp("0");
			bIPS_Mer_User_Management_Entity.setPassword1(encryptedPassword);
			bIPS_Mer_User_Management_Entity.setLogin_status1("N");
			bIPS_Mer_User_Management_Entity.setUser_category("USER");
			bIPS_MerUserManagement_Repo.save(bIPS_Mer_User_Management_Entity);

			// Audit
			IPSAuditTable audit = new IPSAuditTable();
			String audit_ref_no = sequence.generateRequestUUId();
			audit.setAudit_date(new Date());
			audit.setAudit_table("BIPS_MERCHANT_USER_MANAGEMENT");
			audit.setAudit_screen("MERCHANT USER - CREATED");
			audit.setFunc_code("MERCHANT USER CREATION");
			audit.setRemarks(
					bIPS_Mer_User_Management_Entity.getMerchant_user_id() + " : Merchant User Created Successfully");
			audit.setEvent_id(bIPS_Mer_User_Management_Entity.getMerchant_user_id());
			audit.setEvent_name(bIPS_Mer_User_Management_Entity.getMerchant_name());
			audit.setAuth_user(userID);
			audit.setAuth_time(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userID);
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);
			return ResponseEntity.ok("User registered successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@RequestMapping(value = "userManagementuser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> userManagementuser(@RequestParam(required = false) String password1,
			@RequestParam(value = "file", required = false) MultipartFile file, Model md, HttpServletRequest rq,
			@ModelAttribute BIPS_Mer_User_Management_Entity bIPS_Mer_User_Management_Entity, HttpServletRequest req)
			throws IOException {

		if (file != null) {
			byte[] byteArr = file.getBytes();
			bIPS_Mer_User_Management_Entity.setPhoto(byteArr);
		}
		String userID = (String) req.getSession().getAttribute("USERID");
		String password = env.getProperty("user.password");
		try {
			String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
			if ("Active".equals(bIPS_Mer_User_Management_Entity.getUser_status1())) {
				bIPS_Mer_User_Management_Entity.setUser_disable_flag1("N");
			} else {
				bIPS_Mer_User_Management_Entity.setUser_disable_flag1("Y");
			}
			bIPS_Mer_User_Management_Entity.setDel_flag1("N");
			bIPS_Mer_User_Management_Entity.setPassword_life1("180");
			bIPS_Mer_User_Management_Entity.setModify_flag("N");
			bIPS_Mer_User_Management_Entity.setEntry_flag("N");
			bIPS_Mer_User_Management_Entity.setEntry_time(new Date());
			bIPS_Mer_User_Management_Entity.setUser_locked_flg("N");
			bIPS_Mer_User_Management_Entity.setEntry_user(userID);
			bIPS_Mer_User_Management_Entity.setNo_of_attmp("0");
			bIPS_Mer_User_Management_Entity.setUser_category("User");
			bIPS_Mer_User_Management_Entity.setPassword1(encryptedPassword);
			bIPS_Mer_User_Management_Entity.setLogin_status1("N");
			bIPS_Mer_User_Management_Entity.setUser_disable_flag1("N");
			bIPS_Mer_User_Management_Entity.setLogin_channel1("WEB");
			bIPS_MerUserManagement_Repo.save(bIPS_Mer_User_Management_Entity);
			return ResponseEntity.ok("User registered successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@RequestMapping(value = "deviceManagement", method = RequestMethod.POST)
	@ResponseBody
	public String deviceManagement(@RequestParam(required = false) String merchant_user_id, Model md,
			HttpServletRequest rq, @ModelAttribute BIPS_Mer_Device_Management_Entity bIPS_Mer_Device_Management_Entity,
			HttpServletRequest req) {
		String userID = (String) req.getSession().getAttribute("USERID");
		BIPS_Mer_Device_Management_Entity up = bIPS_Mer_Device_Management_Entity;
		if (up.getDevice_status().equals("ACTIVE")) {
			up.setDisable_flag("N");
		} else {
			up.setDisable_flag("Y");
		}
		up.setModify_flag("N");
		up.setEntry_flag("N");
		up.setDel_flg("N");
		up.setEntry_time(new Date());
		up.setEntry_user(userID);
		up.setMerchant_legal_user_id(up.getMerchant_user_id());
		up.setMerchant_corporate_name(up.getMerchant_name());
		bIPS_MerDeviceManagement_Repo.save(up);

		// Audit
		IPSAuditTable audit = new IPSAuditTable();
		String audit_ref_no = sequence.generateRequestUUId();
		audit.setAudit_date(new Date());
		audit.setAudit_table("BIPS_MERCHANT_DEVICE_MANAGEMENT");
		audit.setAudit_screen("MERCHANT DEVICE - CREATED");
		audit.setFunc_code("MERCHANT DEVICE CREATION");
		audit.setRemarks(up.getMerchant_user_id() + " : Merchant Device Created Successfully");
		audit.setEvent_id(up.getMerchant_user_id());
		audit.setEvent_name(up.getMerchant_name());
		audit.setAuth_user(userID);
		audit.setAuth_time(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(userID);
		audit.setModi_details("-");
		audit.setAudit_ref_no(audit_ref_no);
		ipsAuditTableRep.save(audit);
		return "Added Successfully";
	}

	@RequestMapping(value = "deviceManagementsub", method = RequestMethod.POST)
	@ResponseBody
	public String deviceManagementsub(Model md, HttpServletRequest req, @RequestParam(required = false) String deviceid,
			@ModelAttribute BIPS_Mer_Device_Management_Entity bIPS_Mer_Device_Management_Entity) {

		System.out.println("DeviceId: " + deviceid);
		String userID = (String) req.getSession().getAttribute("USERID");

		// Convert deviceid to lowercase before saving
		/*
		 * if (deviceid != null && !deviceid.isEmpty()) { deviceid =
		 * deviceid.toLowerCase(); }
		 */

		// Assuming you're setting the device ID in the entity
		bIPS_Mer_Device_Management_Entity.setDevice_id(deviceid);
		;

		if ("ACTIVE".equals(bIPS_Mer_Device_Management_Entity.getDevice_status())) {
			bIPS_Mer_Device_Management_Entity.setDisable_flag("N");
		} else {
			bIPS_Mer_Device_Management_Entity.setDisable_flag("Y");
		}
		bIPS_Mer_Device_Management_Entity.setDel_flg("N");
		bIPS_Mer_Device_Management_Entity.setModify_flag("N");
		bIPS_Mer_Device_Management_Entity.setEntry_flag("N");
		bIPS_Mer_Device_Management_Entity.setEntry_time(new Date());
		bIPS_Mer_Device_Management_Entity.setEntry_user(userID);
		bIPS_Mer_Device_Management_Entity
				.setMerchant_legal_user_id(bIPS_Mer_Device_Management_Entity.getMerchant_user_id());
		bIPS_Mer_Device_Management_Entity
				.setMerchant_corporate_name(bIPS_Mer_Device_Management_Entity.getMerchant_name());

		bIPS_MerDeviceManagement_Repo.save(bIPS_Mer_Device_Management_Entity);

		return "Device Added Successfully";
	}

	@PostMapping(value = "passwordedit")
	@ResponseBody
	public ResponseEntity<String> registerUser(@RequestParam(required = false) String password, Model md,
			HttpServletRequest req, HttpServletRequest rq,
			@ModelAttribute BIPS_Password_Management_Entity bIPS_Password_Management_Entity) {

		String userID = (String) req.getSession().getAttribute("USERID");

		try {
			BIPS_Password_Management_Entity existingDataList = bIPS_PasswordManagement_Repo
					.getmerchantrep(bIPS_Password_Management_Entity.getMerchant_rep_id());
			if (existingDataList == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
			}
			bIPS_Password_Management_Entity.setDel_flag("N");
			bIPS_Password_Management_Entity.setEntry_flag("N");
			bIPS_Password_Management_Entity.setModify_flag("Y");
			bIPS_Password_Management_Entity.setLogin_status("Y");
			bIPS_Password_Management_Entity.setModify_user(userID);
			bIPS_Password_Management_Entity.setModify_time(new Date());
			bIPS_Password_Management_Entity.setMerchant_legal_user_id(existingDataList.getMerchant_legal_user_id());
			bIPS_Password_Management_Entity.setMerchant_corporate_name(existingDataList.getMerchant_corporate_name());
			bIPS_Password_Management_Entity.setPassword(existingDataList.getPassword());
			bIPS_Password_Management_Entity.setPassword_expiry_date(existingDataList.getPassword_expiry_date());
			bIPS_Password_Management_Entity.setPassword_life(existingDataList.getPassword_life());
			bIPS_Password_Management_Entity.setUser_disable_flag(existingDataList.getUser_disable_flag());
			bIPS_Password_Management_Entity.setUnit_id(existingDataList.getUnit_id());
			bIPS_Password_Management_Entity.setUnit_name(existingDataList.getUnit_name());
			bIPS_Password_Management_Entity.setUnit_type(existingDataList.getUnit_type());
			bIPS_Password_Management_Entity.setPwlog_flg(existingDataList.getPwlog_flg());
			bIPS_Password_Management_Entity.setNo_of_attmp(existingDataList.getNo_of_attmp());
			bIPS_Password_Management_Entity.setUser_locked_flg(existingDataList.getUser_locked_flg());
			bIPS_Password_Management_Entity.setEntry_user(existingDataList.getEntry_user());
			bIPS_Password_Management_Entity.setUser_category(existingDataList.getUser_category());
			bIPS_Password_Management_Entity.setLogin_status(existingDataList.getLogin_status());
			bIPS_PasswordManagement_Repo.save(bIPS_Password_Management_Entity);
			return ResponseEntity.ok("Password Modified successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@RequestMapping(value = "UserProfiles", method = RequestMethod.POST)
	@ResponseBody
	public String UserProfiles(@RequestParam(required = false) String user_id, Model md, HttpServletRequest rq,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@ModelAttribute BIPS_Mer_User_Management_Entity bIPS_Mer_User_Management_Entity, HttpServletRequest req)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String userid = (String) req.getSession().getAttribute("USERID");
		//String password = env.getProperty("user.password");
		Date currentTime = new Date();
		if (file != null) {
			byte[] byteArr = file.getBytes();
			bIPS_Mer_User_Management_Entity.setPhoto(byteArr);
		}

		BIPS_Mer_User_Management_Entity exist = bIPS_MerUserManagement_Repo
				.getuser(bIPS_Mer_User_Management_Entity.getUser_id());
		BIPS_Mer_User_Management_Entity up = bIPS_Mer_User_Management_Entity;
		up.setModify_user(userid);
		up.setModify_time(currentTime);
		up.setModify_flag("Y");
		up.setEntry_flag("N");
		up.setPassword1(exist.getPassword1());
		up.setPassword_life1(exist.getPassword_life1());
		up.setUser_disable_flag1(exist.getUser_disable_flag1());
		up.setDel_flag1(exist.getDel_flag1());
		up.setLogin_status1(exist.getLogin_status1());
		up.setLogin_channel1(exist.getLogin_channel1());
		up.setNo_of_attmp(exist.getNo_of_attmp());
		up.setUser_locked_flg(exist.getUser_locked_flg());
		up.setUser_category(exist.getUser_category());
		bIPS_MerUserManagement_Repo.save(up);

		IPSAuditTable audit = new IPSAuditTable();
		String audit_ref_no = sequence.generateRequestUUId();
		audit.setAudit_date(new Date());
		audit.setAudit_table("BIPS_MERCHANT_USER_MANAGEMENT");
		audit.setAudit_screen("MERCHANT USER - MODIFIED");
		audit.setFunc_code("MERCHANT USER MODIFICATION");
		audit.setRemarks(up.getUser_id() + " : MERCHANT USER MODIFIED SUCCESSFULLY");
		audit.setEvent_id(up.getMerchant_user_id());
		audit.setEvent_name(up.getMerchant_name());
		audit.setAuth_user(userid);
		audit.setAuth_time(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(userid);
		audit.setModi_details("-");
		audit.setAudit_ref_no(audit_ref_no);
		ipsAuditTableRep.save(audit);

		return "Modified Successfully";
	}

	@RequestMapping(value = "DeviceMan", method = RequestMethod.POST)
	@ResponseBody
	public String DeviceMan(@RequestParam(required = false) String device_id,
			@RequestParam(required = false) String entryuser, String merchant_user_id, Model md, HttpServletRequest rq,
			@ModelAttribute BIPS_Mer_Device_Management_Entity bIPS_Mer_Device_Management_Entity,
			HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		Date currentTime = new Date();
		// System.out.println(device_id);
		BIPS_Mer_Device_Management_Entity up = bIPS_Mer_Device_Management_Entity;
		up.setModify_user(userid);
		up.setModify_time(currentTime);
		up.setEntry_user(entryuser);
		up.setModify_flag("Y");
		up.setEntry_flag("N");
		up.setDisable_flag("N");
		up.setDel_flg("N");
		bIPS_MerDeviceManagement_Repo.save(up);

		IPSAuditTable audit = new IPSAuditTable();
		String audit_ref_no = sequence.generateRequestUUId();
		audit.setAudit_date(new Date());
		audit.setAudit_table("BIPS_MERCHANT_DEVICE_MANAGEMENT");
		audit.setAudit_screen("MERCHANT DEVICE - MODIFIED");
		audit.setFunc_code("MERCHANT DEVICE MODIFICATION");
		audit.setRemarks(up.getDevice_id() + " : MERCHANT DEVICE MODIFIED SUCCESSFULLY");
		audit.setEvent_id(up.getMerchant_user_id());
		audit.setEvent_name(up.getMerchant_name());
		audit.setAuth_user(userid);
		audit.setAuth_time(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(userid);
		audit.setModi_details("-");
		audit.setAudit_ref_no(audit_ref_no);
		ipsAuditTableRep.save(audit);

		return "Device Modified Successfully";
	}

	@RequestMapping(value = "UnitManage", method = RequestMethod.POST)
	@ResponseBody
	public String UnitManage(@RequestParam(required = false) String unit_id,
			@RequestParam(required = false) String entryuser, String merchant_user_id, Model md, HttpServletRequest rq,
			@ModelAttribute BIPS_Unit_Mangement_Entity bIPS_Unit_Mangement_Entity,
			BIPS_Password_Management_Entity BIPS_Password_Management_Entity, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		List<BIPS_Password_Management_Entity> up2 = bIPS_PasswordManagement_Repo.getunit(unit_id);
		Date currentTime = null;
		if ((Objects.nonNull(up2))) {
			currentTime = new Date();
			// System.out.println(unit_id);
			BIPS_Unit_Mangement_Entity up = bIPS_Unit_Mangement_Entity;
			up.setModify_user(userid);
			up.setModify_time(currentTime);
			up.setModify_flag("Y");
			up.setEntry_flag("N");
			up.setDel_flg("N");
			up.setEntry_user(entryuser);
			bIPS_UnitManagement_Repo.save(up);

			IPSAuditTable audit = new IPSAuditTable();
			String audit_ref_no = sequence.generateRequestUUId();
			audit.setAudit_date(new Date());
			audit.setAudit_table("BIPS_MERCHANT_UNIT_MANAGEMENT");
			audit.setAudit_screen("MERCHANT UNIT - MODIFIED");
			audit.setFunc_code("MERCHANT UNIT MODIFICATION");
			audit.setRemarks(up.getUnit_id() + " : MERCHANT UNIT MODIFIED SUCCESSFULLY");
			audit.setEvent_id(up.getMerchant_user_id());
			audit.setEvent_name(up.getMerchant_name());
			audit.setAuth_user(userid);
			audit.setAuth_time(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userid);
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);

		} else {
			// System.out.println("Not in the BIPS_Password_Management_Entity");
		}

		return "Modified Successfully";
	}

	// ****************************
	// Merchant User Management
	// ****************************

	@RequestMapping(value = "FeesCharge", method = RequestMethod.POST)
	@ResponseBody
	public String FeesCharge(@RequestParam(required = false) String message_ref, Model md, HttpServletRequest rq,
			@ModelAttribute BIPS_Fees_Charges_Entity bIPS_Fees_Charges_Entity) {
		BIPS_Fees_Charges_Entity up = bIPS_Fees_Charges_Entity;
		bIPS_FeesCharges_Repo.save(up);
		return "Added Successfully";
	}

	@RequestMapping(value = "SubmitRate", method = RequestMethod.POST)
	@ResponseBody
	public String SubmitRate(@RequestParam(required = false) String srl, Model md, HttpServletRequest rq,
			@ModelAttribute BIPS_Rate_Maint_Entity bIPS_Rate_Maint_Entity) {
		BIPS_Rate_Maint_Entity up = bIPS_Rate_Maint_Entity;
		bIPS_RateMaint_Repo.save(up);
		return "Modified Successfully";
	}

	@RequestMapping(value = "ServiceRequest", method = RequestMethod.POST)
	@ResponseBody
	public String ServiceRequest(@RequestParam(required = false) String subject, Model md,
			@ModelAttribute BIPS_Service_Req_Entity bIPS_Service_Req_Entity) {
		// System.out.println("the checking only|" +
		// bIPS_Service_Req_Entity.getSubject());
		BIPS_Service_Req_Entity up = bIPS_Service_Req_Entity;
		bIPS_ServiceReq_Repo.save(up);
		return "Added Successfully";
	}

	@RequestMapping(value = "ChargeBacks", method = RequestMethod.POST)
	@ResponseBody
	public String ChargeBacks(@RequestParam(required = false) String message_id, Model md, HttpServletRequest rq,
			@ModelAttribute BIPS_Outward_Trans_Monitoring_Entity bipsEntity) {
		String msg = "";
		BIPS_Outward_Trans_Monitoring_Entity cbval = bIPS_OutwardTransMonitoring_Repo
				.getTranfees(bipsEntity.getSequence_unique_id());
		if (Objects.nonNull(cbval)) {
			cbval.setTran_date(bipsEntity.getTran_date());
			cbval.setTran_audit_number(bipsEntity.getTran_audit_number());
			cbval.setMerchant_bill_number(bipsEntity.getMerchant_bill_number());
			cbval.setBill_date(bipsEntity.getBill_date());
			cbval.setBill_amount(bipsEntity.getBill_amount());
			cbval.setTran_currency(bipsEntity.getTran_currency());
			cbval.setReversal_amount(bipsEntity.getReversal_amount());
			cbval.setReversal_date(bipsEntity.getReversal_date());
			cbval.setReversal_remarks(bipsEntity.getReversal_remarks());
			bIPS_OutwardTransMonitoring_Repo.save(cbval);
			msg = "Reverted Successfully";
		} else {
			msg = "Data Not Found";
		}
		return msg;
	}

	@RequestMapping(value = "deletetds", method = RequestMethod.POST)
	@ResponseBody
	public String deletetds(Model md, HttpServletRequest rq, @ModelAttribute String tran_id,
			@RequestParam(required = false) String uniqueid) {
		return "deleted successfully";

	}

	@RequestMapping(value = "changePasswordReq", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String changePasswordReq(@RequestParam(required = true) String oldpass,
			@RequestParam(required = true) String newpass, @RequestParam(required = true) String userid, Model md,
			HttpServletRequest rq) {

		String msg = userProfileService.changePasswords(oldpass, newpass, userid);
		return msg;
	}

	@RequestMapping(value = "changePasswordLogin", method = { RequestMethod.GET, RequestMethod.POST })
	public String changePasswordLogin(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws ParseException {
		return "ChangePasswordLogin";
	}

	@RequestMapping(value = "forgetpassword", method = { RequestMethod.GET, RequestMethod.POST })
	public String forgetpassword(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws ParseException {
		md.addAttribute("message", "succes");
		return "forgetpassword";
	}

	// Define a class-level attribute to store the OTP
	public String otpValue;

	@RequestMapping(value = "sendingmail_forget", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> sendingmail_forget(@RequestParam(required = false) String userid,
			@RequestParam(required = false) String Emailid, @RequestParam(required = false) List<String> t, Model md)
			throws SQLException, ParseException {

		String otp = generateOTP();
		otpValue = otp;
		String to = Emailid;
		String from = "barath.p@bornfire.in";
		String username = "barath.p@bornfire.in";
		String password = "MiddleEast#123";
		String host = "sg2plzcpnl491716.prod.sin2.secureserver.net";
		Passwordsendingmail.sendingmailones(from, host, to, username, password, otp);
		return ResponseEntity.status(HttpStatus.OK).body(otp);
	}

	public String getOtpValue() {
		return otpValue;
	}

	@RequestMapping(value = "sendingmail_otp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String sendingmail_otp(@RequestParam(required = false) String data,
			@RequestParam(required = false) String otp, Model md, HttpServletRequest req) throws ParseException {
		String otps = getOtpValue();
		// System.out.println(otps);
		// System.out.println(otp);
		md.addAttribute("message", "success");
		String msg = null;
		if (otps.equals(otp)) {
			msg = "OTP confirmed";
		} else {
			msg = "OTP not correct";
		}
		return msg;
	}

	public String otpsms;

	public String getsmsvalidationnmsg() {
		return otpsms;
	}

	@RequestMapping(value = "validtingsms_otp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String validtingsms_otp(@RequestParam(required = false) String data,
			@RequestParam(required = false) String otp, Model md, HttpServletRequest req) throws ParseException {
		String otps = getsmsvalidationnmsg();
		md.addAttribute("message", "success");
		String msg = null;
		if (otps.equals(otp)) {
			msg = "OTP confirmed";
		} else {
			msg = "OTP not correct";
		}
		return msg;
	}

	public String getotphere() {
		return otpValue;
	}

	@RequestMapping(value = "sendingsms_otp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String sendingsms_otp(@RequestParam(required = false) String user_id, Model md, HttpServletRequest req)
			throws ParseException, UnsupportedEncodingException {

		System.out.println("Came Inside");
		String sendOtpUrl = env.getProperty("ipsx.url") + "/api/OtpToBankUser?merchant_rep_id=" + user_id;

		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		headers.setContentType(MediaType.APPLICATION_JSON);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(sendOtpUrl, HttpMethod.GET, entity, String.class);
		String response = responseEntity.getBody();

		System.out.println("Decrypted data: " + response);

		return response;
	}

	public String generateOTP() {
		Random random = new Random();
		int otpNumber = 100000 + random.nextInt(900000);
		return String.valueOf(otpNumber);
	}

	@Autowired
	BIPS_ChargeBack_Repo bips_ChargeBack_Repo;

	@RequestMapping(value = "Merchantransaction")
	public String Merchantransaction(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String tran_audit, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "TransactionMonitoring");

		if (formmode == null || formmode.equals("list")) {
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();/* 01-JUN-2024 */
			md.addAttribute("formmode", "list");
			md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTranDevlst(currentDateRef));
		} else if (formmode.equals("viewTransaction")) {
			md.addAttribute("formmode", "viewTransaction");
			md.addAttribute("viewcharge", bIPS_OutwardTransMonitoring_Repo.getSingleRecord(tran_audit));
		}
		return "MerchantTransaction.html";
	}

	@RequestMapping(value = "refcode", method = { RequestMethod.GET, RequestMethod.POST })
	public String refcode(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String ref_id, Model md, HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "ReferenceCode");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("reflist", referenceCodeRep.getreflist());

		} else if (formmode.equals("view")) {

			md.addAttribute("formmode", "view");
			md.addAttribute("RefCodeMaster", referenceCodeRep.getrefview(ref_id));

		} else if (formmode.equals("modify")) {

			md.addAttribute("formmode", "modify");
			md.addAttribute("RefCodeMaster", referenceCodeRep.getrefview(ref_id));

		} else if (formmode.equals("edit")) {

			md.addAttribute("formmode", "edit");

		} else if (formmode.equals("add")) {

			md.addAttribute("formmode", formmode);

		} else if (formmode.equals("deleteList")) {

			md.addAttribute("formmode", "deleteList");
		} else if (formmode.equals("delete")) {

			md.addAttribute("formmode", "delete");
		}

		return "IPSReferenceCode";
	}

	@RequestMapping(value = "refcodeadd", method = RequestMethod.POST)
	@ResponseBody
	public String adminRefCodeAdd(@RequestParam("formmode") String formmode,
			@ModelAttribute ReferenceCodeEntity referenceCodeEntity, Model md, HttpServletRequest rq) {
		ReferenceCodeEntity up = referenceCodeEntity;

		up.setEntity_flg("Y");
		up.setDel_flg("N");

		referenceCodeRep.save(up);
		return "Added Successfully";
	}

	@RequestMapping(value = "/adminRefCodeMasterAdd", method = RequestMethod.POST)
	@ResponseBody
	public String adminRefCodeMasterAdd(@RequestParam("formmode") String formmode, @RequestParam("ref_id") String refId,
			@ModelAttribute ReferenceCodeEntity referenceCodeEntity, Model md, HttpServletRequest rq) {

		referenceCodeEntity.setEntity_flg("Y");
		referenceCodeEntity.setDel_flg("N");

		referenceCodeRep.save(referenceCodeEntity);

		return "Modified Successfully";
	}

	@RequestMapping(value = "/refcodedelete", method = RequestMethod.POST)
	@ResponseBody
	public String refcodedelete(@RequestParam(required = false) String refId, Model md, HttpServletRequest rq) {

		ReferenceCodeEntity up = referenceCodeRep.getrefview(refId);
		referenceCodeRep.delete(up);
		return "Deleted successfully";
	}

	@RequestMapping(value = "ChargeBackSubmit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> ChargeBackSubmit(@RequestParam(required = false) String message_id, Model md,
			HttpServletRequest req, @ModelAttribute BIPS_Charge_Back_Entity chargeBackentity) {
		String userID = (String) req.getSession().getAttribute("USERID");
		System.out.println("Inside API");

		BIPS_Charge_Back_Entity cbval = bips_ChargeBack_Repo.getTranfees(chargeBackentity.getSequence_unique_id());
		if (Objects.nonNull(cbval)) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(httpHeaders); // Wrapping headers in HttpEntity
			ResponseEntity<String> response;
			try {
				response = restTemplate
						.postForEntity(
								env.getProperty("ipsx.url") + "api/ws/revertMerchantFndTransfer?seqUniqueID="
										+ chargeBackentity.getSequence_unique_id() + "&userid=" + userID,
								entity, String.class); // Passing the entity with headers
				if (response.getStatusCode() == HttpStatus.OK) {
					return new ResponseEntity<>("Transaction Reverted Successfully", HttpStatus.OK);
				} else {
					return new ResponseEntity<>("Something went wrong at server end", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} catch (HttpClientErrorException ex) {
				if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
					return new ResponseEntity<>("Something went wrong at server end", HttpStatus.NOT_FOUND);
				} else {
					return new ResponseEntity<>("Transaction Failed", ex.getStatusCode());
				}

			} catch (HttpServerErrorException ex) {
				return new ResponseEntity<>("Something went wrong at server end", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>("Data Not Found", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "submitfirstauth", method = RequestMethod.POST)
	@ResponseBody
	public String submitfirstauth(@RequestParam(required = false) String userid,
			@RequestParam(required = false) String security_question,
			@RequestParam(required = false) String security_answer, Model md, HttpServletRequest rq) {

		String one = security_question;
		Twofactorauth authRecord = Two_factor_auth.findById(userid).orElse(null);
		if (authRecord == null) {
			return "User not found";
		}
		if (one.equals("1") && security_answer.equals(authRecord.getSecurity_answer_1())) {
			return "success";
		}
		if (one.equals("2") && security_answer.equals(authRecord.getSecurity_answer_2())) {
			return "success";
		}
		if (one.equals("3") && security_answer.equals(authRecord.getSecurity_answer_3())) {
			return "success";
		}
		if (one.equals("4") && security_answer.equals(authRecord.getSecurity_answer_4())) {
			return "success";
		}
		if (one.equals("5") && security_answer.equals(authRecord.getSecurity_answer_5())) {
			return "success";
		}
		if (one.equals("6") && security_answer.equals(authRecord.getSecurity_answer_6())) {
			return "success";
		}
		if (one.equals("7") && security_answer.equals(authRecord.getSecurity_answer_7())) {
			return "success";
		}
		if (one.equals("8") && security_answer.equals(authRecord.getSecurity_answer_8())) {
			return "success";
		}
		if (one.equals("9") && security_answer.equals(authRecord.getSecurity_answer_9())) {
			return "success";
		}
		if (one.equals("10") && security_answer.equals(authRecord.getSecurity_answer_10())) {
			return "success";
		}
		return "failure";
	}

	@RequestMapping(value = "submitdashboard", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> submitdashboard(Model md, HttpServletRequest rq, @ModelAttribute Twofactorauth up,
			@ModelAttribute UserProfile up1) {
		String USERID = (String) rq.getSession().getAttribute("USERID");
		Map<String, String> response = new HashMap<>();

		if (up.getUser_id().equals(USERID)) {
			UserProfile userProfile = userProfileRep.findById(USERID).orElse(null);
			if (userProfile != null) {
				userProfile.setAuthenticate_flg("Y");
				up.setTwo_fa_enabled("Y");
				up.setEmail(userProfile.getEmail_id());
				up.setPassword_hash(userProfile.getPassword());
				up.setPhone_number(userProfile.getMob_number());
				userProfileRep.save(userProfile);
				Two_factor_auth.save(up);
				response.put("authenticate_flg", "Y");
			} else {
				response.put("error", "User profile not found");
			}
		} else {
			response.put("authenticate_flg", "N");
		}
		return response;
	}

	@RequestMapping(value = "changePasswordwithoutoldpassword", method = RequestMethod.POST)
	@ResponseBody
	public String changePasswordwithoutoldpassword(@RequestParam("newpass") String newpass,
			@ModelAttribute UserProfile userProfile, Model md, HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.changePasswordoldpass(userProfile, newpass, userid);
		// System.out.println(msg);
		return msg;
	}

	// Need to check - (Baratha& Nithya)

	@RequestMapping(value = "UnitSubmitverify", method = RequestMethod.POST)
	@ResponseBody
	public String UnitSubmitverify(@RequestParam(required = false) String MeruserId,
			@RequestParam(required = false) String MerchantName, @RequestParam(required = false) String UnitId,
			@RequestParam(required = false) String ContactPerson1,
			@RequestParam(required = false) String ContactPerson2,
			@RequestParam(required = false) String ContactPerson3,
			@RequestParam(required = false) String ContactPerson4,
			@RequestParam(required = false) String ContactPerson5,
			@RequestParam(required = false) String ContactPerson6,
			@ModelAttribute BIPS_Unit_Mangement_Entity BIPS_Unit_Mangement_Entity, Model md, HttpServletRequest req)
			throws SQLException {
		String userID = (String) req.getSession().getAttribute("USERID");

		try {

			List<BIPS_Unit_Mangement_Entity> up2 = bIPS_UnitManagement_Repo.merctopas(UnitId);
			List<BIPS_Password_Management_Entity> up3 = new ArrayList<>();
			String password = env.getProperty("user.password");
			if (UnitId != null && ContactPerson1 != null && !ContactPerson1.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEmail_address(salsecond.getContact_person1_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setLogin_status("N");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R01");
					salarymain.setMer_representative_name(salsecond.getContact_person1_name());
					salarymain.setMobile_no(salsecond.getContact_person1_mobile());
					salarymain.setCountrycode(salsecond.getCp1_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					salarymain.setPassword_life("180");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					// Set the password_expiry_date attribute of salarymain
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					up3.add(salarymain);
				}
			}

			if (UnitId != null && ContactPerson2 != null && !ContactPerson2.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEmail_address(salsecond.getContact_person2_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setLogin_status("N");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R02");
					salarymain.setMer_representative_name(salsecond.getContact_person2_name());
					salarymain.setMobile_no(salsecond.getContact_person2_mobile());
					salarymain.setCountrycode(salsecond.getCp2_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					salarymain.setPassword_life("180");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					up3.add(salarymain);
				}
			}

			if (UnitId != null && ContactPerson3 != null && !ContactPerson3.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEmail_address(salsecond.getContact_person3_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setLogin_status("N");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R03");
					salarymain.setMer_representative_name(salsecond.getContact_person3_name());
					salarymain.setMobile_no(salsecond.getContact_person3_mobile());
					salarymain.setCountrycode(salsecond.getCp3_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					salarymain.setPassword_life("180");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());

					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salsecond.setEntry_flag("Y");
					salsecond.setVerify_user(userID);
					salsecond.setVerify_time(new Date());
					salsecond.setModify_flag("N");

					up3.add(salarymain);
				}
			}

			if (UnitId != null && ContactPerson4 != null && !ContactPerson4.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEmail_address(salsecond.getContact_person4_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setLogin_status("N");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R04");
					salarymain.setMer_representative_name(salsecond.getContact_person4_name());
					salarymain.setMobile_no(salsecond.getContact_person4_mobile());
					salarymain.setCountrycode(salsecond.getCp4_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					salarymain.setPassword_life("180");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());

					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salsecond.setEntry_flag("Y");
					salsecond.setVerify_user(userID);
					salsecond.setVerify_time(new Date());
					salsecond.setModify_flag("N");

					up3.add(salarymain);
				}
			}

			if (UnitId != null && ContactPerson5 != null && !ContactPerson5.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEmail_address(salsecond.getContact_person5_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setLogin_status("N");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R05");
					salarymain.setMer_representative_name(salsecond.getContact_person5_name());
					salarymain.setMobile_no(salsecond.getContact_person5_mobile());
					salarymain.setCountrycode(salsecond.getCp5_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					salarymain.setPassword_life("180");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());

					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salsecond.setEntry_flag("Y");
					salsecond.setVerify_user(userID);
					salsecond.setVerify_time(new Date());
					salsecond.setModify_flag("N");

					up3.add(salarymain);

				}
			}

			if (UnitId != null && ContactPerson6 != null && !ContactPerson6.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEmail_address(salsecond.getContact_person6_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setLogin_status("N");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R06");
					salarymain.setMer_representative_name(salsecond.getContact_person6_name());
					salarymain.setMobile_no(salsecond.getContact_person6_mobile());
					salarymain.setCountrycode(salsecond.getCp6_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					salarymain.setPassword_life("180");
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());

					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salsecond.setEntry_flag("Y");
					salsecond.setVerify_user(userID);
					salsecond.setVerify_time(new Date());
					salsecond.setModify_flag("N");

					up3.add(salarymain);
				}
			}

			if (up2.size() > 0 && up3.size() > 0) {
				up2.get(0).setEntry_flag("Y");
				up2.get(0).setVerify_time(new Date());
				up2.get(0).setVerify_user(userID);
				up2.get(0).setDel_flg("N");
				bIPS_UnitManagement_Repo.save(up2.get(0));
				bIPS_PasswordManagement_Repo.saveAll(up3);
				return "Unit Verified Successfully";
			} else {
				return "Unit Verified UnSuccessfully";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Error occurred while processing the request";
		}
	}

	@RequestMapping(value = "merchanttopassword", method = RequestMethod.POST)
	@ResponseBody
	public String merchanttopassword(@RequestParam(required = false) String MerLegalId,
			@RequestParam(required = false) String MerCorpName, @RequestParam(required = false) String MerchantName,
			@RequestParam(required = false) String MerRepId1, @RequestParam(required = false) String MerRepId2,
			@RequestParam(required = false) String MerRepId3, @RequestParam(required = false) String MerRepId4,
			@RequestParam(required = false) String MerRepId5, @RequestParam(required = false) String MerRepId6,
			@RequestParam(required = false) String MerRepId7, @RequestParam(required = false) String MerRepId8,
			@RequestParam(required = false) String MerRepId9, @RequestParam(required = false) String MerRepId10,
			@ModelAttribute BIPS_Password_Management_Entity BIPS_Password_Management_Entity, Model md,
			HttpServletRequest req) throws SQLException {
		String userID = (String) req.getSession().getAttribute("USERID");
		String password = env.getProperty("user.password");

		try {

			List<MerchantMasterMod> up2 = merchantMasterModRep.merctopas(MerLegalId, MerCorpName, MerchantName,
					MerRepId1);
			List<BIPS_Password_Management_Entity> up3 = new ArrayList<>();

			if (MerRepId1 != null) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r1());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers());
					salarymain.setMobile_no(salsecond.getMer_ph_no());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r1());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r1());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r1());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId2 != null && !MerRepId2.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r2());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r2());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r2());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r2());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r2());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r2());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r2());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId4 != null && !MerRepId4.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r3());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r3());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r3());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r3());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r3());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r3());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r3());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId4 != null && !MerRepId5.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r4());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r4());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r4());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r4());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r4());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r4());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r4());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId5 != null && !MerRepId5.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r5());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r5());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r5());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r5());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r5());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r5());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r5());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId6 != null && !MerRepId6.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r6());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r6());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r6());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r6());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r6());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r6());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r6());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId7 != null && !MerRepId7.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r7());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r7());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r7());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r7());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r7());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r7());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r7());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId8 != null && !MerRepId8.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r8());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r8());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r8());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r8());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r8());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r8());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r8());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId9 != null && !MerRepId9.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r9());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r9());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r9());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r9());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r9());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r9());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r9());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);

					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}
			if (MerRepId10 != null && !MerRepId10.isEmpty()) {
				for (MerchantMasterMod salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_legal_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_corp_name());
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
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers = new BigDecimal(numberOfConcurrentUsersAsString);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_concurrent_users(numberOfConcurrentUsers);
					// Assuming salarymain is an instance of the salarymain class
					String numberOfConcurrentUsersAsString1 = "1";
					// Convert the String representation to a BigDecimal
					BigDecimal numberOfConcurrentUsers1 = new BigDecimal(numberOfConcurrentUsersAsString1);
					// Set the number of concurrent users using the BigDecimal value
					salarymain.setNo_of_active_devices(numberOfConcurrentUsers1);
					salarymain.setMerchant_rep_id(salsecond.getMer_user_id_r10());
					salarymain.setMer_representative_name(salsecond.getMer_cont_pers_r10());
					salarymain.setMobile_no(salsecond.getMer_ph_no_r10());
					salarymain.setAlternate_mobile_no(salsecond.getMer_ofc_no_r10());
					salarymain.setEmail_address(salsecond.getMer_email_addr_r10());
					salarymain.setMerchant_user_id(salsecond.getMerchant_legal_id());
					salarymain.setCountrycode(salsecond.getPh_countrycode_r10());
					salarymain.setAlt_countrycode(salsecond.getOfc_countrycode_r10());

					LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					int passwordLife = Integer.parseInt(salarymain.getPassword_life());
					// Calculate the password expiry date by adding password life days to the
					// current date
					LocalDate passwordExpiryDate = currentDate.plusDays(passwordLife);
					// Convert LocalDate to Date
					Date sqlPasswordExpiryDate = java.sql.Date.valueOf(passwordExpiryDate);
					// Set the password_expiry_date attribute of salarymain
					salarymain.setPassword_expiry_date(sqlPasswordExpiryDate);
					// Check if the password is expiring (i.e., if it's 30 days from now)
					LocalDate thirtyDaysFromNow = currentDate.plusDays(180);
					if (passwordExpiryDate.isEqual(thirtyDaysFromNow)) {
						// System.out.println("Your password is expiring soon. Please change it.");
					}

					// Get the current date
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					up3.add(salarymain);
					bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			return "Successfully processed the request";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occurred while processing the request";
		}
	}

	@RequestMapping(value = "merchantunitverify", method = RequestMethod.POST)
	@ResponseBody
	public String merchantunitverify(@RequestParam(required = false) String MerUserId,
			@RequestParam(required = false) String MerchantName, @RequestParam(required = false) String UnitId,
			@RequestParam(required = false) String ContactPerson1,
			@RequestParam(required = false) String ContactPerson2,
			@RequestParam(required = false) String ContactPerson3,
			@RequestParam(required = false) String ContactPerson4,
			@RequestParam(required = false) String ContactPerson5,
			@RequestParam(required = false) String ContactPerson6,
			@ModelAttribute BIPS_Unit_Mangement_Entity BIPS_Unit_Mangement_Entity, Model md, HttpServletRequest req)
			throws SQLException {
		String userID = (String) req.getSession().getAttribute("USERID");
		String password = env.getProperty("user.password");
		try {
			List<BIPS_Unit_Mangement_Entity> up2 = bIPS_UnitManagement_Repo.merctopas(UnitId);
			List<BIPS_Password_Management_Entity> up3 = new ArrayList<>();
			if (UnitId != null && ContactPerson1 != null && !ContactPerson1.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setPassword_life("180");
					salarymain.setEmail_address(salsecond.getContact_person1_email());
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setLogin_status("N");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R01");
					salarymain.setMer_representative_name(salsecond.getContact_person1_name());
					salarymain.setMobile_no(salsecond.getContact_person1_mobile());
					salarymain.setCountrycode(salsecond.getCp1_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					// Set the password_expiry_date attribute of salarymain
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("Active");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					up3.add(salarymain);
					// bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			if (UnitId != null && ContactPerson2 != null && !ContactPerson2.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setEmail_address(salsecond.getContact_person2_email());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setLogin_status("N");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("N");
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setPassword_life("180");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R02");
					salarymain.setMer_representative_name(salsecond.getContact_person2_name());
					salarymain.setMobile_no(salsecond.getContact_person2_mobile());
					salarymain.setCountrycode(salsecond.getCp2_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					up3.add(salarymain);
					// bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			if (UnitId != null && ContactPerson3 != null && !ContactPerson3.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setEmail_address(salsecond.getContact_person3_email());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setLogin_status("N");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setPassword_life("180");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R03");
					salarymain.setMer_representative_name(salsecond.getContact_person3_name());
					salarymain.setMobile_no(salsecond.getContact_person3_mobile());
					salarymain.setCountrycode(salsecond.getCp3_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					up3.add(salarymain);
					// bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			if (UnitId != null && ContactPerson4 != null && !ContactPerson4.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setEmail_address(salsecond.getContact_person4_email());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setLogin_status("N");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("N");
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setPassword_life("180");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R04");
					salarymain.setMer_representative_name(salsecond.getContact_person4_name());
					salarymain.setMobile_no(salsecond.getContact_person4_mobile());
					salarymain.setCountrycode(salsecond.getCp4_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					up3.add(salarymain);
					// bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			if (UnitId != null && ContactPerson5 != null && !ContactPerson5.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setEmail_address(salsecond.getContact_person5_email());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setLogin_status("N");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("N");
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setPassword_life("180");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R05");
					salarymain.setMer_representative_name(salsecond.getContact_person5_name());
					salarymain.setMobile_no(salsecond.getContact_person5_mobile());
					salarymain.setCountrycode(salsecond.getCp5_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					up3.add(salarymain);
					// bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			if (UnitId != null && ContactPerson6 != null && !ContactPerson6.trim().isEmpty()) {
				for (BIPS_Unit_Mangement_Entity salsecond : up2) {
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					BIPS_Password_Management_Entity salarymain = new BIPS_Password_Management_Entity();
					LocalDate currentDate1 = LocalDate.now();
					// Calculate the account expiry date by adding 1 year to the current date
					LocalDate accountExpiryDate = currentDate1.plusYears(1);
					// Convert LocalDate to Date
					Date sqlAccountExpiryDate = java.sql.Date.valueOf(accountExpiryDate);
					// Set the account_expiry_date attribute of salarymain
					salarymain.setAccount_expiry_date(sqlAccountExpiryDate);
					salarymain.setAlternate_email_id(salsecond.getEmail_id());
					salarymain.setEmail_address(salsecond.getContact_person6_email());
					salarymain.setAlternate_mobile_no(null);
					salarymain.setDel_flag("N");
					salarymain.setEntry_time(new Date());
					salarymain.setEntry_user(userID);
					salarymain.setLogin_channel("WEB");
					salarymain.setLogin_status("N");
					salarymain.setMaker_or_checker("CHECKER");
					salarymain.setNo_of_attmp(BigDecimal.ZERO);
					salarymain.setUser_locked_flg("N");
					salarymain.setEntry_flag("N");
					salarymain.setModify_flag("N");
					salarymain.setEntry_flag("Y");
					salarymain.setModify_flag("N");
					salarymain.setPassword_life("180");
					salarymain.setUser_category("Representative");
					salarymain.setMerchant_corporate_name(salsecond.getMerchant_name());
					salarymain.setMerchant_legal_user_id(salsecond.getMerchant_user_id());
					salarymain.setMerchant_name(salsecond.getMerchant_name());
					salarymain.setMerchant_rep_id(salsecond.getMerchant_user_id() + salsecond.getUnit_id() + "R06");
					salarymain.setMer_representative_name(salsecond.getContact_person6_name());
					salarymain.setMobile_no(salsecond.getContact_person6_mobile());
					salarymain.setCountrycode(salsecond.getCp6_countrycode());
					salarymain.setModify_time(salsecond.getModify_time());
					salarymain.setModify_user(salsecond.getModify_user());
					salarymain.setNo_of_active_devices(BigDecimal.valueOf(1));
					salarymain.setNo_of_concurrent_users(BigDecimal.valueOf(1));
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(password);
					salarymain.setPassword(encryptedPassword);
					LocalDate today = LocalDate.now();
					// Add 30 days to today's date
					LocalDate expiryDate = today.plusDays(180);
					// Set the password expiry date
					salarymain.setPassword_expiry_date(java.sql.Date.valueOf(expiryDate));
					//LocalDate currentDate = LocalDate.now();
					// Parse the password_life from string to integer
					salarymain.setUnit_id(salsecond.getUnit_id());
					salarymain.setUnit_name(salsecond.getUnit_name());
					salarymain.setUnit_type(salsecond.getUnit_type());
					salarymain.setUser_disable_flag("N");
					salarymain.setPwlog_flg("UNIT");
					salarymain.setUser_status("ACTIVE");
					salarymain.setMerchant_user_id(salsecond.getMerchant_user_id());
					salarymain.setVerify_time(salsecond.getVerify_time());
					salarymain.setVerify_user(salsecond.getVerify_user());
					up3.add(salarymain);
					// bIPS_PasswordManagement_Repo.saveAll(up3);
				}
			}

			if (up2.size() > 0 && up3.size() > 0) {
				up2.get(0).setEntry_flag("Y");
				up2.get(0).setVerify_time(new Date());
				up2.get(0).setVerify_user(userID);
				up2.get(0).setDel_flg("N");
				bIPS_UnitManagement_Repo.save(up2.get(0));
				bIPS_PasswordManagement_Repo.saveAll(up3);
				return "Unit Verified Successfully";
			} else {
				return "Unit Detail can't Verified";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Error occurred while processing the request";
		}
	}

	// Bank and Branch Master

	@RequestMapping(value = "BankBranchMaster", method = { RequestMethod.GET, RequestMethod.POST })
	public String BankBranchMaster(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String solId, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "BankBranchMaster");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menuname", "Organization and Branch Details");
			md.addAttribute("menu", "MMenupage");
			md.addAttribute("formmode", "list");
			md.addAttribute("BankandBranchList",
					bankandBranchServices.BankandBranchList(PageRequest.of(currentPage, pageSize)));
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", "add");
			md.addAttribute("menuname1", "Organization and Branch Details-Add");
			md.addAttribute("BankandBranch", new BankAndBranchBean());
		} else if (formmode.equals("edit")) {
			md.addAttribute("formmode", "edit");
			md.addAttribute("menuname1", "Organization and Branch Details-Modify");
			md.addAttribute("BankandBranch", bankandBranchServices.getSolID(solId));
		} else if (formmode.equals("view")) {
			md.addAttribute("formmode", "view");
			md.addAttribute("menuname1", "Organization and Branch Details-Inquiry");
			md.addAttribute("BankandBranch", bankandBranchServices.getSolID(solId));
		} else if (formmode.equals("viewnew")) {
			md.addAttribute("formmode", "viewnew");
			md.addAttribute("menuname1", "Organization and Branch Details-Inquiry");
			md.addAttribute("BankandBranch", bankandBranchServices.getModSolID(solId));
		} else if (formmode.equals("verify")) {
			md.addAttribute("formmode", "verify");
			md.addAttribute("menuname1", "Organization and Branch Details-Verify");
			md.addAttribute("BankandBranch", bankandBranchServices.getModSolID(solId));
		} else if (formmode.equals("cancel")) {
			md.addAttribute("formmode", "cancel");
			md.addAttribute("menuname1", "Organization and Branch Details-Cancel");
			md.addAttribute("BankandBranch", bankandBranchServices.getModSolID(solId));
		}
		md.addAttribute("adminflag", "adminflag");
		return "BankAndBranchMaster";
	}

	@RequestMapping(value = "ModBankBranchMaster", method = RequestMethod.POST)
	@ResponseBody
	public String ModBankBranchMaster(@RequestParam("formmode") String formmode,
			@ModelAttribute BankAndBranchBean bamlSolEntity, Model md, HttpServletRequest rq) {
		String userID = (String) rq.getSession().getAttribute("USERID");
		String msg = bankandBranchServices.modDetails(bamlSolEntity, formmode, userID);
		md.addAttribute("adminflag", "adminflag");
		return msg;
	}

	@Autowired
	private SettlementAccountRepository settlementAccountRepository;

	@RequestMapping(value = "SettlementAccount")
	public String SettlementAccount(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String acctcode, @RequestParam(required = false) String userid,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req)
			throws FileNotFoundException, SQLException, IOException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "SettlementAccount");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("menu", "MMenupage");
			md.addAttribute("SettleAccount", settlementAccountRepository.findAllCustom());
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", formmode);
		} else if (formmode.equals("addsubmit")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("menu", "MMenupage");
			String msg = "ok";
			md.addAttribute("AddSettle", msg);
			return msg;
		} else if (formmode.equals("edit")) {
			md.addAttribute("menuname1", "Settlement Account- Modify");
			md.addAttribute("formmode", formmode);
			md.addAttribute("SettlementAccount", settlementAccountRepository.findByAccountCode(acctcode));
		} else if (formmode.equals("verify")) {
			md.addAttribute("menuname1", "Settlement Account- Verify");
			md.addAttribute("formmode", formmode);
			md.addAttribute("SettlementAccount", settlementAccountRepository.findByAccountCode(acctcode));
		} else if (formmode.equals("view")) {
			md.addAttribute("menuname1", "Settlement Account- Inquiry");
			md.addAttribute("formmode", formmode);
			md.addAttribute("SettlementAccount", settlementAccountRepository.findByAccountCode(acctcode));
		} else if (formmode.equals("delete")) {
			md.addAttribute("menuname1", "Settlement Account- Delete");
			md.addAttribute("formmode", formmode);
			md.addAttribute("SettlementAccount", settlementAccountRepository.findByAccountCode(acctcode));
		}
		md.addAttribute("auditflag", "auditflag");
		return "SettlementAccount";
	}

	/* Reports Screen */
	@RequestMapping(value = "transactionReports")
	public String transactionReports(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Transaction Reports");
		md.addAttribute("PdfViewer", "TransactionReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html";
	}

	@RequestMapping(value = "transactionRecordsView", method = { RequestMethod.GET, RequestMethod.POST })
	public String transactionRecordsView(@RequestParam(required = false) String merchantId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
			@RequestParam(required = false) String unitId, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String deviceId, Model md, HttpServletRequest req) throws ParseException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");

		String Fromdate = dateFormat1.format(fromDate);
		String Todate = dateFormat1.format(toDate);

		String FromdateView = dateFormat.format(fromDate);
		String TodateView = dateFormat.format(toDate);

		// System.out.println(date + date1);
		md.addAttribute("menuname", "Transaction Records");
		md.addAttribute("auditflag", "auditflag");
		md.addAttribute("formmode", "list");

		String UserId = userId.equals("") ? null : userId;
		String UnitId = unitId.equals("") ? null : unitId;
		String DeviceId = deviceId.equals("") ? null : deviceId;

		// md.addAttribute("AuditList",
		// monitorService.getAuditInquries(dateFormat.parse(date)));

		if (Objects.nonNull(UserId) && Objects.nonNull(UnitId) && Objects.nonNull(DeviceId)) {
			System.out.println("All additional details");
			md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsAll(Fromdate, Todate,
					merchantId, UnitId, UserId, DeviceId));

		} else if (Objects.nonNull(UserId) && Objects.nonNull(UnitId)) {
			System.out.println("Two additional details");
			md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsAllUU(Fromdate, Todate,
					merchantId, UnitId, UserId));

		} else if (Objects.nonNull(UserId) && Objects.nonNull(DeviceId)) {
			System.out.println("Two additional details");
			md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsAllUsD(Fromdate, Todate,
					merchantId, UserId, DeviceId));

		} else if (Objects.nonNull(UnitId) && Objects.nonNull(DeviceId)) {
			System.out.println("Two additional details");
			md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsAllUD(Fromdate, Todate,
					merchantId, UnitId, DeviceId));

		} else if (Objects.nonNull(UnitId) || Objects.nonNull(UserId) || Objects.nonNull(DeviceId)) {
			System.out.println("One additional details");
			if (Objects.nonNull(UnitId)) {
				md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsUnit(Fromdate, Todate,
						merchantId, UnitId));

			} else if (Objects.nonNull(UserId)) {
				md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsUser(Fromdate, Todate,
						merchantId, UserId));

			} else {
				md.addAttribute("click", bIPS_OutwardTransMonitoring_Repo.getTransactionDetailsDevice(Fromdate, Todate,
						merchantId, DeviceId));

			}
		} else {
			 System.out.println("No additional details");
			
			md.addAttribute("click",
					bIPS_OutwardTransMonitoring_Repo.getTransactionDetails(Fromdate, Todate, merchantId));
		}

		md.addAttribute("Fromdate", FromdateView);
		md.addAttribute("Todate", TodateView);
		md.addAttribute("MerchantId", merchantId);
		md.addAttribute("UnitId", UnitId);
		md.addAttribute("UserId", UserId);
		md.addAttribute("DeviceId", DeviceId);

		return "TransactionRecordsView";
	}

	@RequestMapping(value = "chargeBackReports")
	public String chargeBackReports(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "ChargeBack Reports");
		md.addAttribute("PdfViewer", "ChargeBackReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "ChargeBackReports.html";
	}

	@RequestMapping(value = "chargeBackReportsView", method = { RequestMethod.GET, RequestMethod.POST })
	public String chargeBackReportsView(@RequestParam(required = false) String merchantId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
			@RequestParam(required = false) String unitId, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String deviceId, Model md, HttpServletRequest req) throws ParseException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");

		String Fromdate = dateFormat1.format(fromDate);
		String Todate = dateFormat1.format(toDate);

		String FromdateView = dateFormat.format(fromDate);
		String TodateView = dateFormat.format(toDate);

		// System.out.println(date + date1);
		md.addAttribute("menuname", "Transaction Records");
		md.addAttribute("auditflag", "auditflag");
		md.addAttribute("formmode", "list");

		String UserId = userId.equals("") ? null : userId;
		String UnitId = unitId.equals("") ? null : unitId;
		String DeviceId = deviceId.equals("") ? null : deviceId;

		md.addAttribute("Back",
				bips_ChargeBack_Repo.getTotalListBetween(Fromdate, Todate, merchantId, UnitId, UserId, DeviceId));

		md.addAttribute("MerchantId", merchantId);
		md.addAttribute("UnitId", UnitId);
		md.addAttribute("UserId", UserId);
		md.addAttribute("DeviceId", DeviceId);
		md.addAttribute("Fromdate", FromdateView);
		md.addAttribute("Todate", TodateView);

		return "ChargeBackReportsView";
	}

	@RequestMapping(value = "chargeBackRecord")
	public String chargeBackRecord(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			//Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
			md.addAttribute("formmode", "list");
			md.addAttribute("currentDate", valueDate);
			md.addAttribute("Back", bips_ChargeBack_Repo.getAllList(currentDateRef));
		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			BIPS_Charge_Back_Entity chargeData = bips_ChargeBack_Repo.getTranfees(sql_unic_id);
			if (!chargeData.getReversal_remarks().equals("REVERTED")) {
				chargeData.setReversal_date(new Date());
			}
			md.addAttribute("viewcharge", chargeData);
		}
		return "IPSChargeback.html";
	}

	@RequestMapping(value = "RevertedChargeBackTransaction")
	public String RevertedChargeBackTransaction(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req, @RequestParam(required = false) String sql_unic_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			//Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
			md.addAttribute("formmode", "Revert");
			md.addAttribute("currentDate", valueDate);
			md.addAttribute("Back", bips_ChargeBack_Repo.getRevertedTransactionList(currentDateRef));
		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			md.addAttribute("viewcharge", bips_ChargeBack_Repo.getTranfees(sql_unic_id));
		}
		return "IPSChargeback.html";
	}

	@RequestMapping(value = "pendingChargeBackTransaction")
	public String pendingChargeBackTransaction(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req, @RequestParam(required = false) String sql_unic_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "ChargeBackTransaction");

		if (formmode == null || formmode.equals("Pending")) {

			if (Objects.nonNull(valueDate)) {

				SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");

				String currentDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();/* 01-JUN-2024 */
				md.addAttribute("formmode", "Pending");
				md.addAttribute("currentDate", valueDate);
				md.addAttribute("Back", bips_ChargeBack_Repo.getPendingTransactionList(currentDateRef));

			} else {
				SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
				Date currentDate = new Date();
				String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();/* 01-JUN-2024 */
				md.addAttribute("formmode", "Pending");
				md.addAttribute("currentDate", currentDate);
				md.addAttribute("Back", bips_ChargeBack_Repo.getPendingTransactionList(currentDateRef));
			}

		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			md.addAttribute("viewcharge", bips_ChargeBack_Repo.getTranfees(sql_unic_id));
		}
		return "IPSChargeback.html";
	}

	@RequestMapping(value = "PendingServlist")
	public String ServiceRequest(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String request_id, String sql_unic_id) throws SQLException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "ServiceRequest");

		if (formmode == null || formmode.equals("Pendinglist")) {
			md.addAttribute("formmode", "Pendinglist");
			md.addAttribute("Service", bIPS_Service_ReqMonitoring_Repo.getPendingList());
		}
		return "ServiceRequest.html";
	}

	@RequestMapping(value = "ApprovalServlist")
	public String RevertServlist(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id, @RequestParam(required = false) String request_id)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("Approvallist")) {
			md.addAttribute("formmode", "Approvallist");
			md.addAttribute("Service", bIPS_Service_ReqMonitoring_Repo.getApprovedList());
		}
		return "ServiceRequest.html";
	}

	@RequestMapping(value = "ViewService")
	public String ViewService(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String request_id) throws SQLException {
		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		if (formmode == null || formmode.equals("ServReq")) {
			md.addAttribute("formmode", "ServReq");
			// md.addAttribute("viewreq",
			// bIPS_Service_ReqMonitoring_Repo.getReqId(request_id));

			BIPS_Service_ReqMonitoring_Entity chargeData = bIPS_Service_ReqMonitoring_Repo.getReqId(request_id);
			if (!chargeData.getStatus().equals("PENDING")) {
				chargeData.setApproved_date(new Date());
				chargeData.setApproved_by(userID);
			}
			md.addAttribute("viewreq", chargeData);
		}
		return "ServiceRequest.html";
	}

	@RequestMapping(value = "ServiceApprove", method = RequestMethod.POST)
	@ResponseBody
	public String ServiceApprove(Model md, HttpServletRequest rq, HttpServletRequest req,
			@ModelAttribute BIPS_Service_ReqMonitoring_Entity bIPS_Service_ReqMonitoring_Entity) {
		String userID = (String) req.getSession().getAttribute("USERID");

		BIPS_Service_ReqMonitoring_Entity exist = bIPS_Service_ReqMonitoring_Repo
				.getReqId(bIPS_Service_ReqMonitoring_Entity.getRequest_id());
		BIPS_Service_ReqMonitoring_Entity up = bIPS_Service_ReqMonitoring_Entity;
		up.setApproved_by(userID);
		up.setApproved_date(new Date());
		up.setStatus("APPROVED");
		up.setRequest_date(exist.getRequest_date());
		up.setEntry_user(exist.getEntry_user());
		up.setEntry_date(exist.getEntry_date());
		up.setEntry_flag(exist.getEntry_flag());
		up.setDel_flag(exist.getDel_flag());
		up.setUnit_id(exist.getUnit_id());
		up.setUser_id(exist.getUser_id());
		up.setCountrycode(exist.getCountrycode());

		bIPS_Service_ReqMonitoring_Repo.save(up);
		return "Service Request Approved";

	}

	@RequestMapping(value = "paymentprocessing")
	public String paymentprocessing(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
	        throws SQLException {
	    String roleId = (String) req.getSession().getAttribute("ROLEID");
	    md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
	    md.addAttribute("menuname", "IPS Outgoing Report");
	    md.addAttribute("PdfViewer", "IpsOutgoingReport");

	    if (formmode == null || formmode.equals("list")) {
	        List<Object[]> merchantIdNameList = merchantMasterRep.getMerchantIdAndNameList();
	        md.addAttribute("merchantIdNameList", merchantIdNameList);
	    }

	    return "paymentprocessing.html";  // Using TransactionReports.html for now
	}
	
	@RequestMapping(value = "/getimages", method = { RequestMethod.GET })
	@ResponseBody
	public String getimagesss(@RequestParam(required = false) String appl_ref_no) throws SQLException {

		String msg = null;
		String lastChars = null;
		System.out.println(appl_ref_no);
		try {
			System.out.println("inside");
			List<Sign_Master_Entity> vv = Sign_Master_Repo.findByref_no(appl_ref_no);

			InputStream ll = vv.get(0).getSign().getBinaryStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(ll));
			msg = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lastChars = msg.substring(22, msg.length());

		return lastChars;

	}
	
	
	@RequestMapping(value = "MerOnbordParam")
	public String MerOnbordParam(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String acctcode, @RequestParam(required = false) String userid,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			 Model md, HttpServletRequest req)
			throws FileNotFoundException, SQLException, IOException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PdfViewer", "SettlementAccount");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("menu", "MMenupage");
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", formmode);
		}else if (formmode.equals("CheckDigit")) {
			md.addAttribute("formmode", formmode);
		}
		return "MerOnboardParam";
	}
	

	@RequestMapping(value = "MerchantInquiry")
	public String MerchantInquiry(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String MerId, @RequestParam(required = false) String UnitId)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		System.out.println(MerId);
		if (formmode == null || formmode.equals("view")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("branchDet", merchantMasterModRep.findByIdCustom(MerId));
			md.addAttribute("merchant_acct_no", MerId);
			md.addAttribute("DocumentList", documentMaster_Repo.findByMer(MerId));
			md.addAttribute("MerUnit", bIPS_UnitManagement_Repo.getUnitId(UnitId));
			md.addAttribute("merchantFeeDetailses", merchantFeesServiceRepo.merchantDetails(MerId));
			md.addAttribute("bankAgentName", bankAgentTableRep.findByCustomBankName());
			md.addAttribute("merchantFeeDetails", merchantFeesServiceRepo.merchantDetailsFromMod(MerId));
			md.addAttribute("SignId", Sign_Master_Repo.getMerId(MerId));
			// UNIT USER PASSWORD
			md.addAttribute("MerchantPass", bIPS_PasswordManagement_Repo.getPassList(MerId));
			md.addAttribute("MerchantIdUse", MerId);
			// md.addAttribute("MerchantNaUse", merchant_nam);
			md.addAttribute("MerchantUnit", bIPS_UnitManagement_Repo.getUnitlist(MerId));
			md.addAttribute("pro", bIPS_MerUserManagement_Repo.getUserManage1(MerId));
			md.addAttribute("MerchantDevi", bIPS_MerDeviceManagement_Repo.getaddDevice(MerId));
			md.addAttribute("propass", bIPS_PasswordManagement_Repo.getPassmer(MerId));
		}
		return "MerchantInquiry.html"; // Using TransactionReports.html for now
	}
	

	@PostMapping("/imageupload11")
	@ResponseBody

	public String uploadimage(@RequestParam("files") List<MultipartFile> file,
			@RequestParam("unique") List<String> unique_id,
			@RequestParam(value = "appl_ref_no", required = false) String appl_ref_no,
			@RequestParam("dataURL") List<String> dataURL) throws IOException, SerialException, SQLException {
		String msg = null;

		try {

			for (int i = 0; i < file.size(); i++) {

				byte[] fileBytes = file.get(i).getBytes();
				DocumentMaster_Entity back = documentMaster_Repo.findByApplAndUnquieimg(appl_ref_no, unique_id.get(i));

				// Check if it's an image (use dataURL), otherwfindBy1ise store the raw
				// fileBytes
				if (!file.get(i).getContentType().startsWith("image/")) {
					// For non-image files (PDF, Excel, etc.), store the raw file bytes
					back.setUpd_file(fileBytes);
					back.setDel_flg("N");
				} else {
					// For images, use the dataURL if present
					String docString = dataURL.get(i);
					byte[] document = docString.getBytes(); // Handle dataURL for images
					back.setUpd_file(document);
					back.setDel_flg("N");
				}
				
				

				documentMaster_Repo.save(back);

			}
			msg = " File Uploaded Succesfully ";
			// System.out.println(" file Size " + file.size());
		} catch (Exception e) {
			System.out.println("Exception >>>>>>>>>>>>>>>>>" + e);
			msg = " File Upload Unsuccesfull ";
		}
		return msg;
	}
	
	@RequestMapping(value = "/getproof1", method = { RequestMethod.GET })
	@ResponseBody
	public String getproof1(@RequestParam(required = false) String appl_ref_no,
			@RequestParam(required = false) String doc_type, String md) throws SQLException, ParseException {
		System.out.println("EXISTING ");
		System.out.println("MER ID " + appl_ref_no);
		System.out.println("DOC TYPE " + doc_type);
		String msg = null;
		String lastChars = null;

		try {
			System.out.println("inside");
			System.out.println("inside");
			System.out.println("inside");
			List<DocumentMaster_Entity> vv = documentMaster_Repo.findBy1(appl_ref_no, doc_type);
			// System.out.println("The encryptedddddd" +
			// vv.get(0).getUpd_file().toString());

			byte[] ll = vv.get(0).getUpd_file();
			String str = new String(ll, StandardCharsets.UTF_8);
			lastChars = str.substring(str.indexOf(",") + 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("lastChars" + lastChars);

		return lastChars;

	}

	@PostMapping("/multiline")
	@ResponseBody
	public List<String> upLoadForm1(@RequestBody List<DynamicFromValue> dynamicValues,
			@RequestParam(value = "appl_ref_no", required = false) String appl_ref_no) {

		System.err.println(" size " + dynamicValues.size());

		List<DocumentMaster_Entity> list_of_bacp_record = new ArrayList<>();
		for (DynamicFromValue dynamic : dynamicValues) {
			System.err.println(" size " + dynamicValues.size());
			DocumentMaster_Entity bdcm = new DocumentMaster_Entity();
			bdcm.setFile_name(dynamic.getFilename());
			bdcm.setDocument_type(dynamic.getDoctype());
			bdcm.setDocument_code(dynamic.getDoccode());
			bdcm.setDocument_type_desc(dynamic.getDoctypesesc());
			bdcm.setUnique_id(dynamic.getUniqueid());
			bdcm.setPlace_of_issue(dynamic.getPlaceofissue());
			bdcm.setIssue_date(dynamic.getIssuedate());
			bdcm.setExpiry_date(dynamic.getExprydate());
			bdcm.setMerchant_id(appl_ref_no);
			bdcm.setDel_flg("N");
			documentMaster_Repo.save(bdcm);
			list_of_bacp_record.add(bdcm);
		}
		try {
			documentMaster_Repo.saveAll(list_of_bacp_record);
			List<String> uniquelist = list_of_bacp_record.stream().map(e -> e.getUnique_id())
					.collect(Collectors.toList());
			return uniquelist;
		} catch (Exception e) {
			return new ArrayList();
		}

	}
	
	@RequestMapping(value = "holdsubmit", method = RequestMethod.POST)
	@ResponseBody
	public String holdsubmit(Model md, HttpServletRequest req, @RequestParam(required = false) String merchant_id,
			@RequestParam(required = false) String hold_remarks, @RequestParam(required = false) String formmode,
			@ModelAttribute MerchantMasterMod MerchantMasterMod, @RequestParam(required = false) String msg) {

		String userID = (String) req.getSession().getAttribute("USERID");

		System.out.println(merchant_id);
		System.out.println(hold_remarks);
		MerchantMasterMod up = merchantMasterModRep.findByIdCustom(merchant_id);

		up.setHr_hold_date(new Date());
		up.setHr_status("HOLD");
		up.setHr_holdreject_flg('Y');
		up.setHr_hold_remarks(hold_remarks);
		up.setHr_hold_user(userID);
		merchantMasterModRep.save(up);

		return "Merchant Moved to Hold List";
	}

	@RequestMapping(value = "rejectsubmit", method = RequestMethod.POST)
	@ResponseBody
	public String rejectsubmit(Model md, HttpServletRequest req, @RequestParam(required = false) String merchant_id,
			@RequestParam(required = false) String formmode, @ModelAttribute MerchantMasterMod MerchantMasterMod,
			@RequestParam(required = false) String reject_remarks, @RequestParam(required = false) String msg) {

		String userID = (String) req.getSession().getAttribute("USERID");

		MerchantMasterMod up = merchantMasterModRep.findByIdCustom(merchant_id);
		up.setHr_status("REJECT");
		up.setHr_reject_date(new Date());
		up.setHr_holdreject_flg('Y');
		up.setHr_reject_remarks(reject_remarks);
		up.setHr_reject_user(userID);
		merchantMasterModRep.save(up);

		return "Merchant Moved to Reject List";
	}

	@RequestMapping(value = "proceedfunc", method = RequestMethod.POST)
	@ResponseBody
	public String proceedfunc(Model md, HttpServletRequest req, @RequestParam(required = false) String merchant_id,
			@RequestParam(required = false) String formmode, @ModelAttribute MerchantMasterMod MerchantMasterMod,
			@RequestParam(required = false) String reject_remarks, @RequestParam(required = false) String msg) {

		String userID = (String) req.getSession().getAttribute("USERID");

		MerchantMasterMod up = merchantMasterModRep.findByIdCustom(merchant_id);
		up.setHr_holdreject_flg('N');
		merchantMasterModRep.save(up);

		return "Merchant Moved to Merchant List";
	}
 
	@RequestMapping(value = "mauCasTransactions")
	public String mauCasTransactions(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "MAU CAS Transactions");
		md.addAttribute("PdfViewer", "MAUCasTransactionReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "outgoingFileReversal")
	public String outgoingFileReversal(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Outgoing File Reversal");
		md.addAttribute("PdfViewer", "OutgoingFileReversalReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "incomingFilesProcessing")
	public String incomingFilesProcessing(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Incoming Files Processing");
		md.addAttribute("PdfViewer", "IncomingFilesProcessingReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "rejectionPending")
	public String rejectionPending(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Rejection Pending");
		md.addAttribute("PdfViewer", "RejectionPendingReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "reportsRequirement")
	public String reportsRequirement(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Reports Requirement");
		md.addAttribute("PdfViewer", "ReportsRequirementReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "schemaWiseTransaction")
	public String schemaWiseTransaction(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Schema Wise Transaction");
		md.addAttribute("PdfViewer", "SchemaWiseTransactionReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "rejectionReport")
	public String rejectionReport(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Rejection Report");
		md.addAttribute("PdfViewer", "RejectionReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "ipsOutgoingReport")
	public String ipsOutgoingReport(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req)
			throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "IPS Outgoing Report");
		md.addAttribute("PdfViewer", "IpsOutgoingReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "mauCasOutgoingReport")
	public String mauCasOutgoingReport(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "MAU CAS Outgoing Report");
		md.addAttribute("PdfViewer", "MAUCasOutgoingReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "schemesUnsettledTransaction")
	public String schemesUnsettledTransaction(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Schemes Unsettled Transaction");
		md.addAttribute("PdfViewer", "SchemesUnsettledTransactionReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@RequestMapping(value = "schemesSettledTransaction")
	public String schemesSettledTransaction(@RequestParam(required = false) String formmode, Model md,
			HttpServletRequest req) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("menuname", "Schemes Settled Transaction");
		md.addAttribute("PdfViewer", "SchemesSettledTransactionReport");

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchantIds", merchantMasterRep.getMerchantIds());
		}
		return "TransactionReports.html"; // Using TransactionReports.html for now
	}

	@PostMapping(value = "Merchantuploadexcel")
	@ResponseBody
	public String leaseuploadexcel(@RequestParam("file") MultipartFile file, String screenId,
			@ModelAttribute com.bornfire.entity.MerchantMasterMod MerchantMasterMod, Model md, HttpServletRequest rq)
			throws FileNotFoundException, SQLException, IOException, NullPointerException {

		System.out.println("the testing   GST EXCEL UPLOAD");

		System.out.println("fileSize" + file.getSize());

		if (file.getSize() < 50000000) {
			String userid = (String) rq.getSession().getAttribute("USERID");
			String msg = bankAndBranchMasterServices.UploadgstserviceCOLLECTION(screenId, file, userid,
					MerchantMasterMod);
			return msg;
		} else {
			return "File has not been successfully uploaded. Requires less than 128 KB size.";
		}
	}
	
	@PostMapping(value = "Unituploadexcel")
	@ResponseBody
	public String Unituploadexcel(@RequestParam("file") MultipartFile file, String screenId, 
			 String merchant_id, String merchant_name,
			@ModelAttribute com.bornfire.entity.MerchantMasterMod MerchantMasterMod, Model md, HttpServletRequest rq)
			throws FileNotFoundException, SQLException, IOException, NullPointerException {

		System.out.println("the testing   GST EXCEL UPLOAD");

		System.out.println("fileSize" + file.getSize());
		System.out.println("MerIds " + merchant_id);
		System.out.println("MerNames " + merchant_name);

		if (file.getSize() < 50000000) {
			String userid = (String) rq.getSession().getAttribute("USERID");
			String msg = bankAndBranchMasterServices.Uploadunit(screenId, file, userid, merchant_id,merchant_name,MerchantMasterMod);
			return msg;
		} else {
			return "File has not been successfully uploaded. Requires less than 128 KB size.";
		}
	}
	
	@PostMapping(value = "Useruploadexcel")
	@ResponseBody
	public String Useruploadexcel(@RequestParam("file") MultipartFile file, String screenId,
			 String merchant_id, String merchant_name,
			@ModelAttribute com.bornfire.entity.MerchantMasterMod MerchantMasterMod, Model md, HttpServletRequest rq)
			throws FileNotFoundException, SQLException, IOException, NullPointerException {

		System.out.println("the testing   GST EXCEL UPLOAD"); 
		System.out.println("fileSize" + file.getSize());
		System.out.println("MerIduser " + merchant_id);
		System.out.println("MerNameuser " + merchant_name);

	if (file.getSize() < 50000000) {
			String userid = (String) rq.getSession().getAttribute("USERID");
			String msg = bankAndBranchMasterServices.Uploaduser(screenId, file, userid, merchant_id,merchant_name,
					MerchantMasterMod);
			return msg;
		} else {
			return "File has not been successfully uploaded. Requires less than 128 KB size.";
		}
		
	}
	
	@PostMapping(value = "Deviceuploadexcel")
	@ResponseBody
	public String Deviceuploadexcel(@RequestParam("file") MultipartFile file, String screenId,
			 String merchant_id, String merchant_name,
			@ModelAttribute com.bornfire.entity.MerchantMasterMod MerchantMasterMod, Model md, HttpServletRequest rq)
			throws FileNotFoundException, SQLException, IOException, NullPointerException {

		System.out.println("the testing   GST EXCEL UPLOAD"); 
		System.out.println("fileSize" + file.getSize());
		System.out.println("MerIddev " + merchant_id);
		System.out.println("MerNamedev " + merchant_name);

		if (file.getSize() < 50000000) {
			String userid = (String) rq.getSession().getAttribute("USERID");
			String msg = bankAndBranchMasterServices.Uploaddevice(screenId, file, userid, merchant_id,merchant_name,
					MerchantMasterMod);
			return msg;
		} else {
			return "File has not been successfully uploaded. Requires less than 128 KB size.";
		}
	}
	
	@RequestMapping(value = "DocRemove", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String DocRemove(@RequestParam(required = false) String unique_no) {

		DocumentMaster_Entity uniqueid = documentMaster_Repo.findByUnique1(unique_no);
		uniqueid.setDel_flg("Y");
		documentMaster_Repo.save(uniqueid);
		return "Deleted Successfully.....";
	}
}
