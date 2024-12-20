package com.bornfire.entity;

import java.math.BigDecimal;
import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "BIPS_SIGN_MASTER")
public class Sign_Master_Entity {
	@Lob
	private Blob sign;
	private String merchant_Id;
	@Id
	private BigDecimal s_no;

	
	public Blob getSign() {
		return sign;
	}
	public void setSign(Blob sign) {
		this.sign = sign;
	}
	public String getMerchant_Id() {
		return merchant_Id;
	}
	public void setMerchant_Id(String merchant_Id) {
		this.merchant_Id = merchant_Id;
	}
	public BigDecimal getS_no() {
		return s_no;
	}
	public void setS_no(BigDecimal s_no) {
		this.s_no = s_no;
	}
 
	
	public Sign_Master_Entity(Blob sign, String merchant_Id, BigDecimal s_no) {
		super();
		this.sign = sign;
		this.merchant_Id = merchant_Id;
		this.s_no = s_no;
	}
	public Sign_Master_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
