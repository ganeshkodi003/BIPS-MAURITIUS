package com.bornfire.entity;

public class C24RequestAcount {

	private String SchmType;
	private String AcctNumber;
	private String SettlAcctNumber;

	public String getSchmType() {
		return SchmType;
	}

	public void setSchmType(String schmType) {
		SchmType = schmType;
	}

	public String getAcctNumber() {
		return AcctNumber;
	}

	public void setAcctNumber(String acctNumber) {
		AcctNumber = acctNumber;
	}

	public String getSettlAcctNumber() {
		return SettlAcctNumber;
	}

	public void setSettlAcctNumber(String settlAcctNumber) {
		SettlAcctNumber = settlAcctNumber;
	}

	public C24RequestAcount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public C24RequestAcount(String schmType, String acctNumber) {
		super();
		SchmType = schmType;
		AcctNumber = acctNumber;
	}
	
}
