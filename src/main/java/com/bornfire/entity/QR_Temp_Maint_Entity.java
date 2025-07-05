package com.bornfire.entity;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat; 
import java.util.Date;

@Entity
@Table(name = "QR_TEMPLATE_MAINTENANCE")
public class QR_Temp_Maint_Entity {

	@Id
	private String banner_id;

	private String banner_name;

	private String banner_type;

	@Lob
	private byte[] template;

	private String entry_user;

	private String modify_user;

	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date entry_time;

	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date modify_time;


	private String del_flg;

	private String entity_flg;

	private String modify_flg;

	public String getBanner_id() {
		return banner_id;
	}

	public void setBanner_id(String banner_id) {
		this.banner_id = banner_id;
	}

	public String getBanner_name() {
		return banner_name;
	}

	public void setBanner_name(String banner_name) {
		this.banner_name = banner_name;
	}

	public String getBanner_type() {
		return banner_type;
	}

	public void setBanner_type(String banner_type) {
		this.banner_type = banner_type;
	} 
	
	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}

	public String getEntry_user() {
		return entry_user;
	}

	public void setEntry_user(String entry_user) {
		this.entry_user = entry_user;
	}

	public String getModify_user() {
		return modify_user;
	}

	public void setModify_user(String modify_user) {
		this.modify_user = modify_user;
	}

	 

	public Date getEntry_time() {
		return entry_time;
	}

	public void setEntry_time(Date entry_time) {
		this.entry_time = entry_time;
	}

	public Date getModify_time() {
		return modify_time;
	}

	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}

	public String getDel_flg() {
		return del_flg;
	}

	public void setDel_flg(String del_flg) {
		this.del_flg = del_flg;
	}

	public String getEntity_flg() {
		return entity_flg;
	}

	public void setEntity_flg(String entity_flg) {
		this.entity_flg = entity_flg;
	}

	public String getModify_flg() {
		return modify_flg;
	}

	public void setModify_flg(String modify_flg) {
		this.modify_flg = modify_flg;
	} 
 
	

	public QR_Temp_Maint_Entity(String banner_id, String banner_name, String banner_type, byte[] template,
			String entry_user, String modify_user, Date entry_time, Date modify_time, String del_flg, String entity_flg,
			String modify_flg) {
		super();
		this.banner_id = banner_id;
		this.banner_name = banner_name;
		this.banner_type = banner_type;
		this.template = template;
		this.entry_user = entry_user;
		this.modify_user = modify_user;
		this.entry_time = entry_time;
		this.modify_time = modify_time;
		this.del_flg = del_flg;
		this.entity_flg = entity_flg;
		this.modify_flg = modify_flg;
	}

	public QR_Temp_Maint_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}


    // Getters and Setters
 
}
