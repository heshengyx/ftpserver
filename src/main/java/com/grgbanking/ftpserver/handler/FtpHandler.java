package com.grgbanking.ftpserver.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grgbanking.ftpserver.service.FtpService;

@Component
public class FtpHandler {

	private List<FtpService> services;

	public List<FtpService> getServices() {
		return services;
	}

	@Autowired
	public void setServices(List<FtpService> services) {
		this.services = services;
	}
}
