package com.bornfire.services;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.bornfire.entity.AccountContactResponse;
import com.bornfire.entity.C24FTRequest;
import com.bornfire.entity.C24RequestAcount;
import com.bornfire.entity.TranMonitorStatus;
import com.bornfire.exception.ErrorResponseCode;
import com.google.gson.Gson;

@Service
public class Connect24Service {

	@Autowired
	Environment env;

	@Autowired
	RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(Connect24Service.class);

	public ResponseEntity<AccountContactResponse> getAccountContact(String acctNumber) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		String bobBankCode = env.getProperty("bob.bankcode");
		String bobCrncyCode = env.getProperty("bob.crncycode");
		String connect24Url = env.getProperty("connect24.url");

		if (bobBankCode == null || bobCrncyCode == null || connect24Url == null) {
			logger.error("Environment properties are not properly configured.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		httpHeaders.set("Partipant_BIC", bobBankCode);

		if (acctNumber == null || acctNumber.length() < 4) {
			logger.error("Account number is invalid.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		httpHeaders.set("Partipant_SOL", acctNumber.substring(0, 4));

		C24FTRequest c24ftRequest = new C24FTRequest();
		C24RequestAcount c24RequestAcount = new C24RequestAcount();
		c24RequestAcount.setAcctNumber(acctNumber);
		c24RequestAcount.setSchmType("");
		c24ftRequest.setAccount(c24RequestAcount);
		c24ftRequest.setCurrency_Code(bobCrncyCode);
		c24ftRequest.setPAN("0000000000000000");

		HttpEntity<C24FTRequest> entity = new HttpEntity<>(c24ftRequest, httpHeaders);
		ResponseEntity<AccountContactResponse> response = null;

		try {
			logger.info("Sending message to connect24 ");
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			response = restTemplate.postForEntity(connect24Url + "/api/ws/getContact?", entity,
					AccountContactResponse.class);
			if (response != null && response.getBody() != null) {
				logger.info("Response from connect24: " + response.getBody());
				//System.out.println("Success: " + response.getBody().getAccountStatus());
			} else {
				logger.error("Response or response body is null.");
			}
		} catch (HttpClientErrorException ex) {
			logger.error("HttpClientErrorException: " + ex.getLocalizedMessage(), ex);
			if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				AccountContactResponse contactResponse = new AccountContactResponse("FAILURE",
						ErrorResponseCode.SERVER_ERROR_CODE,
						Collections.singletonList(TranMonitorStatus.CBS_SERVER_NOT_CONNECTED.toString()));
				return new ResponseEntity<>(contactResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				AccountContactResponse contactResponse1 = new Gson().fromJson(ex.getResponseBodyAsString(),
						AccountContactResponse.class);
				AccountContactResponse contactResponse = new AccountContactResponse("Failure",
						contactResponse1.getError(), contactResponse1.getError_desc());
				return new ResponseEntity<>(contactResponse, ex.getStatusCode());
			}
		} catch (HttpServerErrorException ex) {
			logger.error("HttpServerErrorException: " + ex.getLocalizedMessage(), ex);
			AccountContactResponse contactResponse = new AccountContactResponse("FAILURE",
					ErrorResponseCode.SERVER_ERROR_CODE,
					Collections.singletonList(TranMonitorStatus.CBS_SERVER_NOT_CONNECTED.toString()));
			return new ResponseEntity<>(contactResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			logger.error("Unexpected Exception: " + ex.getLocalizedMessage(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

}
