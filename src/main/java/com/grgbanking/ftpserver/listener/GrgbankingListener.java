package com.grgbanking.ftpserver.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.enums.OptEnum;
import com.grgbanking.ftpserver.handler.ApplicationContextHandler;
import com.grgbanking.ftpserver.handler.FtpHandler;
import com.grgbanking.ftpserver.netty.GrgbankingServer;


public class GrgbankingListener implements ServletContextListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GrgbankingListener.class);
	
	private GrgbankingServer server;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (server != null)
			server.shutdown();
	}
 
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String opt = OptEnum.FINGER.name();
		String json = "json";
		FtpHandler ftpHandler = ApplicationContextHandler.getBean("ftpHandler");
		List<FtpServer> servers = ftpHandler.getServers();
		for (FtpServer server : servers) {
			if (opt.equals(server.getOpt())) {
				server.handler(json);
			}
		}
		//读取配置信息
		String serverPort = sce.getServletContext().getInitParameter("serverPort");
		server = new GrgbankingServer(Integer.parseInt(serverPort));
		try {
			LOGGER.info("FTP服务正在启动中...");
			//以下代码会阻塞
			server.run();
		} catch (Exception e) {
			LOGGER.error("FTP服务启动失败", e);
		}	
	}
}
