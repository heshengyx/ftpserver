package com.grgbanking.ftpserver.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.grgbanking.ftpserver.enums.OptEnum;
import com.grgbanking.ftpserver.service.FtpService;

@Service
public class FileServiceImpl extends FtpService {

	@PostConstruct
	public void init() {
		this.opt = OptEnum.FINGER.name();
	}
	
	@Override
	protected String process(String json) {
		System.out.println("FileServiceImpl=" + json);
		return null;
	}
}
