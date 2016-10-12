package com.grgbanking.ftpserver.hold;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.enums.OptEnum;

@Service
public class ImageHold extends FtpServer {

	@PostConstruct
	public void init() {
		this.opt = OptEnum.FINGER.name();
	}
	
	@Override
	protected String process(String json) {
		System.out.println(json);
		return null;
	}

}
