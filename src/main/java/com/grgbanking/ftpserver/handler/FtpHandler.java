package com.grgbanking.ftpserver.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grgbanking.ftpserver.FtpServer;

@Component
public class FtpHandler {

	private List<FtpServer> servers;
	private Map<String, FtpServer> map;

	public List<FtpServer> getServers() {
		return servers;
	}

	@Autowired
	public void setServers(List<FtpServer> servers) {
		this.servers = servers;
		createMap();
	}

	private void createMap() {
		if (!CollectionUtils.isEmpty(servers)) {
			map = new HashMap<String, FtpServer>();
			for (FtpServer server : servers) {
				map.put(server.getOpt(), server);
			}
		}
	}

	public FtpServer getFtpServer(String opt) {
		return (!CollectionUtils.isEmpty(map)) ? map.get(opt) : null;
	}
}
