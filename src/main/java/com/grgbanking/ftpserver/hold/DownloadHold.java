package com.grgbanking.ftpserver.hold;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.json.Result;

public class DownloadHold extends FtpServer {

	@Override
	protected Result process(String json) {
		CacheManager manager = CacheManager.create();
		Cache cache = manager.getCache("byteCache");
		
		
		Element element = new Element("", "");
		return null;
	}

}
