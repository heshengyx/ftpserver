package com.grgbanking.ftpserver.json;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class JSONResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String retcode;
	private String message;
	
	private String filename;
	private String fileLength;
	private String frameLength;
	private String sample;
	
	public JSONResult() {}
	
	public JSONResult(String retcode, String message) {
		this.retcode = retcode;
		this.message = message;
	}
	
	public String getFrameLength() {
		return frameLength;
	}

	public void setFrameLength(String frameLength) {
		this.frameLength = frameLength;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileLength() {
		return fileLength;
	}

	public void setFileLength(String fileLength) {
		this.fileLength = fileLength;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
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
	
	public String toCacheJson() {
		StringBuilder sb = new StringBuilder("");
		sb.append("{'type':'").append(type);
		sb.append("','filename':'").append(filename);
		if (StringUtils.isNotBlank(sample)) {
			sb.append("','sample':'").append(sample);
			sb.append("','frameLength':'").append(frameLength);
		} else if (StringUtils.isNotBlank(fileLength)) {
			sb.append("','fileLength':'").append(fileLength);
		}
		sb.append("'}");
		return sb.toString();
	}
}
