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
	private String merchant_id;
	@Id
	private BigDecimal s_no;
	private String merchant_name_sign;
	public Blob getSign() {
		return sign;
	}
	public void setSign(Blob sign) {
		this.sign = sign;
	}
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	public BigDecimal getS_no() {
		return s_no;
	}
	public void setS_no(BigDecimal s_no) {
		this.s_no = s_no;
	}
	public String getMerchant_name_sign() {
		return merchant_name_sign;
	}
	public void setMerchant_name_sign(String merchant_name_sign) {
		this.merchant_name_sign = merchant_name_sign;
	}
	public Sign_Master_Entity(Blob sign, String merchant_id, BigDecimal s_no, String merchant_name_sign) {
		super();
		this.sign = sign;
		this.merchant_id = merchant_id;
		this.s_no = s_no;
		this.merchant_name_sign = merchant_name_sign;
	}
	public Sign_Master_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}
  
	
}
