package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "BIPS_RATEMAINT")
public class BIPS_Rate_Maint_Entity {
	
	private String	billing_currency;
	private String	settlement_currency;
	private BigDecimal	rate;
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date	effective_date;
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date	audit_date;
	@Id
	private String	srl;
	public String getBilling_currency() {
		return billing_currency;
	}
	public void setBilling_currency(String billing_currency) {
		this.billing_currency = billing_currency;
	}
	public String getSettlement_currency() {
		return settlement_currency;
	}
	public void setSettlement_currency(String settlement_currency) {
		this.settlement_currency = settlement_currency;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public Date getEffective_date() {
		return effective_date;
	}
	public void setEffective_date(Date effective_date) {
		this.effective_date = effective_date;
	}
	public Date getAudit_date() {
		return audit_date;
	}
	public void setAudit_date(Date audit_date) {
		this.audit_date = audit_date;
	}
	public String getSrl() {
		return srl;
	}
	public void setSrl(String srl) {
		this.srl = srl;
	}
	public BIPS_Rate_Maint_Entity(String billing_currency, String settlement_currency, BigDecimal rate,
			Date effective_date, Date audit_date, String srl) {
		super();
		this.billing_currency = billing_currency;
		this.settlement_currency = settlement_currency;
		this.rate = rate;
		this.effective_date = effective_date;
		this.audit_date = audit_date;
		this.srl = srl;
	}
	public BIPS_Rate_Maint_Entity() {
		super();
		// TODO Auto-generated constructor stub
	} 
}
