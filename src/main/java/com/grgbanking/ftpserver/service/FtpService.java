package com.grgbanking.ftpserver.service;

public abstract class FtpService {

	protected String opt;

	public final String getOpt() {
		return this.opt;
	}

	public final String handler(String json) {
		return process(json);
	}

	protected abstract String process(String json);
}
