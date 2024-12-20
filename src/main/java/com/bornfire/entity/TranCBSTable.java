package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="BIPS_TRAN_CBS_TABLE")
public class TranCBSTable {
	private String sequence_unique_id;
	@Id
	private String tran_audit_number;
	private String bob_account;
	private Date tran_date;
	private BigDecimal tran_amount;
	private String tran_type;
	private String tran_currency;
	private String tran_cbs_status;
	private String tran_cbs_status_error;
	private String entry_user;
	private Date entry_time;
	private String entity_cre_flg;
	private String settl_acct;
	private String settl_acct_type;
	private Date value_date;
	
	
	public TranCBSTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TranCBSTable(String tran_audit_number, String bob_account, Date tran_date, BigDecimal tran_amount,
			String tran_type, String tran_currency, String tran_cbs_status, String tran_cbs_status_error,
			String entry_user, Date entry_time, String entity_cre_flg) {
		super();
		this.tran_audit_number = tran_audit_number;
		this.bob_account = bob_account;
		this.tran_date = tran_date;
		this.tran_amount = tran_amount;
		this.tran_type = tran_type;
		this.tran_currency = tran_currency;
		this.tran_cbs_status = tran_cbs_status;
		this.tran_cbs_status_error = tran_cbs_status_error;
		this.entry_user = entry_user;
		this.entry_time = entry_time;
		this.entity_cre_flg = entity_cre_flg;
	}
	

	public String getSequence_unique_id() {
		return sequence_unique_id;
	}

	public void setSequence_unique_id(String sequence_unique_id) {
		this.sequence_unique_id = sequence_unique_id;
	}

	public String getTran_audit_number() {
		return tran_audit_number;
	}

	public void setTran_audit_number(String tran_audit_number) {
		this.tran_audit_number = tran_audit_number;
	}

	public String getBob_account() {
		return bob_account;
	}

	public void setBob_account(String bob_account) {
		this.bob_account = bob_account;
	}

	public Date getTran_date() {
		return tran_date;
	}

	public void setTran_date(Date tran_date) {
		this.tran_date = tran_date;
	}

	public BigDecimal getTran_amount() {
		return tran_amount;
	}

	public void setTran_amount(BigDecimal tran_amount) {
		this.tran_amount = tran_amount;
	}

	public String getTran_type() {
		return tran_type;
	}

	public void setTran_type(String tran_type) {
		this.tran_type = tran_type;
	}

	public String getTran_currency() {
		return tran_currency;
	}

	public void setTran_currency(String tran_currency) {
		this.tran_currency = tran_currency;
	}

	public String getTran_cbs_status() {
		return tran_cbs_status;
	}

	public void setTran_cbs_status(String tran_cbs_status) {
		this.tran_cbs_status = tran_cbs_status;
	}

	public String getTran_cbs_status_error() {
		return tran_cbs_status_error;
	}

	public void setTran_cbs_status_error(String tran_cbs_status_error) {
		this.tran_cbs_status_error = tran_cbs_status_error;
	}

	public String getEntry_user() {
		return entry_user;
	}

	public void setEntry_user(String entry_user) {
		this.entry_user = entry_user;
	}

	public Date getEntry_time() {
		return entry_time;
	}

	public void setEntry_time(Date entry_time) {
		this.entry_time = entry_time;
	}

	public String getEntity_cre_flg() {
		return entity_cre_flg;
	}

	public void setEntity_cre_flg(String entity_cre_flg) {
		this.entity_cre_flg = entity_cre_flg;
	}

	public String getSettl_acct() {
		return settl_acct;
	}

	public void setSettl_acct(String settl_acct) {
		this.settl_acct = settl_acct;
	}

	public String getSettl_acct_type() {
		return settl_acct_type;
	}

	public void setSettl_acct_type(String settl_acct_type) {
		this.settl_acct_type = settl_acct_type;
	}

	public Date getValue_date() {
		return value_date;
	}

	public void setValue_date(Date value_date) {
		this.value_date = value_date;
	}
	
	
}
