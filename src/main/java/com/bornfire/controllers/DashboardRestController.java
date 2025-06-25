package com.bornfire.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bornfire.entity.MerchantMasterRep;

@RestController
public class DashboardRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(IPSRestController.class);
	
	@Autowired
	MerchantMasterRep merchantMasterRep;
	
	
	@RequestMapping(value = "GetPieCount", method = RequestMethod.GET)
	@ResponseBody
	public List<List<Integer>> GetPieCount(HttpServletResponse response){
		
		return merchantMasterRep.PiechartCount();

	}

}
