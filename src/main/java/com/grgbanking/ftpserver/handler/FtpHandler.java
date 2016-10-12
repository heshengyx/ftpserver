package com.grgbanking.ftpserver.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grgbanking.ftpserver.FtpServer;

@Component
public class FtpHandler {

	private List<FtpServer> servers;

	public List<FtpServer> getServers() {
		return servers;
	}

	@Autowired
	public void setServers(List<FtpServer> servers) {
		this.servers = servers;
	}
}
