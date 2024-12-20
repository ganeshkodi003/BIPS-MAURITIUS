package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.Date;

public class TranCimCBSTablePojo {
	private String	sequence_unique_id;
	private String	request_uuid;
	private String	channel_id;
	private String	service_request_version;
	private String	service_request_id;
	private Date	message_date_time;
	private String	tran_no;
	private String	init_channel;
	private String	init_tran_no;
	private String	post_to_cbs;
	private String	tran_type;
	private String	isreversal;
	private String	tran_no_from_cbs;
	private String	customer_name;
	private String	from_account_no;
	private String	to_account_no;
	private BigDecimal	tran_amt;
	private Date	tran_date;
	private String	tran_currency;
	private String	tran_particular_code;
	private String	debit_remarks;
	private String	credit_remarks;
	private String	resv_field_1;
	private String	resv_field_2;
	private String	status;
	private String	status_code;
	private String	message;
	private String	entry_user;
	private Date	entry_time;
	private String	entity_cre_flg;
	private Date	value_date;
	private String	tran_charge_type;
	public TranCimCBSTablePojo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TranCimCBSTablePojo(String sequence_unique_id, String request_uuid, String channel_id,
			String service_request_version, String service_request_id, Date message_date_time, String tran_no,
			String init_channel, String init_tran_no, String post_to_cbs, String tran_type, String isreversal,
			String tran_no_from_cbs, String customer_name, String from_account_no, String to_account_no,
			BigDecimal tran_amt, Date tran_date, String tran_currency, String tran_particular_code,
			String debit_remarks, String credit_remarks, String resv_field_1, String resv_field_2, String status,
			String status_code, String message, String entry_user, Date entry_time, String entity_cre_flg,
			Date value_date, String tran_charge_type) {
		super();
		this.sequence_unique_id = sequence_unique_id;
		this.request_uuid = request_uuid;
		this.channel_id = channel_id;
		this.service_request_version = service_request_version;
		this.service_request_id = service_request_id;
		this.message_date_time = message_date_time;
		this.tran_no = tran_no;
		this.init_channel = init_channel;
		this.init_tran_no = init_tran_no;
		this.post_to_cbs = post_to_cbs;
		this.tran_type = tran_type;
		this.isreversal = isreversal;
		this.tran_no_from_cbs = tran_no_from_cbs;
		this.customer_name = customer_name;
		this.from_account_no = from_account_no;
		this.to_account_no = to_account_no;
		this.tran_amt = tran_amt;
		this.tran_date = tran_date;
		this.tran_currency = tran_currency;
		this.tran_particular_code = tran_particular_code;
		this.debit_remarks = debit_remarks;
		this.credit_remarks = credit_remarks;
		this.resv_field_1 = resv_field_1;
		this.resv_field_2 = resv_field_2;
		this.status = status;
		this.status_code = status_code;
		this.message = message;
		this.entry_user = entry_user;
		this.entry_time = entry_time;
		this.entity_cre_flg = entity_cre_flg;
		this.value_date = value_date;
		this.tran_charge_type = tran_charge_type;
	}
	public String getSequence_unique_id() {
		return sequence_unique_id;
	}
	public void setSequence_unique_id(String sequence_unique_id) {
		this.sequence_unique_id = sequence_unique_id;
	}
	public String getRequest_uuid() {
		return request_uuid;
	}
	public void setRequest_uuid(String request_uuid) {
		this.request_uuid = request_uuid;
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public String getService_request_version() {
		return service_request_version;
	}
	public void setService_request_version(String service_request_version) {
		this.service_request_version = service_request_version;
	}
	public String getService_request_id() {
		return service_request_id;
	}
	public void setService_request_id(String service_request_id) {
		this.service_request_id = service_request_id;
	}
	public Date getMessage_date_time() {
		return message_date_time;
	}
	public void setMessage_date_time(Date message_date_time) {
		this.message_date_time = message_date_time;
	}
	public String getTran_no() {
		return tran_no;
	}
	public void setTran_no(String tran_no) {
		this.tran_no = tran_no;
	}
	public String getInit_channel() {
		return init_channel;
	}
	public void setInit_channel(String init_channel) {
		this.init_channel = init_channel;
	}
	public String getInit_tran_no() {
		return init_tran_no;
	}
	public void setInit_tran_no(String init_tran_no) {
		this.init_tran_no = init_tran_no;
	}
	public String getPost_to_cbs() {
		return post_to_cbs;
	}
	public void setPost_to_cbs(String post_to_cbs) {
		this.post_to_cbs = post_to_cbs;
	}
	public String getTran_type() {
		return tran_type;
	}
	public void setTran_type(String tran_type) {
		this.tran_type = tran_type;
	}
	public String getIsreversal() {
		return isreversal;
	}
	public void setIsreversal(String isreversal) {
		this.isreversal = isreversal;
	}
	public String getTran_no_from_cbs() {
		return tran_no_from_cbs;
	}
	public void setTran_no_from_cbs(String tran_no_from_cbs) {
		this.tran_no_from_cbs = tran_no_from_cbs;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getFrom_account_no() {
		return from_account_no;
	}
	public void setFrom_account_no(String from_account_no) {
		this.from_account_no = from_account_no;
	}
	public String getTo_account_no() {
		return to_account_no;
	}
	public void setTo_account_no(String to_account_no) {
		this.to_account_no = to_account_no;
	}
	public BigDecimal getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(BigDecimal tran_amt) {
		this.tran_amt = tran_amt;
	}
	public Date getTran_date() {
		return tran_date;
	}
	public void setTran_date(Date tran_date) {
		this.tran_date = tran_date;
	}
	public String getTran_currency() {
		return tran_currency;
	}
	public void setTran_currency(String tran_currency) {
		this.tran_currency = tran_currency;
	}
	public String getTran_particular_code() {
		return tran_particular_code;
	}
	public void setTran_particular_code(String tran_particular_code) {
		this.tran_particular_code = tran_particular_code;
	}
	public String getDebit_remarks() {
		return debit_remarks;
	}
	public void setDebit_remarks(String debit_remarks) {
		this.debit_remarks = debit_remarks;
	}
	public String getCredit_remarks() {
		return credit_remarks;
	}
	public void setCredit_remarks(String credit_remarks) {
		this.credit_remarks = credit_remarks;
	}
	public String getResv_field_1() {
		return resv_field_1;
	}
	public void setResv_field_1(String resv_field_1) {
		this.resv_field_1 = resv_field_1;
	}
	public String getResv_field_2() {
		return resv_field_2;
	}
	public void setResv_field_2(String resv_field_2) {
		this.resv_field_2 = resv_field_2;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus_code() {
		return status_code;
	}
	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getEntry_user() {
		return entry_user;
	}
	public void setEntry_user(String entry_user) {
		this.entry_user = entry_user;
	}
	public Date getEntry_time() {
		return entry_time;
	}
	public void setEntry_time(Date entry_time) {
		this.entry_time = entry_time;
	}
	public String getEntity_cre_flg() {
		return entity_cre_flg;
	}
	public void setEntity_cre_flg(String entity_cre_flg) {
		this.entity_cre_flg = entity_cre_flg;
	}
	public Date getValue_date() {
		return value_date;
	}
	public void setValue_date(Date value_date) {
		this.value_date = value_date;
	}
	public String getTran_charge_type() {
		return tran_charge_type;
	}
	public void setTran_charge_type(String tran_charge_type) {
		this.tran_charge_type = tran_charge_type;
	}
	
	public TranCimCBSTablePojo(TranCimCBSTable cimCBSTable) {
		this.sequence_unique_id = cimCBSTable.getSequence_unique_id();
		this.request_uuid = cimCBSTable.getRequest_uuid();
		this.channel_id = cimCBSTable.getChannel_id();
		this.service_request_version = cimCBSTable.getService_request_version();
		this.service_request_id = cimCBSTable.getService_request_id();
		this.message_date_time = cimCBSTable.getMessage_date_time();
		this.tran_no = cimCBSTable.getTran_no();
		this.init_channel = cimCBSTable.getInit_channel();
		this.init_tran_no = cimCBSTable.getInit_tran_no();
		this.post_to_cbs = cimCBSTable.getPost_to_cbs();
		this.tran_type = cimCBSTable.getTran_type();
		this.isreversal = cimCBSTable.getIsreversal();
		this.tran_no_from_cbs = cimCBSTable.getTran_no_from_cbs();
		this.customer_name = cimCBSTable.getCustomer_name();
		this.from_account_no = cimCBSTable.getFrom_account_no();
		this.to_account_no = cimCBSTable.getTo_account_no();
		this.tran_amt = cimCBSTable.getTran_amt();
		this.tran_date = cimCBSTable.getTran_date();
		this.tran_currency = cimCBSTable.getTran_currency();
		this.tran_particular_code = cimCBSTable.getTran_particular_code();
		this.debit_remarks = cimCBSTable.getDebit_remarks();
		this.credit_remarks = cimCBSTable.getCredit_remarks();
		this.resv_field_1 = cimCBSTable.getResv_field_1();
		this.resv_field_2 = cimCBSTable.getResv_field_2();
		this.status = cimCBSTable.getStatus();
		this.status_code = cimCBSTable.getStatus_code();
		this.message = cimCBSTable.getMessage();
		this.entry_user = cimCBSTable.getEntry_user();
		this.entry_time = cimCBSTable.getEntry_time();
		this.entity_cre_flg = cimCBSTable.getEntity_cre_flg();
		this.value_date = cimCBSTable.getValue_date();
		this.tran_charge_type = cimCBSTable.getTran_charge_type();
	}
	
}
