package com.grgbanking.ftpserver;

public abstract class FtpServer {

	protected String opt;
	protected String ip;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public final String getOpt() {
		return this.opt;
	}

	public final String handler(String json) {
		return process(json);
	}

	protected abstract String process(String json);
}
