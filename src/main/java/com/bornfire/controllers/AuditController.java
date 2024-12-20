package com.bornfire.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bornfire.entity.IPSAuditTableRep;
import com.bornfire.services.ExcelPdfDownloadService;

@RestController
public class AuditController {

	private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	@Autowired
	ExcelPdfDownloadService excelPdfDownloadService;
	
	// Service Audit

	@RequestMapping(value = "serviceAuditRecordCount", method = RequestMethod.GET)
	public int DownloadAuditOperationDateChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date)
			throws IOException, SQLException, ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		int count = ipsAuditTableRep.getAuditTableRecordCount(formatter.parse(from_date));
		logger.info("Audit Record Cound :" + count);
		return count;
	}
	
	@RequestMapping(value = "AuditOperationDownloadRecord", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> AuditOperationDownload(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {

		try {
			File repfile = excelPdfDownloadService.getAuditOperationFileRecord(from_date, filetype);
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

	// User Activity Audit

	@RequestMapping(value = "userActivityAuditCount", method = RequestMethod.GET)
	public int DownloadAuditDateChecking(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date)
			throws IOException, SQLException, ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		int count = ipsAuditTableRep.getUserAuditCount(formatter.parse(from_date));
		return count;
	}

	@RequestMapping(value = "UserAuditDownloadRecord", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> UserAuditDownloadRecord(HttpServletResponse response,
			@RequestParam(value = "from_date", required = false) String from_date,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {

		try {
			File repfile = excelPdfDownloadService.getUserAuditFileRecord(from_date, filetype);
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

}
