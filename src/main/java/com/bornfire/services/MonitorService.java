package com.bornfire.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bornfire.entity.AuditTablePojo;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;

@Component
public class MonitorService {

	@Autowired
	private IPSAuditTableRep iPSAuditTableRep;

	public List<AuditTablePojo> getAuditInquries(Date date1) {
		List<IPSAuditTable> auditList = iPSAuditTableRep.getauditListOpeartion(date1);
		List<AuditTablePojo> auditPojoList = new ArrayList<>();
		for (IPSAuditTable ipsAudit : auditList) {
			AuditTablePojo auditTablePojo = new AuditTablePojo();
			auditTablePojo.setAudit_date(ipsAudit.getAudit_date());
			auditTablePojo.setAudit_table(ipsAudit.getAudit_table());
			auditTablePojo.setFunc_code(ipsAudit.getFunc_code());
			auditTablePojo.setEntry_user(ipsAudit.getEntry_user());
			auditTablePojo.setEntry_time(ipsAudit.getEntry_time());
			auditTablePojo.setAuth_user(ipsAudit.getAuth_user());
			auditTablePojo.setRemarks(ipsAudit.getRemarks());
			List<String> fieldName = new ArrayList<String>();
			List<String> oldvalue = new ArrayList<String>();
			List<String> newvalue = new ArrayList<String>();
			String[] dd = ipsAudit.getModi_details().split("\\|\\|");
			for (String str : dd) {
				String[] str1 = str.split("\\+");
				if (str1.length > 0) {
					fieldName.add(str1[0]);
				}
				if (str1.length > 1) {
					oldvalue.add(str1[1]);
				}
				if (str1.length > 2) {
					newvalue.add(str1[2]);
				}
			}
			auditTablePojo.setFieldName(fieldName);
			auditTablePojo.setOldvalue(oldvalue);
			auditTablePojo.setNewvalue(newvalue);
			auditPojoList.add(auditTablePojo);
		}
		return auditPojoList;
	}

	public List<AuditTablePojo> getauditListLocal(Date fromdate1) {
		List<IPSAuditTable> auditList = iPSAuditTableRep.getauditListLocal(fromdate1);
		List<AuditTablePojo> auditPojoList = new ArrayList<>();

		for (IPSAuditTable ipsAudit : auditList) {
			boolean isUpdated = false;
			for (AuditTablePojo existingPojo : auditPojoList) {
				String auditRefNo = existingPojo.getAudit_ref_no();
				String remarks = existingPojo.getRemarks();
				String ipsAuditno = ipsAudit.getAudit_ref_no();
				if (auditRefNo != null && ipsAuditno != null
						&& existingPojo.getAudit_ref_no().equals(ipsAudit.getAudit_ref_no()) && remarks != null
						&& ("Login Successfully".equals(existingPojo.getRemarks())
								|| "Logout Successfully".equals(existingPojo.getRemarks()))) {
					existingPojo.setAudit_date(ipsAudit.getAudit_date());
					existingPojo.setAudit_table(ipsAudit.getAudit_table());
					existingPojo.setFunc_code(ipsAudit.getFunc_code());
					existingPojo.setEntry_user(ipsAudit.getEntry_user());
					existingPojo.setEntry_time(ipsAudit.getEntry_time());
					existingPojo.setAuth_user(ipsAudit.getAuth_user());
					existingPojo.setAuth_time(ipsAudit.getAuth_time());
					existingPojo.setRemarks(ipsAudit.getRemarks());
					List<String> fieldName = new ArrayList<>();
					List<String> oldvalue = new ArrayList<>();
					List<String> newvalue = new ArrayList<>();
					String[] dd = ipsAudit.getModi_details().split("\\|\\|");

					for (String str : dd) {
						String[] str1 = str.split("\\+");
						if (str1.length > 0) {
							fieldName.add(str1[0]);
						}
						if (str1.length > 1) {
							oldvalue.add(str1[1]);
						}
						if (str1.length > 2) {
							newvalue.add(str1[2]);
						}
					}
					existingPojo.setFieldName(fieldName);
					existingPojo.setOldvalue(oldvalue);
					existingPojo.setNewvalue(newvalue);
					isUpdated = true;
					break;
				}
			}

			// If no existing entry was updated, create a new one
			if (!isUpdated) {
				AuditTablePojo auditTablePojo = new AuditTablePojo();
				auditTablePojo.setAudit_date(ipsAudit.getAudit_date());
				auditTablePojo.setAudit_table(ipsAudit.getAudit_table());
				auditTablePojo.setFunc_code(ipsAudit.getFunc_code());
				auditTablePojo.setEntry_user(ipsAudit.getEntry_user());
				auditTablePojo.setEntry_time(ipsAudit.getEntry_time());
				auditTablePojo.setAuth_user(ipsAudit.getAuth_user());
				auditTablePojo.setRemarks(ipsAudit.getRemarks());

				List<String> fieldName = new ArrayList<>();
				List<String> oldvalue = new ArrayList<>();
				List<String> newvalue = new ArrayList<>();
				String[] dd = ipsAudit.getModi_details().split("\\|\\|");
				for (String str : dd) {
					String[] str1 = str.split("\\+");
					if (str1.length > 0) {
						fieldName.add(str1[0]);
					}
					if (str1.length > 1) {
						oldvalue.add(str1[1]);
					}
					if (str1.length > 2) {
						newvalue.add(str1[2]);
					}
				}
				auditTablePojo.setFieldName(fieldName);
				auditTablePojo.setOldvalue(oldvalue);
				auditTablePojo.setNewvalue(newvalue);
				auditPojoList.add(auditTablePojo);
			}
		}
		return auditPojoList;
	}

}
