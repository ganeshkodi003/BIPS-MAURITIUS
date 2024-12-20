package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "BIPS_FEESCHARGE")
public class BIPS_Fees_Charges_Entity {

	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date	transaction_date;
	@Id
	private String	message_ref;
	private String	audit_ref;
	private String	bank;
	private String	remitter_account;
	private String	currency;
	private BigDecimal	amount;
	private BigDecimal	transaction_fees;
	private String	applied_flag;
	public Date getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(Date transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getMessage_ref() {
		return message_ref;
	}
	public void setMessage_ref(String message_ref) {
		this.message_ref = message_ref;
	}
	public String getAudit_ref() {
		return audit_ref;
	}
	public void setAudit_ref(String audit_ref) {
		this.audit_ref = audit_ref;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getRemitter_account() {
		return remitter_account;
	}
	public void setRemitter_account(String remitter_account) {
		this.remitter_account = remitter_account;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getTransaction_fees() {
		return transaction_fees;
	}
	public void setTransaction_fees(BigDecimal transaction_fees) {
		this.transaction_fees = transaction_fees;
	}
	public String getApplied_flag() {
		return applied_flag;
	}
	public void setApplied_flag(String applied_flag) {
		this.applied_flag = applied_flag;
	}
	public BIPS_Fees_Charges_Entity(Date transaction_date, String message_ref, String audit_ref, String bank,
			String remitter_account, String currency, BigDecimal amount, BigDecimal transaction_fees,
			String applied_flag) {
		super();
		this.transaction_date = transaction_date;
		this.message_ref = message_ref;
		this.audit_ref = audit_ref;
		this.bank = bank;
		this.remitter_account = remitter_account;
		this.currency = currency;
		this.amount = amount;
		this.transaction_fees = transaction_fees;
		this.applied_flag = applied_flag;
	}
	public BIPS_Fees_Charges_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
