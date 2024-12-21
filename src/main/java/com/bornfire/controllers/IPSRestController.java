package com.bornfire.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bornfire.configuration.ErrorResponseCod;
import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.AccessandRolesRepository;
import com.bornfire.entity.AccountContactResponse;
import com.bornfire.entity.BIPS_Black_Repo;
import com.bornfire.entity.BIPS_ChargeBack_Repo;
import com.bornfire.entity.BIPS_Charge_Back_Entity;
import com.bornfire.entity.BIPS_CheckListEntity;
import com.bornfire.entity.BIPS_CheckList_Repo;
import com.bornfire.entity.BIPS_Dedup_Repo;
import com.bornfire.entity.BIPS_MerDeviceManagement_Repo;
import com.bornfire.entity.BIPS_MerUserManagement_Repo;
import com.bornfire.entity.BIPS_Mer_Device_Management_Entity;
import com.bornfire.entity.BIPS_Mer_User_Management_Entity;
import com.bornfire.entity.BIPS_Negative_Repo;
import com.bornfire.entity.BIPS_OutwardTransMonitoring_Repo;
import com.bornfire.entity.BIPS_Outward_Trans_Monitoring_Entity;
import com.bornfire.entity.BIPS_PasswordManagement_Repo;
import com.bornfire.entity.BIPS_Password_Management_Entity;
import com.bornfire.entity.BIPS_Pep_Repo;
import com.bornfire.entity.BIPS_UnitManagement_Repo;
import com.bornfire.entity.BIPS_Unit_Mangement_Entity;
import com.bornfire.entity.BankAgentTable;
import com.bornfire.entity.BankAgentTableRep;
import com.bornfire.entity.DocumentMaster_Entity;
import com.bornfire.entity.DocumentMaster_Repo;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;
import com.bornfire.entity.IPSChargesAndFees;
import com.bornfire.entity.MerchantChargesAndFees;
import com.bornfire.entity.MerchantChargesAndFeesRepositry;
import com.bornfire.entity.MerchantChargesandFeesMod;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.MerchantMasterMod;
import com.bornfire.entity.MerchantMasterModRep;
import com.bornfire.entity.MerchantMasterRep;
import com.bornfire.entity.SettlementAccount;
import com.bornfire.entity.Sign_Master_Entity;
import com.bornfire.entity.Sign_Master_Repo;
import com.bornfire.entity.UserProfile;
import com.bornfire.entity.UserProfileRep;
import com.bornfire.exception.Connect24Exception;
import com.bornfire.exception.ErrorResponseCode;
import com.bornfire.exception.ServerErrorException;
import com.bornfire.services.BIPSBankandBranchServices;
import com.bornfire.services.BankAndBranchMasterServices;
import com.bornfire.services.Connect24Service;
import com.bornfire.services.ExcelPdfDownloadService;
import com.bornfire.services.IPSAccessRoleService;
import com.bornfire.services.LoginSecurityServices;
import com.bornfire.services.MandateServices;
import com.bornfire.services.SettlementAccountServices;

import net.sf.jasperreports.engine.JRException;

@RestController
public class IPSRestController {

	private static final Logger logger = LoggerFactory.getLogger(IPSRestController.class);

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	@Autowired
	SettlementAccountServices settlementAccountServices;

	@Autowired
	BankAndBranchMasterServices bankAndBranchMasterServices;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	IPSAccessRoleService AccessRoleService;

	@Autowired
	LoginSecurityServices loginSecurityServices;

	@Autowired
	MandateServices mandateServices;

	@Autowired
	AccessandRolesRepository accessandRolesRepository;

	@Autowired
	Environment env;

	@Autowired
	BankAgentTableRep bankAgentTableRep;

	@Autowired
	MerchantChargesAndFeesRepositry merchantChargesAndFeesRepositry;

	@Autowired
	BIPSBankandBranchServices bankandBranchServices;

	@Autowired
	ExcelPdfDownloadService excelPdfDownloadService;

	@Autowired
	MandateServices MandateService;

	@Autowired
	MerchantMasterRep merchantMasterRep;

	@Autowired
	BIPS_OutwardTransMonitoring_Repo BIPS_OutwardTransMonitoring_Rep;

	@Autowired
	Connect24Service connect24Service;

	@Autowired
	ErrorResponseCod errorResponseCod;

	@Autowired
	BIPS_ChargeBack_Repo BIPS_ChargeBack_Repo;

	@Autowired
	IPSAuditTableRep IPSAuditTableRep;

	@Autowired
	BIPS_UnitManagement_Repo bIPS_UnitManagement_Repo;

	@Autowired
	BIPS_OutwardTransMonitoring_Repo bIPS_OutwardTransMonitoring_Repo;

	@Autowired
	BIPS_ChargeBack_Repo bips_ChargeBack_Repo;

	@Autowired
	BIPS_MerUserManagement_Repo bIPS_MerUserManagement_Repo;

	@Autowired
	BIPS_MerDeviceManagement_Repo bIPS_MerDeviceManagement_Repo;
	
	@Autowired
	BIPS_Dedup_Repo dedup_Repo;

	@Autowired
	BIPS_Black_Repo black_Repo;

	@Autowired
	BIPS_Negative_Repo Negative_Repo;

	@Autowired
	BIPS_Pep_Repo Pep_Repo;

	@Autowired
	BIPS_CheckList_Repo bIPS_CheckList_Repo;
	
	@Autowired
	Sign_Master_Repo Sign_Master_Repo;
	
	@Autowired
	DocumentMaster_Repo documentMaster_Repo;


	@RequestMapping(value = "TransactionDownload", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource TransactionDownload(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {
		response.setContentType("application/octet-stream");
		InputStreamResource resource = null;
		try {
			File repfile = excelPdfDownloadService.getFile(from_date, to_date, filetype);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "ChargebackDownload", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource ChargebackDownload(HttpServletResponse response,
			@RequestParam(value = "filetype", required = false) String filetype,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "formmode", required = false) String formmode) throws IOException, SQLException {
		response.setContentType("application/octet-stream");
		InputStreamResource resource = null;
		try {
			File repfile = excelPdfDownloadService.getChargebackFile(from_date, to_date, filetype, formmode);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "AuditDownload", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource AuditDownload(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {
		response.setContentType("application/octet-stream");
		InputStreamResource resource = null;
		try {
			File repfile = excelPdfDownloadService.getAuditFile(from_date, to_date, filetype);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "AuditOperationDownload", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource AuditOperationDownload(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {
		response.setContentType("application/octet-stream");
		InputStreamResource resource = null;
		try {
			File repfile = excelPdfDownloadService.getAuditOperationFile(from_date, to_date, filetype);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "getUserBlobImage/{userID}", method = RequestMethod.GET)
	@ResponseBody
	public String BlobImageUser(@PathVariable("userID") String userID, Model md) {
		UserProfile userProfile = mandateServices.UserBlobImages(userID);
		return Base64.getEncoder().encodeToString(userProfile.getPhoto());
	}

	@RequestMapping(value = "getMerBlobImage/{userID}", method = RequestMethod.GET)
	@ResponseBody
	public String getMerBlobImage(@PathVariable("userID") String userID, Model md) {
		MerchantMaster query = mandateServices.MerBlobImages(userID);
		return Base64.getEncoder().encodeToString(query.getPhoto());
	}

	@RequestMapping(value = "getMerUserBlobImage/{userID}", method = RequestMethod.GET)
	@ResponseBody
	public String getMerUserBlobImage(@PathVariable("userID") String userID, Model md) {
		BIPS_Mer_User_Management_Entity query = mandateServices.MerUserBlobImages(userID);
		return Base64.getEncoder().encodeToString(query.getPhoto());
	}

	@RequestMapping(value = "addFeesAndServices", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addFeesandServices(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute IPSChargesAndFees ipsChargesAndFees, Model md, HttpServletRequest req) throws IOException {
		String userID = (String) req.getSession().getAttribute("USERID");
		String msg = loginSecurityServices.addFees(ipsChargesAndFees, formmode, userID);
		return msg;
	}

	@RequestMapping(value = "addverifyMerchantFeesAndServices", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addMerchantFeesAndServices(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String modifytime, @RequestParam(required = false) String entrytime,
			@ModelAttribute MerchantChargesandFeesMod merchantChargesAndFees, HttpServletRequest req) {
		Date modifytimeDate = null;
		Date entrytimeDate = null;
		if (modifytime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				modifytimeDate = sdf.parse(modifytime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (entrytime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				entrytimeDate = sdf.parse(entrytime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String userID = (String) req.getSession().getAttribute("USERID");
		String msg = loginSecurityServices.addFees(merchantChargesAndFees, formmode, userID, modifytimeDate,
				entrytimeDate);
		return msg;
	}

	@RequestMapping(value = "editMerchantFeesAndServices", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String editMerchantFeesAndServices(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String verifyuser, @RequestParam(required = false) String entryuserss,
			@ModelAttribute MerchantChargesAndFees merchantChargesAndFees, HttpServletRequest req) {
		String a = entryuserss;
		String userID = (String) req.getSession().getAttribute("USERID");
		String msg = loginSecurityServices.editFees(merchantChargesAndFees, formmode, userID, verifyuser, a);
		return msg;
	}

	@RequestMapping(value = "/getRoleDetails/{roleid}", method = { RequestMethod.GET, RequestMethod.POST })
	public String gettingpatvisitdetail(@PathVariable(value = "roleid", required = true) String roleid) {
		String roleiddetails = accessandRolesRepository.rulelistCODE(roleid);
		// System.out.println("roleid = " + roleid);
		return roleiddetails;
	}

	@RequestMapping(value = "addmerchantReg2", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addmerchantReg2(@RequestParam(required = false) String formmode,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(required = false) String userid, @ModelAttribute MerchantMasterMod bankAgentTable, Model md,
			HttpServletRequest req) throws IOException, SQLException {
		String ID = (String) req.getSession().getAttribute("USERID");
		if (file != null) {
			byte[] byteArr = file.getBytes();
			bankAgentTable.setPhoto(byteArr);
		}
		String msg = bankAndBranchMasterServices.addmerchantReg2(bankAgentTable, formmode, ID);
		return msg;
	}

	@RequestMapping(value = "getBankDetail", method = RequestMethod.GET)
	public BankAgentTable getBankDetails(@RequestParam("bank_name") String bank_name) {
		// System.out.println("TEST");
		return bankAgentTableRep.findByCustomBankDetails(bank_name);
	}

	@RequestMapping(value = "getBankDetail2", method = RequestMethod.GET)
	public BankAgentTable getBankDetails2(@RequestParam("bank_name2") String bank_name2) {
		// System.out.println("TEST");
		return bankAgentTableRep.findByCustomBankDetails2(bank_name2);
	}

	@RequestMapping(value = "getFeeDetail", method = RequestMethod.GET)
	public MerchantChargesAndFees getFeeDetail(@RequestParam("fee_desc") String fee_desc) {
		// System.out.println("TEST1");
		return merchantChargesAndFeesRepositry.findByFeeDesc(fee_desc);
	}

	@RequestMapping(value = "getAllFeeDetail", method = RequestMethod.GET)
	public List<MerchantChargesAndFees> getAllFeeDetail() {
		// System.out.println("TEST2");
		return merchantChargesAndFeesRepositry.findAllCustom();
	}

	@RequestMapping(value = "addunitform", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addunitform(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid,
			@ModelAttribute BIPS_Unit_Mangement_Entity BIPS_Unit_Mangement_Entitys, Model md, HttpServletRequest req)
			throws IOException {

		String ID = (String) req.getSession().getAttribute("USERID");
		String msg = bankAndBranchMasterServices.addunit(BIPS_Unit_Mangement_Entitys, formmode, ID);
		return msg;
	}

	@GetMapping(path = "getContact", produces = "application/json", consumes = "application/json")
	public AccountContactResponse getContact(@RequestParam(required = false) String accountNumber)
			throws DatatypeConfigurationException, JAXBException, KeyManagementException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		logger.info("Account Number : " + accountNumber);
		AccountContactResponse response = null;
		ResponseEntity<AccountContactResponse> connect24ResponseAccContactExist = connect24Service
				.getAccountContact(accountNumber);
		if (connect24ResponseAccContactExist.getStatusCode() == HttpStatus.OK) {
			// System.out.println("success");
			response = connect24ResponseAccContactExist.getBody();
			if (response.getAccountStatus().equals("A") || response.getStatus().equals("0")) {
				// Open Account
				if (response.getFrezCode().equals("N")) {
					// No Frez Account
					if (response.getSchmType().equals("CAA") || response.getSchmType().equals("SBA")
							|| response.getSchmType().equals("ODA")) {
						return response;
						// Success
					} else {
						// Failure
						throw new ServerErrorException(ErrorResponseCode.NOT_VALID_SCHM_ACCOUNT);
					}
				} else {
					// Frez Account
					throw new ServerErrorException(ErrorResponseCode.FREZ_ACCOUNT);
				}
			} else {
				// Closed Account
				throw new ServerErrorException(ErrorResponseCode.CLOSED_ACCOUNT);
			}
		} else if (connect24ResponseAccContactExist.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new ServerErrorException(ErrorResponseCode.SERVER_ERROR);
		} else {
			throw new Connect24Exception(
					errorResponseCod.ErrorCode(connect24ResponseAccContactExist.getBody().getError()));
		}
	}

	@RequestMapping(value = "DownloadTransactionDateChecking", method = RequestMethod.GET)
	public int DownloadTransactionDateChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype)
			throws IOException, SQLException, ParseException {

		List<BIPS_Outward_Trans_Monitoring_Entity> count = BIPS_OutwardTransMonitoring_Rep.getBetweenList(from_date,
				to_date);
		return count.size();
	}

	@RequestMapping(value = "DownloadChargebackDateForChecking", method = RequestMethod.GET)
	public int DownloadChargebackDateForChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype)
			throws IOException, SQLException, ParseException {

		List<BIPS_Charge_Back_Entity> count = BIPS_ChargeBack_Repo.getAllListBetweenForChecking(from_date, to_date);
		return count.size();
	}

	@RequestMapping(value = "DownloadChargebackDateChecking", method = RequestMethod.GET)
	public int DownloadChargebackDateChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "formmode", required = false) String formmode,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {
		List<BIPS_Charge_Back_Entity> count = null;
		// System.out.println("Entered");
		if (formmode.equals("Revert")) {
			count = BIPS_ChargeBack_Repo.getRevertedTransactionListBetween(from_date, to_date);
		} else if (formmode.equals("Pending")) {
			count = BIPS_ChargeBack_Repo.getPendingTransactionListBetween(from_date, to_date);
		} else {
			count = BIPS_ChargeBack_Repo.getAllListBetween(from_date, to_date);
		}

		// System.out.println(count.size());
		return count.size();
	}

	@RequestMapping(value = "DownloadAuditDateChecking", method = RequestMethod.GET)
	public int DownloadAuditDateChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date)
			throws IOException, SQLException, ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

		List<IPSAuditTable> count = IPSAuditTableRep.getauditListLocal(formatter.parse(from_date),
				formatter.parse(to_date));
		// System.out.println(count.size());
		return count.size();
	}

	@RequestMapping(value = "DownloadAuditOperationDateChecking", method = RequestMethod.GET)
	public int DownloadAuditOperationDateChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date)
			throws IOException, SQLException, ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

		List<IPSAuditTable> count = IPSAuditTableRep.getauditListOpeartion(formatter.parse(from_date),
				formatter.parse(to_date));
		// System.out.println(count.size());
		return count.size();
	}

	@RequestMapping(value = "getUnitDetails", method = RequestMethod.GET)
	public Object[] getUnitDetails(@RequestParam(required = false) String unitId) throws IOException, SQLException {

		return bIPS_UnitManagement_Repo.getUnitDetail(unitId);
	}

	@RequestMapping(value = "createSettlementAccount", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String createSettlementAccount(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@ModelAttribute SettlementAccount settlementAccount, Model md, HttpServletRequest req) throws IOException {
		String userID = (String) req.getSession().getAttribute("USERID");
		String msg = settlementAccountServices.CreateSettlement(settlementAccount, formmode, userID);
		return msg;

	}

	@RequestMapping(value = "getFeeDetails", method = RequestMethod.GET)
	public MerchantChargesAndFees getFeeDetails(@RequestParam("fee_desc") String fee_desc) {
		// System.out.println("fee_desc");
		return merchantChargesAndFeesRepositry.findByFeeDesc(fee_desc);
	}

	@RequestMapping(value = "getTransactionRecords", method = RequestMethod.GET)
	public List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionRecords(
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate) {

		SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
		Date currentDate = new Date();
		String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();
		String valueDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();

		List<BIPS_Outward_Trans_Monitoring_Entity> records = new ArrayList<>();
		if (currentDateRef.equals(valueDateRef)) {
			records = bIPS_OutwardTransMonitoring_Repo.getTranDevlst(currentDateRef);
		} else {
			records = bIPS_OutwardTransMonitoring_Repo.getTranDevlstHist(valueDateRef);
		}

		return records;
	}

	@RequestMapping(value = "getChargeBackRecords", method = RequestMethod.GET)
	public List<BIPS_Charge_Back_Entity> getChargeBackRecords(
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate,
			@RequestParam(required = false) String tranRecord) {

		SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
		Date currentDate = new Date();
		String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();
		String valueDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();

		List<BIPS_Charge_Back_Entity> records = new ArrayList<>();
		if (currentDateRef.equals(valueDateRef)) {
			if (tranRecord.equalsIgnoreCase("ALL")) {
				records = bips_ChargeBack_Repo.getAllList(currentDateRef);
			} else if (tranRecord.equalsIgnoreCase("PENDING")) {
				records = bips_ChargeBack_Repo.getPendingTransactionList(currentDateRef);
			} else {
				records = bips_ChargeBack_Repo.getRevertedTransactionList(currentDateRef);
			}

		} else {
			if (tranRecord.equalsIgnoreCase("ALL")) {
				records = bips_ChargeBack_Repo.getAllList(valueDateRef);
			} else if (tranRecord.equalsIgnoreCase("PENDING")) {
				records = bips_ChargeBack_Repo.getPendingTransactionList(valueDateRef);
			} else {
				records = bips_ChargeBack_Repo.getRevertedTransactionList(valueDateRef);
			}
		}

		return records;
	}

	@RequestMapping(value = "getMerchantName", method = RequestMethod.GET)
	public String getMerchantName(@RequestParam(required = false) String merchantId) {

		String merchantName = merchantMasterRep.getMerchantName(merchantId);
		return merchantName.trim();
	}

	@RequestMapping(value = "getMerchantUserId", method = RequestMethod.GET)
	public List<String> getMerchantUserId(@RequestParam(required = false) String merchantId) {

		List<String> merchantUserIds = bIPS_MerUserManagement_Repo.getuserid(merchantId);
		return merchantUserIds;
	}

	@RequestMapping(value = "getMerchantUnitId", method = RequestMethod.GET)
	public List<String> getMerchantUnitId(@RequestParam(required = false) String merchantId) {

		List<String> merchantUnitIds = bIPS_UnitManagement_Repo.getpartUnitId(merchantId);
		return merchantUnitIds;
	}

	@RequestMapping(value = "getMerchantDeviceId", method = RequestMethod.GET)
	public List<String> getMerchantDeviceId(@RequestParam(required = false) String merchantId) {

		List<String> merchantDeviceIds = bIPS_MerDeviceManagement_Repo.getdeviceId(merchantId);
		return merchantDeviceIds;
	}

	// Delete Records

	@Autowired
	MerchantMasterModRep merchantModRep;

	@Autowired
	BIPS_PasswordManagement_Repo passwordManagement_Repo;

	@RequestMapping(value = "deleteMerchantRecord", method = RequestMethod.GET)
	public ResponseEntity<String> deleteMerchantRecord(@RequestParam(required = false) String merchantId,
			@RequestParam(required = false) String remarks, HttpServletRequest req) {
		if (merchantId == null || merchantId.isEmpty()) {
			return ResponseEntity.badRequest().body("Merchant ID is required.");
		}

		String USERID = (String) req.getSession().getAttribute("USERID");
		String msg;

		try {
			MerchantMaster merResponse = merchantMasterRep.findByIdCustom(merchantId);
			MerchantMasterMod merResponse1 = merchantModRep.findByIdCustomDelete(merchantId);

			if (Objects.nonNull(merResponse)) {
				MerchantMaster merchant = merResponse;
				merchant.setDel_flg('Y');
				merchant.setDelete_remarks(remarks);
				merchant.setDelete_user(USERID);
				merchant.setDelete_time(new Date());
				removeUserRecords(merchantId, USERID);
				removeUnitRecords(merchantId, USERID);
				removeDeviceRecords(merchantId, USERID);
				removePasswordManagement(merchantId, USERID);
				merchantMasterRep.save(merchant);
				msg = "Merchant deleted successfully.";
			} else if (Objects.nonNull(merResponse1)) {
				MerchantMasterMod merchantMod = merResponse1;
				merchantMod.setDel_flg('Y');
				merchantMod.setDelete_remarks(remarks);
				merchantMod.setDelete_user(USERID);
				merchantMod.setDelete_time(new Date());
				merchantModRep.save(merchantMod);
				msg = "Merchant deleted successfully.";
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Merchant deletion failed. Merchant not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred during merchant deletion.");
		}

		return ResponseEntity.ok(msg);
	}

	public void removeUserRecords(String merchantId, String USERID) {
		List<BIPS_Mer_User_Management_Entity> userEntityList = bIPS_MerUserManagement_Repo.getUserManage1(merchantId);
		for (BIPS_Mer_User_Management_Entity entity : userEntityList) {
			entity.setDel_flag1("Y");
			entity.setDelete_time(new Date());
			entity.setDelete_user(USERID);
			entity.setUser_status1("INACTIVE");
			bIPS_MerUserManagement_Repo.save(entity);
		}
	}

	public void removeUnitRecords(String merchantId, String USERID) {
		List<BIPS_Unit_Mangement_Entity> unitEntityList = bIPS_UnitManagement_Repo.getUnitlist(merchantId);
		for (BIPS_Unit_Mangement_Entity entity : unitEntityList) {
			entity.setDel_flg("Y");
			entity.setDelete_date(new Date());
			entity.setDelete_user(USERID);
			bIPS_UnitManagement_Repo.save(entity);
		}
	}

	public void removeDeviceRecords(String merchantId, String USERID) {
		List<BIPS_Mer_Device_Management_Entity> unitEntityList = bIPS_MerDeviceManagement_Repo
				.getUnDeleteRecord(merchantId);
		for (BIPS_Mer_Device_Management_Entity entity : unitEntityList) {
			entity.setDisable_flag("Y");
			entity.setDel_flg("Y");
			entity.setDelete_time(new Date());
			entity.setDelete_user(USERID);
			entity.setDevice_removeddate(new Date());
			bIPS_MerDeviceManagement_Repo.save(entity);
		}
	}

	public void removePasswordManagement(String merchantId, String USERID) {
		List<BIPS_Password_Management_Entity> unitEntityList = passwordManagement_Repo.getpasslst(merchantId);
		for (BIPS_Password_Management_Entity entity : unitEntityList) {
			entity.setUser_disable_flag("Y");
			entity.setDel_flag("Y");
			entity.setDel_time(new Date());
			entity.setDel_user(USERID);
			entity.setRemarks("");
			passwordManagement_Repo.save(entity);
		}
	}

	@RequestMapping(value = "deleteRcordByUnitId", method = RequestMethod.GET)
	public ResponseEntity<String> deleteUnitRcordByMerchantId(@RequestParam(required = false) String unitId,
			@RequestParam String unitRemarks, HttpServletRequest req) {
		if (unitId == null || unitId.isEmpty()) {
			return ResponseEntity.badRequest().body("Unit ID is required.");
		}

		String USERID = (String) req.getSession().getAttribute("USERID");
		String msg;

		try {
			BIPS_Unit_Mangement_Entity unitEntity = bIPS_UnitManagement_Repo.getUnitId(unitId);
			if (unitEntity != null) {
				unitEntity.setDel_flg("Y");
				unitEntity.setDelete_date(new Date());
				unitEntity.setDelete_user(USERID);
				unitEntity.setRemarks(unitRemarks);
				bIPS_UnitManagement_Repo.save(unitEntity);
				msg = "Unit Record deleted successfully.";
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unit Record deletion failed. Unit not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred during Unit Record deletion.");
		}

		return ResponseEntity.ok(msg);
	}

	@RequestMapping(value = "deleteRecordByUserId", method = RequestMethod.GET)
	public ResponseEntity<String> deleteUserRecordByMerchantId(@RequestParam(required = false) String userId,
			@RequestParam(required = false) String userRemarks, HttpServletRequest req) {
		if (userId == null || userId.isEmpty()) {
			return ResponseEntity.badRequest().body("User ID is required.");
		}

		String USERID = (String) req.getSession().getAttribute("USERID");
		String msg;

		try {
			BIPS_Mer_User_Management_Entity userEntity = bIPS_MerUserManagement_Repo.getuser(userId);
			if (userEntity != null) {
				userEntity.setDel_flag1("Y");
				userEntity.setDelete_time(new Date());
				userEntity.setDelete_user(USERID);
				userEntity.setUser_status1("INACTIVE");
				bIPS_MerUserManagement_Repo.save(userEntity);
				msg = "User Record deleted successfully.";
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Record deletion failed. User not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred during User Record deletion.");
		}

		return ResponseEntity.ok(msg);
	}

	@RequestMapping(value = "deleteRecordByDeviceId", method = RequestMethod.GET)
	public ResponseEntity<String> deleteRecordByDeviceId(@RequestParam(required = false) String deviceId,
			@RequestParam(required = false) String deviceRemarks, HttpServletRequest req) {
		if (deviceId == null || deviceId.isEmpty()) {
			return ResponseEntity.badRequest().body("Device ID is required.");
		}

		String USERID = (String) req.getSession().getAttribute("USERID");
		String msg;

		try {
			BIPS_Mer_Device_Management_Entity deviceEntity = bIPS_MerDeviceManagement_Repo.getdevice(deviceId);
			if (Objects.nonNull(deviceEntity)) {
				deviceEntity.setDisable_flag("Y");
				deviceEntity.setDel_flg("Y");
				deviceEntity.setDelete_time(new Date());
				deviceEntity.setDelete_user(USERID);
				deviceEntity.setDevice_removeddate(new Date());
				deviceEntity.setRemark(deviceRemarks);
				bIPS_MerDeviceManagement_Repo.save(deviceEntity);
				msg = "Device Record deleted successfully.";
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Device Record deletion failed. Device not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred during device record deletion.");
		}

		return ResponseEntity.ok(msg);
	}

	@RequestMapping(value = "deleteRecordByMerRepId", method = RequestMethod.GET)
	public ResponseEntity<String> deleteRecordByMerRepId(@RequestParam(required = false) String merRepId,
			@RequestParam(required = false) String merRepRemarks, HttpServletRequest req) {
		if (merRepId == null || merRepId.isEmpty()) {
			return ResponseEntity.badRequest().body("MerRep ID is required.");
		}

		String USERID = (String) req.getSession().getAttribute("USERID");
		String msg;

		try {
			BIPS_Password_Management_Entity passEntity = passwordManagement_Repo.getbyflg(merRepId);
			if (Objects.nonNull(passEntity)) {
				passEntity.setUser_disable_flag("Y");
				passEntity.setDel_flag("Y");
				passEntity.setDel_time(new Date());
				passEntity.setDel_user(USERID);
				passEntity.setRemarks(merRepRemarks);
				passwordManagement_Repo.save(passEntity);
				msg = "Password Management Record deleted successfully.";
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Password Management Record deletion failed. Record not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred during password management record deletion.");
		}

		return ResponseEntity.ok(msg);
	}

	@RequestMapping(value = "TotalTransactionDownload", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> TotalTransactionDownload(HttpServletResponse response,
			@RequestParam(value = "merchant_id", required = false) String merchant_id,
			@RequestParam(value = "unit_id", required = false) String unit_id,
			@RequestParam(value = "user_id", required = false) String user_id,
			@RequestParam(value = "device_id", required = false) String device_id,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype) {

		try {
			File repfile = excelPdfDownloadService.getTotalTxnFile(from_date, to_date, filetype, merchant_id, unit_id,
					user_id, device_id);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			response.setContentLengthLong(repfile.length());
			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
		} catch (FileNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
		}
	}

	@RequestMapping(value = "TotalChargebackDownload", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> TotalChargebackDownload(HttpServletResponse response,
			@RequestParam(value = "merchant_id", required = false) String merchant_id,
			@RequestParam(value = "unit_id", required = false) String unit_id,
			@RequestParam(value = "user_id", required = false) String user_id,
			@RequestParam(value = "device_id", required = false) String device_id,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "to_date", required = false) String to_date,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {

		try {
			File repfile = excelPdfDownloadService.getTotalChargebackFile(from_date, to_date, filetype, merchant_id,
					unit_id, user_id, device_id);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			response.setContentLengthLong(repfile.length());
			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
		} catch (FileNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
		}
	}

	@RequestMapping(value = "resetUserData", method = RequestMethod.GET)
	@ResponseBody
	public String resetUserData(String user_id) {
		BIPS_Mer_User_Management_Entity login = bIPS_MerUserManagement_Repo.getuser(user_id);

		if (Objects.nonNull(login)) {
			login.setLogin_status1("N");
			login.setUser_locked_flg("N");
			login.setNo_of_attmp("0");
			login.setUser_status1("ACTIVE");
			bIPS_MerUserManagement_Repo.save(login);

			String audit_ref_no = sequence.generateRequestUUId();
			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(login.getUser_id());
			audit.setFunc_code("LOGOUT");
			audit.setRemarks("LOGOUT SUCCESSFULLY");
			audit.setAudit_table("BIPS_MERCHANT_USER_MANAGEMENT");
			audit.setAudit_screen("LOGOUT");
			audit.setEvent_id(login.getUser_id());
			audit.setEvent_name(login.getUser_name());
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);

			return "User Id Reset Successfully";
		} else {
			return "User Id Not found";
		}
	}

	@RequestMapping(value = "resetRepData", method = RequestMethod.GET)
	@ResponseBody
	public String resetRepData(String MerchantRepId) {
		BIPS_Password_Management_Entity login = passwordManagement_Repo.getRepId(MerchantRepId);

		System.out.print("Logout Representative");
		if (Objects.nonNull(login)) {
			login.setLogin_status("N");
			login.setUser_locked_flg("N");
			login.setNo_of_attmp(BigDecimal.ZERO);
			login.setUser_status("ACTIVE");
			passwordManagement_Repo.save(login);

			String audit_ref_no = sequence.generateRequestUUId();
			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(login.getMerchant_rep_id());
			audit.setFunc_code("LOGOUT");
			audit.setRemarks("LOGOUT SUCCESSFULLY");
			audit.setAudit_table("BIPS_PASSWORD_MANAGEMENT");
			audit.setAudit_screen("LOGOUT");
			audit.setEvent_id(login.getMerchant_rep_id());
			audit.setEvent_name(login.getMer_representative_name());
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);

			return "Representative Id Reset Successfully";
		} else {
			return "Representative Id Not Found";
		}
	}

	@Autowired
	UserProfileRep userProfileRep;

	@RequestMapping(value = "resetUserProfileData", method = RequestMethod.GET)
	@ResponseBody
	public String resetUserProfileData(String user_id) {

		UserProfile login = userProfileRep.getEmployeedetails(user_id);
		if (Objects.nonNull(login)) {
			login.setLogin_status("N");
			login.setUser_locked_flg("N");
			login.setNo_of_attmp(0);
			login.setUser_status("ACTIVE");
			login.setDel_flg("N");
			userProfileRep.save(login);
			String audit_ref_no = sequence.generateRequestUUId();
			IPSAuditTable audit = new IPSAuditTable();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(login.getUserid());
			audit.setFunc_code("LOGOUT");
			audit.setRemarks("LOGOUT SUCCESSFULLY");
			audit.setAudit_table("BIPS_USER_PROFILE");
			audit.setAudit_screen("LOGOUT");
			audit.setEvent_id(login.getUserid());
			audit.setEvent_name(login.getUsername());
			audit.setModi_details("-");
			audit.setAudit_ref_no(audit_ref_no);
			ipsAuditTableRep.save(audit);
			return "User Reset Successfully";
		} else {
			return "User Not Found";
		}
	}

	@Autowired
	MerchantChargesAndFeesRepositry merchantChargesAndFeesRep;

	@RequestMapping(value = "checkUniqueId", method = RequestMethod.GET)
	@ResponseBody
	public String checkUniqueId(@RequestParam("desc") String desc) {
		// Fetch the entity using the description
		MerchantChargesAndFees findDesc = merchantChargesAndFeesRep.findByDescription(desc);

		// Check if the entity is found
		if (Objects.nonNull(findDesc)) {
			return "Description already exists"; // Better to return an appropriate message
		} else {
			return null;
		}
	}

	@RequestMapping(value = "DownloadUnitList", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> DownloadUnitList(HttpServletResponse response,
			@RequestParam(value = "merchant_user_id", required = true) String merchant_user_id,
			@RequestParam(value = "filetype", required = true) String filetype) {

		try {
			File repfile = excelPdfDownloadService.getUnitFile(merchant_user_id, filetype);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			response.setContentLengthLong(repfile.length());
			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
		} catch (FileNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
		}
	}

	@RequestMapping(value = "DownloadUserList", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> downloadUserList(
			@RequestParam(value = "merchant_user_id", required = false) String merchantUserId,
			@RequestParam(value = "filetype", required = false) String fileType) {

		try {
			File reportFile = excelPdfDownloadService.getUserFile(merchantUserId, fileType);
			String contentType = fileType.equalsIgnoreCase("pdf") ? "application/pdf"
					: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			InputStreamResource resource = new InputStreamResource(new FileInputStream(reportFile));

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportFile.getName())
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@RequestMapping(value = "DownloadDeviceList", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> downloadDeviceList(
			@RequestParam(value = "merchant_user_id", required = false) String merchantUserId,
			@RequestParam(value = "filetype", required = false) String fileType) {

		try {
			File reportFile = excelPdfDownloadService.getDeviceFile(merchantUserId, fileType);
			String contentType = fileType.equalsIgnoreCase("pdf") ? "application/pdf"
					: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			InputStreamResource resource = new InputStreamResource(new FileInputStream(reportFile));

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportFile.getName())
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	// AML CHECK

		@RequestMapping(value = "CheckUnit", method = { RequestMethod.GET })
		@ResponseBody
		public Map<String, String> CheckUnit(@RequestParam(required = false) String brn_no,
				@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date brn_date,
				HttpServletRequest req) {
			System.out.println(brn_no);
			Map<String, String> response = new HashMap<>();
			StringBuilder duplicateMsg = new StringBuilder();
			StringBuilder negativeMsg = new StringBuilder();
			StringBuilder blackMsg = new StringBuilder();
			StringBuilder pepMsg = new StringBuilder();
			String userID = (String) req.getSession().getAttribute("USERID");
			boolean duplicateFound = false;
			boolean negativeFound = false;
			boolean blackFound = false;
			boolean pepFound = false;
			try {
				String sequenceValue = bIPS_CheckList_Repo.getSequence();

				BIPS_CheckListEntity checklist = new BIPS_CheckListEntity();
				checklist.setSrl_no(sequenceValue);
				checklist.setEntry_user(userID);
				checklist.setDel_flg("N");
				checklist.setEntry_time(new Date());
				checklist.setBrn_unit(brn_no);
				checklist.setBrn_date_unit(brn_date);
				
				// Check for duplicates
				List<String> brn_no1 = dedup_Repo.getBrn_No_Unit();
				if (brn_no1.contains(brn_no)) {
					duplicateFound = true;
					duplicateMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}

				// Format the date
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String formattedBrnDate = dateFormat.format(brn_date);

				// Check for duplicate date
				List<Date> brn_date1 = dedup_Repo.findBrnDateUnit();
				List<String> formattedBrnDates = new ArrayList<>();
				for (Date date : brn_date1) {
					formattedBrnDates.add(dateFormat.format(date));
				}

				if (formattedBrnDates.contains(formattedBrnDate)) {
					duplicateFound = true;
					duplicateMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDate)
							.append("</td><td>").append(formattedBrnDate).append("</td></tr>");
				}

				// Prepare duplicate response
				if (duplicateFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(duplicateMsg).append("</tbody>").append("</table>");
					checklist.setBrn_unit(brn_no);

					checklist.setBrn_date_unit(brn_date);
					checklist.setDuplicate_check("YES");

					bIPS_CheckList_Repo.save(checklist);
					response.put("duplicate", table.toString());

				} else {
					checklist.setDuplicate_check("YES");

					checklist.setRemark("No Match Found");
					checklist.setEntry_user(userID);
					checklist.setDel_flg("N");
					checklist.setEntry_time(new Date());
					bIPS_CheckList_Repo.save(checklist);
					response.put("duplicate", "MATCH NOT FOUND");
				}

				// Check for negative list
				List<String> brn_no_neg = Negative_Repo.getBrn_No_Unit();
				if (brn_no_neg.contains(brn_no)) {
					negativeFound = true;
					negativeMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}

				String formattedBrnDateNeg = dateFormat.format(brn_date);
				List<Date> brn_date_neg = Negative_Repo.findBrnDateUnit();
				List<String> formattedBrnDatesNeg = new ArrayList<>();
				for (Date date : brn_date_neg) {
					formattedBrnDatesNeg.add(dateFormat.format(date));
				}

				if (formattedBrnDatesNeg.contains(formattedBrnDateNeg)) {
					negativeFound = true;
					negativeMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDateNeg)
							.append("</td><td>").append(formattedBrnDateNeg).append("</td></tr>");
				}

				// Prepare negative response
				if (negativeFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(negativeMsg).append("</tbody>").append("</table>");

					checklist.setBrn_unit(brn_no);
					checklist.setBrn_date_unit(brn_date);
					checklist.setNegative_check("YES");

					bIPS_CheckList_Repo.save(checklist);

					response.put("negative", table.toString());
				} else {
					checklist.setNegative_check("YES");
					response.put("negative", "MATCH NOT FOUND");
				}
				// black list found

				List<String> brn_no_blac = black_Repo.getBrn_No_Unit();
				if (brn_no_blac.contains(brn_no)) {
					blackFound = true;
					blackMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}

				String formattedBrnDateBlac = dateFormat.format(brn_date);
				List<Date> brn_date_bla = black_Repo.findBrnDateUnit();
				List<String> formattedBrnDatesBla = new ArrayList<>();
				for (Date date : brn_date_bla) {
					formattedBrnDatesBla.add(dateFormat.format(date));
				}

				if (formattedBrnDatesBla.contains(formattedBrnDateBlac)) {
					blackFound = true;
					blackMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDateNeg)
							.append("</td><td>").append(formattedBrnDateNeg).append("</td></tr>");
				}

				// Prepare negative response
				if (blackFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(blackMsg).append("</tbody>").append("</table>");

					checklist.setBrn_unit(brn_no);
					checklist.setBrn_date_unit(brn_date);
					checklist.setBlack_check("YES");

					bIPS_CheckList_Repo.save(checklist);

					response.put("Black", table.toString());
				} else {
					checklist.setBlack_check("YES");
					response.put("Black", "MATCH NOT FOUND");
				}

				// pep list found

				List<String> brn_no_pep = Pep_Repo.getBrn_No_Unit();
				if (brn_no_pep.contains(brn_no)) {
					pepFound = true;
					pepMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}

				String formattedBrnDatePep = dateFormat.format(brn_date);
				List<Date> brn_date_pep = Pep_Repo.findBrnDateUnit();
				List<String> formattedBrnDatespep = new ArrayList<>();
				for (Date date : brn_date_pep) {
					formattedBrnDatespep.add(dateFormat.format(date));
				}

				if (formattedBrnDatespep.contains(formattedBrnDatePep)) {
					pepFound = true;
					pepMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDateNeg)
							.append("</td><td>").append(formattedBrnDateNeg).append("</td></tr>");
				}

				// Prepare negative response
				if (pepFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(pepMsg).append("</tbody>").append("</table>");

					checklist.setBrn_unit(brn_no);
					checklist.setBrn_date_unit(brn_date);
					checklist.setPep_check("YES");

					bIPS_CheckList_Repo.save(checklist);

					response.put("Pep", table.toString());
				} else {
					checklist.setPep_check("YES");
					response.put("Pep", "MATCH NOT FOUND");
				}
				if (duplicateFound && negativeFound && blackFound && pepFound) {
					checklist.setRemark("Match Found");
				} else if (duplicateFound || negativeFound || blackFound || pepFound) {
					checklist.setRemark("Match Found");
				} else if (duplicateFound) {
					checklist.setRemark("Duplicate Match Found");
				} else if (negativeFound) {
					checklist.setRemark("Negative List Match Found");
				} else if (blackFound) {
					checklist.setRemark("Black List Match Found");
				} else if (pepFound) {
					checklist.setRemark("Pep List Match Found");
				} else {
					checklist.setRemark("No Match Found");
				}
				bIPS_CheckList_Repo.save(checklist);
			} catch (NullPointerException e) {
				response.put("error", "clear");
			}

			return response;
		}

		@RequestMapping(value = "CheckMerchant", method = { RequestMethod.GET })
		@ResponseBody
		public Map<String, String> CheckMerchant(@RequestParam(required = false) String brn_no,
				@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date brn_date,
				HttpServletRequest req, @RequestParam(required = false) String merchant_corp_name) {
			System.out.println(brn_no);
			Map<String, String> response = new HashMap<>();
			StringBuilder duplicateMsg = new StringBuilder();
			StringBuilder negativeMsg = new StringBuilder();
			StringBuilder blackMsg = new StringBuilder();
			StringBuilder pepMsg = new StringBuilder();
			String userID = (String) req.getSession().getAttribute("USERID");
			boolean duplicateFound = false;
			boolean negativeFound = false;
			boolean blackFound = false;
			boolean pepFound = false;
			try {
				String sequenceValue = bIPS_CheckList_Repo.getSequence();

				BIPS_CheckListEntity checklist = new BIPS_CheckListEntity();
				checklist.setSrl_no(sequenceValue);
				checklist.setEntry_user(userID);
				checklist.setDel_flg("N");
				checklist.setEntry_time(new Date());
				checklist.setBrn_unit(brn_no);
				checklist.setBrn_date_unit(brn_date);
				checklist.setCorporatename(merchant_corp_name);
				// Check for duplicates
				List<String> brn_no1 = dedup_Repo.getBrn_No_Unit();
				if (brn_no1.contains(brn_no)) {
					duplicateFound = true;
					duplicateMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}
				List<String> merchant_corp_name1 = dedup_Repo.getcorporatename();
				if (merchant_corp_name1.contains(merchant_corp_name)) {
					duplicateFound = true;
					duplicateMsg.append("<tr><td>Merchant Corporate Name</td><td>").append(merchant_corp_name)
							.append("</td><td>").append(merchant_corp_name).append("</td></tr>");
				}
				// Format the date
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String formattedBrnDate = dateFormat.format(brn_date);

				// Check for duplicate date
				List<Date> brn_date1 = dedup_Repo.findBrnDateUnit();
				List<String> formattedBrnDates = new ArrayList<>();
				for (Date date : brn_date1) {
					formattedBrnDates.add(dateFormat.format(date));
				}

				if (formattedBrnDates.contains(formattedBrnDate)) {
					duplicateFound = true;
					duplicateMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDate)
							.append("</td><td>").append(formattedBrnDate).append("</td></tr>");
				}

				// Prepare duplicate response
				if (duplicateFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(duplicateMsg).append("</tbody>").append("</table>");
					checklist.setBrn_unit(brn_no);

					checklist.setBrn_date_unit(brn_date);
					checklist.setDuplicate_check("YES");

					bIPS_CheckList_Repo.save(checklist);
					response.put("duplicate", table.toString());

				} else {
					checklist.setDuplicate_check("YES");

					checklist.setRemark("No Match Found");
					checklist.setEntry_user(userID);
					checklist.setDel_flg("N");
					checklist.setEntry_time(new Date());
					bIPS_CheckList_Repo.save(checklist);
					response.put("duplicate", "MATCH NOT FOUND");
				}

				// Check for negative list
				List<String> brn_no_neg = Negative_Repo.getBrn_No_Unit();
				if (brn_no_neg.contains(brn_no)) {
					negativeFound = true;
					negativeMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}
				List<String> merchant_corp_name_neg1 = Negative_Repo.getcorporatename();
				if (merchant_corp_name_neg1.contains(merchant_corp_name)) {
					negativeFound = true;
					negativeMsg.append("<tr><td>Merchant Corporate Name</td><td>").append(merchant_corp_name)
							.append("</td><td>").append(merchant_corp_name).append("</td></tr>");
				}

				String formattedBrnDateNeg = dateFormat.format(brn_date);
				List<Date> brn_date_neg = Negative_Repo.findBrnDateUnit();
				List<String> formattedBrnDatesNeg = new ArrayList<>();
				for (Date date : brn_date_neg) {
					formattedBrnDatesNeg.add(dateFormat.format(date));
				}

				if (formattedBrnDatesNeg.contains(formattedBrnDateNeg)) {
					negativeFound = true;
					negativeMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDateNeg)
							.append("</td><td>").append(formattedBrnDateNeg).append("</td></tr>");
				}

				// Prepare negative response
				if (negativeFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(negativeMsg).append("</tbody>").append("</table>");

					checklist.setBrn_unit(brn_no);
					checklist.setBrn_date_unit(brn_date);
					checklist.setNegative_check("YES");

					bIPS_CheckList_Repo.save(checklist);

					response.put("negative", table.toString());
				} else {
					checklist.setNegative_check("YES");
					response.put("negative", "MATCH NOT FOUND");
				}
				// black list found

				List<String> brn_no_blac = black_Repo.getBrn_No_Unit();
				if (brn_no_blac.contains(brn_no)) {
					blackFound = true;
					blackMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}
				List<String> merchant_corp_name_bla1 = black_Repo.getcorporatename();
				if (merchant_corp_name_bla1.contains(merchant_corp_name)) {
					blackFound = true;
					blackMsg.append("<tr><td>Merchant Corporate Name</td><td>").append(merchant_corp_name)
							.append("</td><td>").append(merchant_corp_name).append("</td></tr>");
				}

				String formattedBrnDateBlac = dateFormat.format(brn_date);
				List<Date> brn_date_bla = black_Repo.findBrnDateUnit();
				List<String> formattedBrnDatesBla = new ArrayList<>();
				for (Date date : brn_date_bla) {
					formattedBrnDatesBla.add(dateFormat.format(date));
				}

				if (formattedBrnDatesBla.contains(formattedBrnDateBlac)) {
					blackFound = true;
					blackMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDateNeg)
							.append("</td><td>").append(formattedBrnDateNeg).append("</td></tr>");
				}

				// Prepare negative response
				if (blackFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(blackMsg).append("</tbody>").append("</table>");

					checklist.setBrn_unit(brn_no);
					checklist.setBrn_date_unit(brn_date);
					checklist.setBlack_check("YES");

					bIPS_CheckList_Repo.save(checklist);

					response.put("Black", table.toString());
				} else {
					checklist.setBlack_check("YES");
					response.put("Black", "MATCH NOT FOUND");
				}

				// pep list found

				List<String> brn_no_pep = Pep_Repo.getBrn_No_Unit();
				if (brn_no_pep.contains(brn_no)) {
					pepFound = true;
					pepMsg.append("<tr><td>Business Registration No</td><td>").append(brn_no).append("</td><td>")
							.append(brn_no).append("</td></tr>");
				}
				List<String> merchant_corp_namepep_1 = Pep_Repo.getcorporatename();
				if (merchant_corp_namepep_1.contains(merchant_corp_name)) {
					pepFound = true;
					pepMsg.append("<tr><td>Merchant Corporate Name</td><td>").append(merchant_corp_name).append("</td><td>")
							.append(merchant_corp_name).append("</td></tr>");
				}

				String formattedBrnDatePep = dateFormat.format(brn_date);
				List<Date> brn_date_pep = Pep_Repo.findBrnDateUnit();
				List<String> formattedBrnDatespep = new ArrayList<>();
				for (Date date : brn_date_pep) {
					formattedBrnDatespep.add(dateFormat.format(date));
				}

				if (formattedBrnDatespep.contains(formattedBrnDatePep)) {
					pepFound = true;
					pepMsg.append("<tr><td>Business Registration Date</td><td>").append(formattedBrnDateNeg)
							.append("</td><td>").append(formattedBrnDateNeg).append("</td></tr>");
				}

				// Prepare negative response
				if (pepFound) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(pepMsg).append("</tbody>").append("</table>");

					checklist.setBrn_unit(brn_no);
					checklist.setBrn_date_unit(brn_date);
					checklist.setPep_check("YES");

					bIPS_CheckList_Repo.save(checklist);

					response.put("Pep", table.toString());
				} else {
					checklist.setPep_check("YES");
					response.put("Pep", "MATCH NOT FOUND");
				}
				if (duplicateFound && negativeFound && blackFound && pepFound) {
					checklist.setRemark("Match Found");
				} else if (duplicateFound || negativeFound || blackFound || pepFound) {
					checklist.setRemark("Match Found");
				} else if (duplicateFound) {
					checklist.setRemark("Duplicate Match Found");
				} else if (negativeFound) {
					checklist.setRemark("Negative List Match Found");
				} else if (blackFound) {
					checklist.setRemark("Black List Match Found");
				} else if (pepFound) {
					checklist.setRemark("Pep List Match Found");
				} else {
					checklist.setRemark("No Match Found");
				}
				bIPS_CheckList_Repo.save(checklist);
			} catch (NullPointerException e) {
				response.put("error", "clear");
			}

			return response;
		}

		@RequestMapping(value = "CheckContact", method = { RequestMethod.GET })
		@ResponseBody
		public Map<String, String> CheckContact(HttpServletRequest req,
				@RequestParam(value = "mer_ph_no", required = false) String merPhNo,
				@RequestParam(value = "mer_ph_no_r2", required = false) String merPhNoR2,
				@RequestParam(value = "mer_ph_no_r3", required = false) String merPhNoR3,
				@RequestParam(value = "mer_ph_no_r4", required = false) String merPhNoR4,
				@RequestParam(value = "mer_ph_no_r5", required = false) String merPhNoR5,
				@RequestParam(value = "mer_ph_no_r6", required = false) String merPhNoR6,
				@RequestParam(value = "mer_ph_no_r7", required = false) String merPhNoR7,
				@RequestParam(value = "mer_ph_no_r8", required = false) String merPhNoR8,
				@RequestParam(value = "mer_ph_no_r9", required = false) String merPhNoR9,
				@RequestParam(value = "mer_ph_no_r10", required = false) String merPhNoR10,
				@RequestParam(value = "mer_nat_id", required = false) String merNatId,
				@RequestParam(value = "mer_nat_id_r2", required = false) String merNatIdR2,
				@RequestParam(value = "mer_nat_id_r3", required = false) String merNatIdR3,
				@RequestParam(value = "mer_nat_id_r4", required = false) String merNatIdR4,
				@RequestParam(value = "mer_nat_id_r5", required = false) String merNatIdR5,
				@RequestParam(value = "mer_nat_id_r6", required = false) String merNatIdR6,
				@RequestParam(value = "mer_nat_id_r7", required = false) String merNatIdR7,
				@RequestParam(value = "mer_nat_id_r8", required = false) String merNatIdR8,
				@RequestParam(value = "mer_nat_id_r9", required = false) String merNatIdR9,
				@RequestParam(value = "mer_nat_id_r10", required = false) String merNatIdR10) {
			System.out.println(merPhNo);
			Map<String, String> response = new HashMap<>();
			StringBuilder duplicateMsg = new StringBuilder();
			StringBuilder negativeMsg = new StringBuilder();
			StringBuilder blackMsg = new StringBuilder();
			StringBuilder pepMsg = new StringBuilder();
			String userID = (String) req.getSession().getAttribute("USERID");

			Map<String, String> duplicateMsgMap = new HashMap<>();
			boolean[] duplicateFound = { false };
			boolean[] negativeFound = { false };
			boolean[] blackFound = { false };
			boolean[] pepFound = { false };
			try {
				String sequenceValue = bIPS_CheckList_Repo.getSequence();

				BIPS_CheckListEntity checklist = new BIPS_CheckListEntity();
				checklist.setSrl_no(sequenceValue);
				checklist.setEntry_user(userID);
				checklist.setDel_flg("N");
				checklist.setEntry_time(new Date());
				
				List<String> phoneNumbers = new ArrayList<>();
				if (merPhNo != null)
					phoneNumbers.add(merPhNo);
				if (merPhNoR2 != null)
					phoneNumbers.add(merPhNoR2);
				if (merPhNoR3 != null)
					phoneNumbers.add(merPhNoR3);
				if (merPhNoR4 != null)
					phoneNumbers.add(merPhNoR4);
				if (merPhNoR5 != null)
					phoneNumbers.add(merPhNoR5);
				if (merPhNoR6 != null)
					phoneNumbers.add(merPhNoR6);
				if (merPhNoR7 != null)
					phoneNumbers.add(merPhNoR7);
				if (merPhNoR8 != null)
					phoneNumbers.add(merPhNoR8);
				if (merPhNoR9 != null)
					phoneNumbers.add(merPhNoR9);
				if (merPhNoR10 != null)
					phoneNumbers.add(merPhNoR10);
				String concatenatedPhoneNumbers = String.join(",", phoneNumbers);
				checklist.setMbl_num(concatenatedPhoneNumbers);
				System.out.println("Concatenated Phone Numbers: " + concatenatedPhoneNumbers);
				List<String> nationalId = new ArrayList<>();
				if (merNatId != null)
					nationalId.add(merNatId);
				if (merNatIdR2 != null)
					nationalId.add(merNatIdR2);
				if (merNatIdR3 != null)
					nationalId.add(merNatIdR3);
				if (merNatIdR4 != null)
					nationalId.add(merNatIdR4);
				if (merNatIdR5 != null)
					nationalId.add(merNatIdR5);
				if (merNatIdR6 != null)
					nationalId.add(merNatIdR6);
				if (merNatIdR7 != null)
					nationalId.add(merNatIdR7);
				if (merNatIdR8 != null)
					nationalId.add(merNatIdR8);
				if (merNatIdR9 != null)
					nationalId.add(merNatIdR9);
				if (merNatIdR10 != null)
					nationalId.add(merNatIdR10);
				String concatenatednationalId = String.join(",", nationalId);
				System.out.println("Concatenated Phone Numbers: " + concatenatednationalId);
				checklist.setNational_id(concatenatednationalId);
				;

				// Check for duplicates
				List<String> mobe_num_dup = dedup_Repo.findmbl_num();
				BiConsumer<String, String> checkDuplicate = (phoneNumber, paramName) -> {
					if (phoneNumber != null && mobe_num_dup.contains(phoneNumber)) {
						duplicateFound[0] = true;
						duplicateMsg.append("<tr><td>Mobile No</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};
				checkDuplicate.accept(merPhNo, "Phone No 1");
				checkDuplicate.accept(merPhNoR2, "Phone No 2");
				checkDuplicate.accept(merPhNoR3, "Phone No 3");
				checkDuplicate.accept(merPhNoR4, "Phone No 4");
				checkDuplicate.accept(merPhNoR5, "Phone No 5");
				checkDuplicate.accept(merPhNoR6, "Phone No 6");
				checkDuplicate.accept(merPhNoR7, "Phone No 7");
				checkDuplicate.accept(merPhNoR8, "Phone No 8");
				checkDuplicate.accept(merPhNoR9, "Phone No 9");
				checkDuplicate.accept(merPhNoR10, "Phone No 10");

				List<String> national_dup = dedup_Repo.findnational_id();

				BiConsumer<String, String> checkDuplicateNat = (phoneNumber, paramName) -> {
					if (phoneNumber != null && national_dup.contains(phoneNumber)) {
						duplicateFound[0] = true;
						duplicateMsg.append("<tr><td>National Id</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};

				checkDuplicateNat.accept(merNatId, "National Id 1");
				checkDuplicateNat.accept(merNatIdR2, "National Id 2");
				checkDuplicateNat.accept(merNatIdR3, "National Id 3");
				checkDuplicateNat.accept(merNatIdR4, "National Id 4");
				checkDuplicateNat.accept(merNatIdR5, "National Id 5");
				checkDuplicateNat.accept(merNatIdR6, "National Id 6");
				checkDuplicateNat.accept(merNatIdR7, "National Id 7");
				checkDuplicateNat.accept(merNatIdR8, "National Id 8");
				checkDuplicateNat.accept(merNatIdR9, "National Id 9");
				checkDuplicateNat.accept(merNatIdR10, "National Id 10");

				if (duplicateFound[0]) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(duplicateMsg).append("</tbody>").append("</table>");
					checklist.setDuplicate_check("YES");
					System.out.println(table.toString());
					bIPS_CheckList_Repo.save(checklist);
					response.put("duplicate", table.toString());

				} else {
					checklist.setDuplicate_check("YES");
					checklist.setRemark("No Match Found");
					checklist.setEntry_user("userID"); // Replace with actual user ID
					checklist.setDel_flg("N");
					checklist.setEntry_time(new Date());
					bIPS_CheckList_Repo.save(checklist);
					response.put("duplicate", "MATCH NOT FOUND");
				}
				// Check for negative list

				List<String> mobe_num_neg = Negative_Repo.findmbl_num();
				BiConsumer<String, String> checkNegative = (phoneNumber, paramName) -> {
					if (phoneNumber != null && mobe_num_neg.contains(phoneNumber)) {
						negativeFound[0] = true;
						negativeMsg.append("<tr><td>Mobile No</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};

				checkNegative.accept(merPhNo, "Phone No 1");
				checkNegative.accept(merPhNoR2, "Phone No 2");
				checkNegative.accept(merPhNoR3, "Phone No 3");
				checkNegative.accept(merPhNoR4, "Phone No 4");
				checkNegative.accept(merPhNoR5, "Phone No 5");
				checkNegative.accept(merPhNoR6, "Phone No 6");
				checkNegative.accept(merPhNoR7, "Phone No 7");
				checkNegative.accept(merPhNoR8, "Phone No 8");
				checkNegative.accept(merPhNoR9, "Phone No 9");
				checkNegative.accept(merPhNoR10, "Phone No 10");

				List<String> national_neg = Negative_Repo.findnational_id();

				BiConsumer<String, String> checkNegaiveNat = (phoneNumber, paramName) -> {
					if (phoneNumber != null && national_neg.contains(phoneNumber)) {
						negativeFound[0] = true;
						negativeMsg.append("<tr><td>National Id</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};

				checkNegaiveNat.accept(merNatId, "National Id 1");
				checkNegaiveNat.accept(merNatIdR2, "National Id 2");
				checkNegaiveNat.accept(merNatIdR3, "National Id 3");
				checkNegaiveNat.accept(merNatIdR4, "National Id 4");
				checkNegaiveNat.accept(merNatIdR5, "National Id 5");
				checkNegaiveNat.accept(merNatIdR6, "National Id 6");
				checkNegaiveNat.accept(merNatIdR7, "National Id 7");
				checkNegaiveNat.accept(merNatIdR8, "National Id 8");
				checkNegaiveNat.accept(merNatIdR9, "National Id 9");
				checkNegaiveNat.accept(merNatIdR10, "National Id 10");

				if (negativeFound[0]) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(negativeMsg).append("</tbody>").append("</table>");
					checklist.setNegative_check("YES");

					bIPS_CheckList_Repo.save(checklist);
					response.put("negative", table.toString());

				} else {
					checklist.setNegative_check("YES");
					checklist.setRemark("No Match Found");
					checklist.setEntry_user("userID"); // Replace with actual user ID
					checklist.setDel_flg("N");
					checklist.setEntry_time(new Date());
					bIPS_CheckList_Repo.save(checklist);
					response.put("negative", "MATCH NOT FOUND");
				}

				// black list found

				List<String> mob_no_blac = black_Repo.findmbl_num();
				BiConsumer<String, String> checkBlack = (phoneNumber, paramName) -> {
					if (phoneNumber != null && mob_no_blac.contains(phoneNumber)) {
						blackFound[0] = true;
						blackMsg.append("<tr><td>Mobile No</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};

				checkBlack.accept(merPhNo, "Phone No 1");
				checkBlack.accept(merPhNoR2, "Phone No 2");
				checkBlack.accept(merPhNoR3, "Phone No 3");
				checkBlack.accept(merPhNoR4, "Phone No 4");
				checkBlack.accept(merPhNoR5, "Phone No 5");
				checkBlack.accept(merPhNoR6, "Phone No 6");
				checkBlack.accept(merPhNoR7, "Phone No 7");
				checkBlack.accept(merPhNoR8, "Phone No 8");
				checkBlack.accept(merPhNoR9, "Phone No 9");
				checkBlack.accept(merPhNoR10, "Phone No 10");

				List<String> national_black = black_Repo.findnational_id();

				BiConsumer<String, String> checkPepNat = (phoneNumber, paramName) -> {
					if (phoneNumber != null && national_black.contains(phoneNumber)) {
						negativeFound[0] = true;
						negativeMsg.append("<tr><td>National Id</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};
				if (blackFound[0]) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(blackMsg).append("</tbody>").append("</table>");
					checklist.setBlack_check("YES");
					bIPS_CheckList_Repo.save(checklist);
					response.put("Black", table.toString());
				} else {
					checklist.setBlack_check("YES");
					checklist.setRemark("No Match Found");
					checklist.setEntry_user("userID"); // Replace with actual user ID
					checklist.setDel_flg("N");
					checklist.setEntry_time(new Date());
					bIPS_CheckList_Repo.save(checklist);
					response.put("Black", "MATCH NOT FOUND");
				}

				// pep list found

				List<String> mob_no_pep = Pep_Repo.findmbl_num();
				BiConsumer<String, String> checkPep = (phoneNumber, paramName) -> {
					if (phoneNumber != null && mob_no_pep.contains(phoneNumber)) {
						pepFound[0] = true;
						pepMsg.append("<tr><td>Mobile No</td><td>").append(phoneNumber).append("</td><td>")
								.append(phoneNumber).append("</td></tr>");
					}
				};

				checkPep.accept(merPhNo, "Phone No 1");
				checkPep.accept(merPhNoR2, "Phone No 2");
				checkPep.accept(merPhNoR3, "Phone No 3");
				checkPep.accept(merPhNoR4, "Phone No 4");
				checkPep.accept(merPhNoR5, "Phone No 5");
				checkPep.accept(merPhNoR6, "Phone No 6");
				checkPep.accept(merPhNoR7, "Phone No 7");
				checkPep.accept(merPhNoR8, "Phone No 8");
				checkPep.accept(merPhNoR9, "Phone No 9");
				checkPep.accept(merPhNoR10, "Phone No 10");
				if (pepFound[0]) {
					StringBuilder table = new StringBuilder();
					table.append("<table border='1' style='width:100%;text-align:center;'>").append(
							"<thead><tr><th>Match Type</th><th>Existing Value</th><th>Current Value</th></tr></thead>")
							.append("<tbody>").append(pepMsg).append("</tbody>").append("</table>");
					checklist.setPep_check("YES");

					bIPS_CheckList_Repo.save(checklist);
					response.put("Pep", table.toString());

				} else {
					checklist.setPep_check("YES");
					checklist.setRemark("No Match Found");
					checklist.setEntry_user("userID"); // Replace with actual user ID
					checklist.setDel_flg("N");
					checklist.setEntry_time(new Date());
					bIPS_CheckList_Repo.save(checklist);
					response.put("Pep", "MATCH NOT FOUND");
				}

				if (duplicateFound[0] && negativeFound[0] && blackFound[0] && pepFound[0]) {
					checklist.setRemark("Match Found");
				} else if (duplicateFound[0] || negativeFound[0] || blackFound[0] || pepFound[0]) {
					checklist.setRemark("Match Found");
				} else if (duplicateFound[0]) {
					checklist.setRemark("Duplicate Match Found");
				} else if (negativeFound[0]) {
					checklist.setRemark("Negative List Match Found");
				} else if (blackFound[0]) {
					checklist.setRemark("Black List Match Found");
				} else if (pepFound[0]) {
					checklist.setRemark("Pep List Match Found");
				} else {
					checklist.setRemark("No Match Found");
				}
				bIPS_CheckList_Repo.save(checklist);
			} catch (NullPointerException e) {
				response.put("error", "clear");
			}

			return response;
		}
		@GetMapping("/{uniqueId}")
		@ResponseBody
		public ResponseEntity<ByteArrayResource> viewOrDownloadFile(@PathVariable String uniqueId) {
			System.out.println("Unique ID: " + uniqueId);

			DocumentMaster_Entity document = documentMaster_Repo.findByUnique1(uniqueId);

			if (document != null) {
				byte[] documentContent = document.getUpd_file();
				String fileName = document.getFile_name();
				String fileType = determineFileType(fileName);

				// Log the file type
				System.out.println("File Type: " + fileType);

				ByteArrayResource resource = new ByteArrayResource(documentContent);
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
						.contentType(MediaType.parseMediaType(fileType)).contentLength(documentContent.length)
						.header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
						.header(HttpHeaders.PRAGMA, "no-cache").header(HttpHeaders.EXPIRES, "0").body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		}

		private String determineFileType(String fileName) {
			if (fileName.endsWith(".png")) {
				return "image/png";
			} else if (fileName.endsWith(".pdf")) {
				return "application/pdf";
			} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".jfif")) {
				return "image/jpeg";
			} else if (fileName.endsWith(".mp4")) {
				return "video/mp4";
			} else if (fileName.endsWith(".xls")) {
				return "application/vnd.ms-excel"; // MIME type for .xls
			} else if (fileName.endsWith(".xlsx")) {
				return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; // MIME type for .xlsx
			} else {
				return "application/octet-stream"; // Fallback MIME type
			}
		}

		@PostMapping(value = "/AddStudentss")
		@ResponseBody
		public String uploadSignatureAndPhoto(@RequestParam String merchant_Id, @RequestParam String srcURL)
				throws IOException, SQLException {

			String msg = null;
			try {
				Sign_Master_Entity signatureEntity = new Sign_Master_Entity();

				BigDecimal srlnoStr = Sign_Master_Repo.getSequence();
				signatureEntity.setS_no(srlnoStr);

				signatureEntity.setMerchant_Id(merchant_Id);
				;
				byte[] buff = srcURL.getBytes();
				Blob blob = new SerialBlob(buff);
				signatureEntity.setSign(blob);

				Sign_Master_Repo.save(signatureEntity);

				msg = "Signature details Uploaded successfully...";
			} catch (Exception e) {
				e.printStackTrace();
				msg = "Signature details Upload Unsuccessfull...";
			}

			return msg;
		}

		@RequestMapping(value = "getMerchantTerminalId", method = RequestMethod.GET)
		public List<String> getMerchantTerminalId(@RequestParam(required = false) String merchantId,
				@RequestParam(required = false) String unitId) {
			System.out.println(merchantId + "hhhhhhhhhhhhhhhhhhhhhhhh" + unitId);

			List<String> terminalIds = bIPS_MerDeviceManagement_Repo.getTerminalId(merchantId, unitId);

			System.out.println(terminalIds.toString());

			return terminalIds;
		}

}
