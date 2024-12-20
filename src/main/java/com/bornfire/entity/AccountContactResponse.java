package com.bornfire.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountContactResponse {

	private String status;
	private String error;
	private List<String> error_desc;
	private String docName;
	private String docNumber;
	private String countryCode;
	private String phoneNumber;
	private String schmType;
	private String accountStatus;
	private String currencyCode;
	private String frezCode;
	private String accountName;
	// private String hldyFlg;

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getFrezCode() {
		return frezCode;
	}

	public void setFrezCode(String frezCode) {
		this.frezCode = frezCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<String> getError_desc() {
		return error_desc;
	}

	public void setError_desc(List<String> error_desc) {
		this.error_desc = error_desc;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSchmType() {
		return schmType;
	}

	public void setSchmType(String schmType) {
		this.schmType = schmType;
	}

	@Override
	public String toString() {
		return "AccountContactResponse [status=" + status + ", error=" + error + ", error_desc=" + error_desc
				+ ", docName=" + docName + ", docNumber=" + docNumber + ", countryCode=" + countryCode
				+ ", phoneNumber=" + phoneNumber + ", schmType=" + schmType + ", accountStatus=" + accountStatus
				+ ", currencyCode=" + currencyCode + ", frezCode=" + frezCode + ", getAccountStatus()="
				+ getAccountStatus() + ", getCurrencyCode()=" + getCurrencyCode() + ", getFrezCode()=" + getFrezCode()
				+ ", getStatus()=" + getStatus() + ", getError()=" + getError() + ", getError_desc()=" + getError_desc()
				+ ", getDocName()=" + getDocName() + ", getDocNumber()=" + getDocNumber() + ", getCountryCode()="
				+ getCountryCode() + ", getPhoneNumber()=" + getPhoneNumber() + ", getSchmType()=" + getSchmType()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

	public AccountContactResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountContactResponse(String status, String error, List<String> error_desc, String docName,
			String docNumber, String countryCode, String phoneNumber, String schmType, String accountStatus,
			String currencyCode, String frezCode,String accountName) {
		super();
		this.status = status;
		this.error = error;
		this.error_desc = error_desc;
		this.docName = docName;
		this.docNumber = docNumber;
		this.countryCode = countryCode;
		this.phoneNumber = phoneNumber;
		this.schmType = schmType;
		this.accountStatus = accountStatus;
		this.currencyCode = currencyCode;
		this.frezCode = frezCode;
		this.accountName = accountName;
	}

	public AccountContactResponse(String string, String serverErrorCode, List<String> singletonList) {
		// TODO Auto-generated constructor stub
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

}
