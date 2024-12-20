package com.bornfire.configuration;

import org.springframework.stereotype.Component;

@Component
public class ErrorResponseCod {

	public String ErrorCode(String code) {

		String responseDesc = null;

		if (code.equals("BOB0")) {
			responseDesc = "BOB0:Success";
		} else if (code.equals("BOB114")) {
			responseDesc = "AC01:Incorrect Account Number";
		} else if (code.equals("BOB116")) {
			responseDesc = "AM04:Insufficient Funds";
		} else if (code.equals("BOB119")) {
			responseDesc = "AG01:Transaction Forbidden";
		} else if (code.equals("AC04")) {
			responseDesc = "AC04:Closed Account Number";
		} else if (code.equals("AC06")) {
			responseDesc = "AC06:Blocked Account";
		} else if (code.equals("AM01")) {
			responseDesc = "AM01:Zero Amount";
		} else if (code.equals("AM02")) {
			responseDesc = "AM02:Not Allowed Amount";
		} else if (code.equals("RR01")) {
			responseDesc = "RR01:Missing Debtor Account or Identification";
		} else if (code.equals("AM11")) {
			responseDesc = "AM11:Invalid Transaction Currency";
		} else if (code.equals("AC03")) {
			// responseDesc = "AC03:Invalid Creditor Account Number";
			responseDesc = "AC03:Invalid Account Number";
		} else if (code.equals("AC01")) {
			responseDesc = "AC01:Incorrect Account Number";
		} else if (code.equals("AC13")) {
			responseDesc = "AC13:Invalid Account Type";
		} else if (code.equals("TRAN")) {
			responseDesc = "TRAN:Hours Closed";
		} else if (code.equals("HLDY")) {
			responseDesc = "HLDY:HLDYFLG ON";
		} else if (code.equals("INVD")) {
			responseDesc = "INVLD:INVALID TRAN TYPE";
		} else if (code.equals("NOIFT")) {
			responseDesc = "BOB400:NO TRANSACTION PRESENT PLEASE ENTER VALID TXID IN REMARKS";
		} else {
			responseDesc = "TECH:Technical Problem";
		}
		return responseDesc;
	}

}
