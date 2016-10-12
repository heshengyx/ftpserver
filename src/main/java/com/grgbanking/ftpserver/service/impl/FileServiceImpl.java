package com.grgbanking.ftpserver.service.impl;

import org.springframework.stereotype.Service;

import com.grgbanking.ftpserver.service.FtpService;

@Service
public class FileServiceImpl implements FtpService {

	private String opt = "";
	
	public FileServiceImpl() {
		System.out.println("================FileServiceImpl");
	}
	
	@Override
	public String getOpt() {
		return this.opt;
	}

	@Override
	public String handler(String json) {
		return null;
	}

}
