package com.grgbanking.ftpserver.json;

import java.io.Serializable;

public class JSONResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String retcode;
	private String message;
	
	public JSONResult() {}
	
	public JSONResult(String retcode, String message) {
		this.retcode = retcode;
		this.message = message;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRetcode() {
		return retcode;
	}
	public void setRetcode(String retcode) {
		this.retcode = retcode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toJson() {
		StringBuilder sb = new StringBuilder("");
		sb.append("{'type':'").append(type);
		sb.append("','retcode':'").append(retcode);
		sb.append("','name':'").append(message);
		sb.append("'}");
		return sb.toString();
	}
}
