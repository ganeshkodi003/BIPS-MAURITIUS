package com.bornfire.entity;

import java.io.Serializable;

public class DocumentMaster_Entity_PrimaryId implements Serializable {

	private String merchant_id; 
	private String unique_id;
	
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	public String getUnique_id() {
		return unique_id;
	}
	public void setUnique_id(String unique_id) {
		this.unique_id = unique_id;
	}
	public DocumentMaster_Entity_PrimaryId() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
