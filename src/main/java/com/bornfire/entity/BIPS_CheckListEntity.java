package com.bornfire.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "BIPS_CHECK_LIST")	
public class BIPS_CheckListEntity {
	private String	corporatename;
	private String	brn;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date	brn_date;
	private String	mbl_num;
	private String	brn_unit;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date	brn_date_unit;
	private String	mbl_num_unit;
	private String	national_id;
	private String	remark;
	private String	duplicate_check;
	private String	negative_check;
	private String	pep_check;
	private String	black_check;
	private String	entity_flag;
	private Date	entry_time;
	private String	entry_user;
	private Date	verify_time;
	private String	verify_user;
	@Id
	private String	srl_no;
	private String	del_flg;
	
	public String getDel_flg() {
		return del_flg;
	}
	public void setDel_flg(String del_flg) {
		this.del_flg = del_flg;
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
	public String getDuplicate_check() {
		return duplicate_check;
	}
	public void setDuplicate_check(String duplicate_check) {
		this.duplicate_check = duplicate_check;
	}
	public String getNegative_check() {
		return negative_check;
	}
	public void setNegative_check(String negative_check) {
		this.negative_check = negative_check;
	}
	public String getPep_check() {
		return pep_check;
	}
	public void setPep_check(String pep_check) {
		this.pep_check = pep_check;
	}
	public String getBlack_check() {
		return black_check;
	}
	public void setBlack_check(String black_check) {
		this.black_check = black_check;
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
	public String getSrl_no() {
		return srl_no;
	}
	public void setSrl_no(String srl_no) {
		this.srl_no = srl_no;
	}
	public BIPS_CheckListEntity(String corporatename, String brn, Date brn_date, String mbl_num, String brn_unit,
			Date brn_date_unit, String mbl_num_unit, String national_id, String remark, String duplicate_check,
			String negative_check, String pep_check, String black_check, String entity_flag, Date entry_time,
			String entry_user, Date verify_time, String verify_user, String srl_no , String del_flg) {
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
		this.duplicate_check = duplicate_check;
		this.negative_check = negative_check;
		this.pep_check = pep_check;
		this.black_check = black_check;
		this.entity_flag = entity_flag;
		this.entry_time = entry_time;
		this.entry_user = entry_user;
		this.verify_time = verify_time;
		this.verify_user = verify_user;
		this.srl_no = srl_no;
		this.del_flg =del_flg;
	}
	public BIPS_CheckListEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
