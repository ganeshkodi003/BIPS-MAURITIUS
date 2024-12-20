package com.bornfire.entity;

import java.io.Serializable;

import javax.persistence.Id;


public class NotificationParmsID implements Serializable {

/**
	 * 
	 */
private static final long serialVersionUID = 1L;
@Id	
	private String RECORD_SRL_NO;

public String getRECORD_SRL_NO() {
	return RECORD_SRL_NO;
}

public void setRECORD_SRL_NO(String rECORD_SRL_NO) {
	RECORD_SRL_NO = rECORD_SRL_NO;
}





}
