package com.bornfire.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.CIMMerchantQRAddlInfo;
import com.bornfire.entity.CIMMerchantQRcodeAcctInfo;
import com.bornfire.entity.CIMMerchantQRcodeRequest;
import com.bornfire.entity.MerchantQRRegistration; 
import com.bornfire.entity.TranCimCBSTable;
import com.bornfire.entity.cimMerchantQRcodeResponse;
import com.google.gson.Gson;

@Component
public class IPSServices {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	@Autowired
	SequenceGenerator sequence;

	public String ManualDebitReversalTransaction(TranCimCBSTable tranCBStable, String userID)
			throws UnknownHostException {

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());

		HttpEntity<TranCimCBSTable> entity = new HttpEntity<>(tranCBStable, httpHeaders);
		ResponseEntity<String> response = null;
		try {

			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "api/ws/" + userID + "/reverseDebit",
					entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return "Reversal Transaction Successfully";
			} else {
				return "Something went wrong at server end";
			}

		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				//System.out.println("dfjhgkuy");
				return "Something went wrong at server end";

			} else {
				return "Transaction Failed";
			}

		} catch (HttpServerErrorException ex) {
			return "Something went wrong at server end";
		}

	}

	public String ManualBulkCreditReversalTransaction(TranCimCBSTable tranCBStable, String userID)
			throws UnknownHostException {

		//System.out.println("IPSServices");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());

		HttpEntity<TranCimCBSTable> entity = new HttpEntity<>(tranCBStable, httpHeaders);
		ResponseEntity<String> response = null;
		try {

			response = restTemplate.postForEntity(
					env.getProperty("ipsx.url") + "api/ws/" + userID + "/reverseBulkCredit", entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return "Reversal Transaction Successfully";
			} else {
				return "Something went wrong at server end";
			}

		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				//System.out.println("dfjhgkuy");
				return "Something went wrong at server end";

			} else {
				return "Transaction Failed";
			}

		} catch (HttpServerErrorException ex) {
			return "Something went wrong at server end";
		}

	}

	public String ManualBulkDebitReversalTransaction(TranCimCBSTable tranCBStable, String userID)
			throws UnknownHostException {
		//System.out.println("IPSServices");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());

		HttpEntity<TranCimCBSTable> entity = new HttpEntity<>(tranCBStable, httpHeaders);
		ResponseEntity<String> response = null;
		try {

			response = restTemplate.postForEntity(
					env.getProperty("ipsx.url") + "api/ws/" + userID + "/reverseBulkDebit", entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return "Reversal Transaction Successfully";
			} else {
				return "Something went wrong at server end";
			}

		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				//System.out.println("dfjhgkuy");
				return "Something went wrong at server end";

			} else {
				return "Transaction Failed";
			}

		} catch (HttpServerErrorException ex) {
			return "Something went wrong at server end";
		}

	}

	public String ManualCreditReversalTransaction(TranCimCBSTable tranCBStable, String userID)
			throws UnknownHostException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());

		HttpEntity<TranCimCBSTable> entity = new HttpEntity<>(tranCBStable, httpHeaders);
		ResponseEntity<String> response = null;
		try {

			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "api/ws/" + userID + "/reverseCredit",
					entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return "Reversal Transaction Successfully";
			} else {
				return "Something went wrong at server end";
			}

		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				return "Something went wrong at server end";

			} else {
				return "Transaction Failed";
			}

		} catch (HttpServerErrorException ex) {
			return "Something went wrong at server end";
		}

	}
 
	
	public ResponseEntity<cimMerchantQRcodeResponse> getMerchantQrCodeStr(MerchantQRRegistration merchantQRgenerator) 
			throws UnknownHostException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		httpHeaders.set("Merchant_ID", merchantQRgenerator.getMerchant_id());
		
		CIMMerchantQRcodeRequest  cimMerchantQRcodeRequest=new CIMMerchantQRcodeRequest();
		System.out.println(merchantQRgenerator.getPayload_format_indicator().toString());
		cimMerchantQRcodeRequest.setPayloadFormatIndiator(merchantQRgenerator.getPayload_format_indicator().toString());
		cimMerchantQRcodeRequest.setPointOfInitiationFormat(merchantQRgenerator.getPoi_method().toString());
	
		CIMMerchantQRcodeAcctInfo merchantQRAcctInfo=new CIMMerchantQRcodeAcctInfo();
		merchantQRAcctInfo.setGlobalID(merchantQRgenerator.getGlobal_unique_id());
		merchantQRAcctInfo.setPayeeParticipantCode(merchantQRgenerator.getPayee_participant_code());
		merchantQRAcctInfo.setMerchantAcctNumber(merchantQRgenerator.getMerchant_id());
		merchantQRAcctInfo.setMerchantID(merchantQRgenerator.getMerchant_id());
		cimMerchantQRcodeRequest.setMerchantAcctInformation(merchantQRAcctInfo);
		
		cimMerchantQRcodeRequest.setMCC(merchantQRgenerator.getMerchant_category_code().toString());
		cimMerchantQRcodeRequest.setCurrency(merchantQRgenerator.getTransaction_crncy().toString());
		
		
		if(!String.valueOf(merchantQRgenerator.getTransaction_amt()).equals("null")&&
				!String.valueOf(merchantQRgenerator.getTransaction_amt()).equals("")) {
			cimMerchantQRcodeRequest.setTrAmt(merchantQRgenerator.getTransaction_amt().toString());
		}
		
		
		/*if(merchantQRgenerator.getTip_or_conv_indicator().toString().equals("0")){
			cimMerchantQRcodeRequest.setConvenienceIndicator(false);
		}else {
			cimMerchantQRcodeRequest.setConvenienceIndicator(true);
			cimMerchantQRcodeRequest.setConvenienceIndicatorFeeType(merchantQRgenerator.getConv_fees_type().toString());
			cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());
		}*/
		
		if(merchantQRgenerator.getTip_or_conv_indicator()!=null) {
			if(!merchantQRgenerator.getTip_or_conv_indicator().toString().equals("")){

				if(merchantQRgenerator.getTip_or_conv_indicator().toString().equals("01")) {
					cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("01");
				}else if(merchantQRgenerator.getTip_or_conv_indicator().toString().equals("02")) {
					if(merchantQRgenerator.getConv_fees_type().equals("Fixed")) {
						cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("02");
						cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());
					}else if(merchantQRgenerator.getConv_fees_type().equals("Percentage")) {
						cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("03");
						cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());

					}
				}
				
			}
		}
		
		
		cimMerchantQRcodeRequest.setCountryCode(merchantQRgenerator.getCountry().toString());
		cimMerchantQRcodeRequest.setMerchantName(merchantQRgenerator.getMerchant_name().toString());
		cimMerchantQRcodeRequest.setCity(merchantQRgenerator.getCity().toString());
		
		if(!String.valueOf(merchantQRgenerator.getZip_code()).equals("null")&&
				!String.valueOf(merchantQRgenerator.getZip_code()).equals("")) {
			cimMerchantQRcodeRequest.setPostalCode(merchantQRgenerator.getZip_code().toString());
		}
		
		
		
		CIMMerchantQRAddlInfo cimMercbantQRAddlInfo=new CIMMerchantQRAddlInfo();
		cimMercbantQRAddlInfo.setBillNumber(merchantQRgenerator.getBill_number());
		cimMercbantQRAddlInfo.setMobileNumber(merchantQRgenerator.getMobile());
		cimMercbantQRAddlInfo.setStoreLabel(merchantQRgenerator.getStore_label());
		cimMercbantQRAddlInfo.setLoyaltyNumber(merchantQRgenerator.getLoyalty_number());
		cimMercbantQRAddlInfo.setCustomerLabel(merchantQRgenerator.getCustomer_label());
		cimMercbantQRAddlInfo.setTerminalLabel(merchantQRgenerator.getTerminal_label());
		cimMercbantQRAddlInfo.setReferenceLabel(merchantQRgenerator.getReference_label());
		cimMercbantQRAddlInfo.setPurposeOfTransaction(merchantQRgenerator.getPurpose_of_tran());
		cimMercbantQRAddlInfo.setAddlDataRequest(merchantQRgenerator.getAdditional_details());
		
		cimMerchantQRcodeRequest.setAdditionalDataInformation(cimMercbantQRAddlInfo);
		
		HttpEntity<CIMMerchantQRcodeRequest> entity = new HttpEntity<>(cimMerchantQRcodeRequest, httpHeaders);
		ResponseEntity<cimMerchantQRcodeResponse> response = null;
		try {
			System.out.println("URL IPS"+env.getProperty("ipsx.url")+"api/ws/generateMerchantQRcode");
			response = restTemplate.postForEntity(env.getProperty("ipsx.url")+"api/ws/generateMerchantQRcode",
					entity, cimMerchantQRcodeResponse.class);

			System.out.println("data-->");
			
			if (response.getStatusCode() == HttpStatus.OK) {
				System.out.println("data-->OK");
				return new ResponseEntity<cimMerchantQRcodeResponse>(response.getBody(), HttpStatus.OK);

			} else {
				return response;
			}

		}catch (HttpClientErrorException ex) {
			
			if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				cimMerchantQRcodeResponse errorRestResponse = new Gson().fromJson(ex.getResponseBodyAsString().toString(), cimMerchantQRcodeResponse.class);
				return new ResponseEntity<cimMerchantQRcodeResponse>(errorRestResponse, HttpStatus.BAD_REQUEST);
			} else {
				cimMerchantQRcodeResponse c24ftResponse = new cimMerchantQRcodeResponse("500",Arrays.asList("Something went wrong at server end"));
				return new ResponseEntity<cimMerchantQRcodeResponse>(c24ftResponse, ex.getStatusCode());
			}



		} catch (HttpServerErrorException ex) {
			cimMerchantQRcodeResponse c24ftResponse = new cimMerchantQRcodeResponse("500",Arrays.asList("Something went wrong at server end"));
			return new ResponseEntity<cimMerchantQRcodeResponse>(c24ftResponse, ex.getStatusCode());
		}

	}


	
}