package com.bornfire.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
@Entity
@Table(name = "BIPS_DEDUP_LIST")	
public class BIPS_DeDupList {
	private String	corporatename;
	private String	brn;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private  Date	brn_date;
	private String	mbl_num;
	private String	brn_unit;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private  Date	brn_date_unit;
	private String	mbl_num_unit;
	
	private String	national_id;
	@Lob
	private String	remark;
	private String	entity_flag;
	private  Date	entry_time;
	private String	entry_user;
	private  Date	verify_time;
	private String	verify_user;
	@Id
	private String	srl_no;
	
	public String getSrl_no() {
		return srl_no;
	}
	public void setSrl_no(String srl_no) {
		this.srl_no = srl_no;
	}
	public String getCorporatename() {
		return corporatename;
	}
	public void setCorporatename(String corporatename) {
		this.corporatename = corporatename;
	}
	public String getBrn() {
		return brn;
	}
	public void setBrn(String brn) {
		this.brn = brn;
	}
	public Date getBrn_date() {
		return brn_date;
	}
	public void setBrn_date(Date brn_date) {
		this.brn_date = brn_date;
	}
	public String getMbl_num() {
		return mbl_num;
	}
	public void setMbl_num(String mbl_num) {
		this.mbl_num = mbl_num;
	}
	public String getBrn_unit() {
		return brn_unit;
	}
	public void setBrn_unit(String brn_unit) {
		this.brn_unit = brn_unit;
	}
	public Date getBrn_date_unit() {
		return brn_date_unit;
	}
	public void setBrn_date_unit(Date brn_date_unit) {
		this.brn_date_unit = brn_date_unit;
	}
	public String getMbl_num_unit() {
		return mbl_num_unit;
	}
	public void setMbl_num_unit(String mbl_num_unit) {
		this.mbl_num_unit = mbl_num_unit;
	}
	public String getNational_id() {
		return national_id;
	}
	public void setNational_id(String national_id) {
		this.national_id = national_id;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getEntity_flag() {
		return entity_flag;
	}
	public void setEntity_flag(String entity_flag) {
		this.entity_flag = entity_flag;
	}
	public Date getEntry_time() {
		return entry_time;
	}
	public void setEntry_time(Date entry_time) {
		this.entry_time = entry_time;
	}
	public String getEntry_user() {
		return entry_user;
	}
	public void setEntry_user(String entry_user) {
		this.entry_user = entry_user;
	}
	public Date getVerify_time() {
		return verify_time;
	}
	public void setVerify_time(Date verify_time) {
		this.verify_time = verify_time;
	}
	public String getVerify_user() {
		return verify_user;
	}
	public void setVerify_user(String verify_user) {
		this.verify_user = verify_user;
	}
	public BIPS_DeDupList(String corporatename, String brn, Date brn_date, String mbl_num, String brn_unit,
			Date brn_date_unit, String mbl_num_unit, String national_id, String remark, String entity_flag,
			Date entry_time, String entry_user, Date verify_time, String verify_user,String srl_no) {
		super();
		this.corporatename = corporatename;
		this.brn = brn;
		this.brn_date = brn_date;
		this.mbl_num = mbl_num;
		this.brn_unit = brn_unit;
		this.brn_date_unit = brn_date_unit;
		this.mbl_num_unit = mbl_num_unit;
		this.national_id = national_id;
		this.remark = remark;
		this.entity_flag = entity_flag;
		this.entry_time = entry_time;
		this.entry_user = entry_user;
		this.verify_time = verify_time;
		this.verify_user = verify_user;
		this.srl_no =srl_no;
	}
	
	@Override
	public String toString() {
		return "BIPS_DeDupList [corporatename=" + corporatename + ", brn=" + brn + ", brn_date=" + brn_date
				+ ", mbl_num=" + mbl_num + ", brn_unit=" + brn_unit + ", brn_date_unit=" + brn_date_unit
				+ ", mbl_num_unit=" + mbl_num_unit + ", national_id=" + national_id + ", remark=" + remark
				+ ", entity_flag=" + entity_flag + ", entry_time=" + entry_time + ", entry_user=" + entry_user
				+ ", verify_time=" + verify_time + ", verify_user=" + verify_user + ", srl_no=" + srl_no + "]";
	}
	public BIPS_DeDupList() {
		super();
		// TODO Auto-generated constructor stub
	}

}
