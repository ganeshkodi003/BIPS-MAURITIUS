package com.bornfire.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "BIPS_SERVICEREQ")
public class BIPS_Service_Req_Entity {

	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date	ticket_date;
	@Id
	private String	subject;
	private String	description;
	private String	status;
	private String	resolution;
	public Date getTicket_date() {
		return ticket_date;
	}
	public void setTicket_date(Date ticket_date) {
		this.ticket_date = ticket_date;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public BIPS_Service_Req_Entity(Date ticket_date, String subject, String description, String status,
			String resolution) {
		super();
		this.ticket_date = ticket_date;
		this.subject = subject;
		this.description = description;
		this.status = status;
		this.resolution = resolution;
	}
	public BIPS_Service_Req_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
