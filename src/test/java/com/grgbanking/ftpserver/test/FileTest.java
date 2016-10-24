package com.grgbanking.ftpserver.test;

import java.io.IOException;

import com.grgbanking.ftpserver.util.BASE64Util;
import com.grgbanking.ftpserver.util.FileUtil;

public class FileTest {

	public static void main(String[] args) throws IOException {
		//String content = BASE64Util.encode("E:/images/2.bmp");
		//System.out.println(content.replaceAll("\\s", ""));
		//FileUtil.writeFile("E:/images/4.jpg", content);
		
		String content = "";
		String body = "{'name': '1', 'si1ple': 'dfdfd', 'type', '2'}";
		boolean flag = body.contains("simple");
		if (flag) {
			content = body.substring(0, body.indexOf("simple")) + "simple': '','" + body.substring(body.indexOf("type"));
		}
		System.out.println(content);
	}
}
