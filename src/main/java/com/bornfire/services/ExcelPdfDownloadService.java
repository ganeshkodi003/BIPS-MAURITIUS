package com.bornfire.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class ExcelPdfDownloadService {

	@Autowired
	Environment env;

	@Autowired
	DataSource srcdataSource;

	public File getFile(String from_date, String to_date, String filetype)
			throws FileNotFoundException, JRException, SQLException {
		String directoryPath = env.getProperty("output.exportpath");
		String bank_address = env.getProperty("default.bank_address");
		if (directoryPath == null) {
			throw new FileNotFoundException("Export path not found in environment properties");
		}
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = currentDateTime.format(formatter);
		String fileName = "Transaction_List-" + formattedDate;
		String filePath;
		File outputFile;
		try {
			InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/bips_transaction.jrxml");
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper file not found at the specified path");
			}
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			HashMap<String, Object> map = new HashMap<>();
			map.put("TRAN_DATE1", from_date);
			map.put("TRAN_DATE2", to_date);
			map.put("DETAILED_ADDRESS", bank_address);
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			if (filetype.equalsIgnoreCase("pdf")) {
				fileName += ".pdf";
				filePath = Paths.get(directoryPath, fileName).toString();
				JasperExportManager.exportReportToPdfFile(jp, filePath);
			} else if (filetype.equalsIgnoreCase("xlsx")) {
				fileName += ".xlsx";
				filePath = Paths.get(directoryPath, fileName).toString();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
				exporter.exportReport();
			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filetype);
			}
		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating report", e);
		}
		outputFile = new File(filePath);
		if (!outputFile.exists()) {
			throw new FileNotFoundException("Generated file not found: " + filePath);
		}
		return outputFile;
	}

	public File getChargebackFile(String from_date, String to_date, String filetype, String formmode)
			throws FileNotFoundException, JRException, SQLException {
		String directoryPath = env.getProperty("output.exportpath");
		String bank_address = env.getProperty("default.bank_address");
		if (directoryPath == null) {
			throw new FileNotFoundException("Export path not found in environment properties");
		}
		String fileName;
		File outputFile;
		InputStream jasperFile;
		try {
			switch (formmode) {
			case "Revert":
				fileName = "REVERTED_CHARGEBACK_LIST_BETWEEN_" + from_date + "-" + to_date;
				jasperFile = getResourceAsStreamForChargeback(filetype, "/static/jasper/Chargeback2.jrxml");
				break;
			case "Pending":
				fileName = "PENDING_CHARGEBACK_LIST_BETWEEN_" + from_date + "-" + to_date;
				jasperFile = getResourceAsStreamForChargeback(filetype, "/static/jasper/Chargeback3.jrxml");
				break;
			default:
				fileName = "ALL_CHARGEBACK_LIST_BETWEEN_" + from_date + "-" + to_date;
				jasperFile = getResourceAsStreamForChargeback(filetype, "/static/jasper/Chargeback.jrxml");
				break;
			}
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper file not found at the specified path");
			}
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			HashMap<String, Object> map = new HashMap<>();
			map.put("TRAN_DATE1", from_date);
			map.put("TRAN_DATE2", to_date);
			map.put("DETAILED_ADDRESS", bank_address);
			String filePath;
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			if (filetype.equalsIgnoreCase("pdf")) {
				fileName += ".pdf";
				filePath = Paths.get(directoryPath, fileName).toString();
				JasperExportManager.exportReportToPdfFile(jp, filePath);
			} else if (filetype.equalsIgnoreCase("xlsx")) {
				fileName += ".xlsx";
				filePath = Paths.get(directoryPath, fileName).toString();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
				exporter.exportReport();
			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filetype);
			}
			outputFile = new File(filePath);
			if (!outputFile.exists()) {
				throw new FileNotFoundException("Generated file not found: " + filePath);
			}
			return outputFile;
		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating report", e);
		}
	}

	private InputStream getResourceAsStreamForChargeback(String filetype, String path) {
		return this.getClass().getResourceAsStream(path);
	}

	public File getAuditFile(String from_date, String to_date, String filetype)
			throws FileNotFoundException, JRException, SQLException {
		return generateReport("AUDIT_LIST", "/static/jasper/AuditDetails.jrxml", from_date, to_date, filetype);
	}

	public File getAuditOperationFile(String from_date, String to_date, String filetype)
			throws FileNotFoundException, JRException, SQLException {
		return generateReport("AUDIT_OPERATION_LIST-" + from_date + "-" + to_date,
				"/static/jasper/AuditOperationDetails.jrxml", from_date, to_date, filetype);
	}

	private File generateReport(String baseFileName, String jasperPath, String from_date, String to_date,
			String filetype) throws FileNotFoundException, JRException, SQLException {
		String directoryPath = env.getProperty("output.exportpath");
		if (directoryPath == null) {
			throw new FileNotFoundException("Export path not found in environment properties");
		}

		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = currentDateTime.format(formatter);
		String fileName = baseFileName + "-" + formattedDate;
		File outputFile;

		try {
			InputStream jasperFile = this.getClass().getResourceAsStream(jasperPath);
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper file not found at the specified path: " + jasperPath);
			}

			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			HashMap<String, Object> map = new HashMap<>();
			map.put("AUDIT_DATE1", from_date);
			map.put("AUDIT_DATE2", to_date);

			String filePath;
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());

			if (filetype.equalsIgnoreCase("pdf")) {
				fileName += ".pdf";
				filePath = Paths.get(directoryPath, fileName).toString();
				JasperExportManager.exportReportToPdfFile(jp, filePath);

			} else if (filetype.equalsIgnoreCase("xlsx")) {
				fileName += ".xlsx";
				filePath = Paths.get(directoryPath, fileName).toString();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
				exporter.exportReport();

			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filetype);
			}

			outputFile = new File(filePath);
			if (!outputFile.exists()) {
				throw new FileNotFoundException("Generated file not found: " + filePath);
			}

			return outputFile;

		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating report", e);
		}
	}

	public File getTotalTxnFile(String from_date, String to_date, String filetype, String merchant_id, String unit_id,
			String user_id, String device_id) throws FileNotFoundException, JRException, SQLException {
		String directoryPath = env.getProperty("output.exportpath");
		String bank_address = env.getProperty("default.bank_address");
		if (directoryPath == null) {
			throw new FileNotFoundException("Export path not found in environment properties");
		}
		String fileName = "Transaction_List-" + from_date + " to " + to_date;
		String filePath;
		File outputFile;
		try {
			InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/TransactionReports/TransactionTotalList.jrxml");
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper file not found at the specified path");
			}
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			HashMap<String, Object> map = new HashMap<>();
			map.put("TRAN_DATE1", from_date);
			map.put("TRAN_DATE2", to_date);
			map.put("MERCHANT_ID", merchant_id);
			map.put("UNIT_ID", unit_id);
			map.put("USER_ID", user_id);
			map.put("DEVICE_ID", device_id);
			map.put("DETAILED_ADDRESS", bank_address);
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			if (filetype.equalsIgnoreCase("pdf")) {
				fileName += ".pdf";
				filePath = Paths.get(directoryPath, fileName).toString();
				JasperExportManager.exportReportToPdfFile(jp, filePath);
			} else if (filetype.equalsIgnoreCase("xlsx")) {
				fileName += ".xlsx";
				filePath = Paths.get(directoryPath, fileName).toString();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
				exporter.exportReport();
			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filetype);
			}
		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating report", e);
		}
		outputFile = new File(filePath);
		if (!outputFile.exists()) {
			throw new FileNotFoundException("Generated file not found: " + filePath);
		}
		return outputFile;
	}

	public File getTotalChargebackFile(String from_date, String to_date, String filetype, String merchant_id,
			String unit_id, String user_id, String device_id) throws FileNotFoundException, JRException, SQLException {
		String directoryPath = env.getProperty("output.exportpath");
		String bank_address = env.getProperty("default.bank_address");
		System.out.println(merchant_id + unit_id + user_id + device_id + from_date + to_date);
		if (directoryPath == null) {
			throw new FileNotFoundException("Export path not found in environment properties");
		}
		String fileName = "Chargeback_List-" + from_date + " to " + to_date;
		String filePath;
		File outputFile;
		try {
			InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/ChargebackReports/ChargebackTotalList.jrxml");
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper file not found at the specified path");
			}
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			HashMap<String, Object> map = new HashMap<>();
			map.put("TRAN_DATE1", from_date);
			map.put("TRAN_DATE2", to_date);
			map.put("MERCHANT_ID", merchant_id);
			map.put("UNIT_ID", unit_id);
			map.put("USER_ID", user_id);
			map.put("DEVICE_ID", device_id);
			map.put("DETAILED_ADDRESS", bank_address);
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			if (filetype.equalsIgnoreCase("pdf")) {
				fileName += ".pdf";
				filePath = Paths.get(directoryPath, fileName).toString();
				JasperExportManager.exportReportToPdfFile(jp, filePath);
			} else if (filetype.equalsIgnoreCase("xlsx")) {
				fileName += ".xlsx";
				filePath = Paths.get(directoryPath, fileName).toString();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
				exporter.exportReport();
			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filetype);
			}
		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating report", e);
		}
		outputFile = new File(filePath);
		if (!outputFile.exists()) {
			throw new FileNotFoundException("Generated file not found: " + filePath);
		}
		return outputFile;
	}

	// New Method Download (07-11-2024)
	
	// User Activity Audit
	
	public File getUserAuditFileRecord(String from_date, String filetype)
			throws FileNotFoundException, JRException, SQLException {
		return generateAuditReport("AUDIT_OPERATION_LIST_" + from_date, "/static/jasper/UserAudit/AuditOperationDetails.jrxml",
				from_date, filetype);
	}
	
	// User Service Audit

	public File getAuditOperationFileRecord(String from_date, String filetype)
			throws FileNotFoundException, JRException, SQLException {
		return generateAuditReport("AUDIT_OPERATION_LIST_" + from_date, "/static/jasper/ServiceAudit/AuditDetails.jrxml",
				from_date, filetype);
	}

	private File generateAuditReport(String baseFileName, String jasperPath, String from_date, String filetype)
			throws FileNotFoundException, JRException, SQLException {
		
		String directoryPath = env.getProperty("output.exportpath");
		if (directoryPath == null) {
			throw new FileNotFoundException("Export path not found in environment properties");
		}
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = currentDateTime.format(formatter);
		String fileName = baseFileName + "-" + formattedDate;
		File outputFile;
		try {
			InputStream jasperFile = this.getClass().getResourceAsStream(jasperPath);
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper file not found at the specified path: " + jasperPath);
			}
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			HashMap<String, Object> map = new HashMap<>();
			map.put("AUDIT_DATE1", from_date);

			String filePath;
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());

			if (filetype.equalsIgnoreCase("pdf")) {
				fileName += ".pdf";
				filePath = Paths.get(directoryPath, fileName).toString();
				JasperExportManager.exportReportToPdfFile(jp, filePath);

			} else if (filetype.equalsIgnoreCase("xlsx")) {
				fileName += ".xlsx";
				filePath = Paths.get(directoryPath, fileName).toString();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
				exporter.exportReport();

			} else {
				throw new IllegalArgumentException("Unsupported file type: " + filetype);
			}

			outputFile = new File(filePath);
			if (!outputFile.exists()) {
				throw new FileNotFoundException("Generated file not found: " + filePath);
			}

			return outputFile;

		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating report", e);
		}
	}
	
	public File getUnitFile(String merchantUserId, String fileType) throws FileNotFoundException, JRException, SQLException {
	    String directoryPath = env.getProperty("output.exportpath");

	    if (directoryPath == null) {
	        throw new FileNotFoundException("Export path not found in environment properties");
	    }

	    String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	    String fileName = "Unit_List-" + formattedDate + (fileType.equalsIgnoreCase("pdf") ? ".pdf" : ".xlsx");
	    String filePath = Paths.get(directoryPath, fileName).toString();

	    try (InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/MerchantMaster_Unit_User_Device/UnitList.jrxml")) {
	        if (jasperFile == null) {
	            throw new FileNotFoundException("Jasper file not found at the specified path");
	        }

	        JasperReport jasperReport = JasperCompileManager.compileReport(jasperFile);

	        HashMap<String, Object> parameters = new HashMap<>();
	        parameters.put("merchant_user_id", merchantUserId);

	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, srcdataSource.getConnection());

	        if (fileType.equalsIgnoreCase("pdf")) {
	            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
	        } else if (fileType.equalsIgnoreCase("excel")) {
	            JRXlsxExporter exporter = new JRXlsxExporter();
	            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
	            exporter.exportReport();
	        } else {
	            throw new IllegalArgumentException("Unsupported file type: " + fileType);
	        }

	        File outputFile = new File(filePath);
	        if (!outputFile.exists()) {
	            throw new FileNotFoundException("Generated file not found: " + filePath);
	        }

	        return outputFile;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error generating the report", e);
	    }
	}
	
	
	public File getUserFile(String merchantUserId, String fileType) throws FileNotFoundException, JRException, SQLException {
	    String directoryPath = env.getProperty("output.exportpath");

	    if (directoryPath == null) {
	        throw new FileNotFoundException("Export path not found in environment properties");
	    }

	    String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	    String fileName = "User_List-" + formattedDate + (fileType.equalsIgnoreCase("pdf") ? ".pdf" : ".xlsx");
	    String filePath = Paths.get(directoryPath, fileName).toString();

	    try (InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/MerchantMaster_Unit_User_Device/UserList.jrxml")) {
	        if (jasperFile == null) {
	            throw new FileNotFoundException("Jasper file not found at the specified path");
	        }

	        JasperReport jasperReport = JasperCompileManager.compileReport(jasperFile);

	        HashMap<String, Object> parameters = new HashMap<>();
	        parameters.put("merchant_user_id", merchantUserId);

	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, srcdataSource.getConnection());

	        if (fileType.equalsIgnoreCase("pdf")) {
	            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
	        } else if (fileType.equalsIgnoreCase("excel")) {
	            JRXlsxExporter exporter = new JRXlsxExporter();
	            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
	            exporter.exportReport();
	        } else {
	            throw new IllegalArgumentException("Unsupported file type: " + fileType);
	        }

	        File outputFile = new File(filePath);
	        if (!outputFile.exists()) {
	            throw new FileNotFoundException("Generated file not found: " + filePath);
	        }

	        return outputFile;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error generating the report", e);
	    }
	}

	
	public File getDeviceFile(String merchantUserId, String fileType) throws FileNotFoundException, JRException, SQLException {
	    String directoryPath = env.getProperty("output.exportpath");

	    if (directoryPath == null) {
	        throw new FileNotFoundException("Export path not found in environment properties");
	    }

	    String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	    String fileName = "Device_List-" + formattedDate + (fileType.equalsIgnoreCase("pdf") ? ".pdf" : ".xlsx");
	    String filePath = Paths.get(directoryPath, fileName).toString();

	    try (InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/MerchantMaster_Unit_User_Device/DeviceList.jrxml")) {
	        if (jasperFile == null) {
	            throw new FileNotFoundException("Jasper file not found at the specified path");
	        }

	        JasperReport jasperReport = JasperCompileManager.compileReport(jasperFile);

	        HashMap<String, Object> parameters = new HashMap<>();
	        parameters.put("merchant_user_id", merchantUserId);

	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, srcdataSource.getConnection());

	        if (fileType.equalsIgnoreCase("pdf")) {
	            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
	        } else if (fileType.equalsIgnoreCase("excel")) {
	            JRXlsxExporter exporter = new JRXlsxExporter();
	            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePath));
	            exporter.exportReport();
	        } else {
	            throw new IllegalArgumentException("Unsupported file type: " + fileType);
	        }

	        File outputFile = new File(filePath);
	        if (!outputFile.exists()) {
	            throw new FileNotFoundException("Generated file not found: " + filePath);
	        }

	        return outputFile;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error generating the report", e);
	    }
	}

}
