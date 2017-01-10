package com.grgbanking.ftpserver.test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.grgbanking.ftpserver.common.Symbol;
import com.grgbanking.ftpserver.util.BASE64Util;
import com.grgbanking.ftpserver.util.FileUtil;
import com.grgbanking.ftpserver.util.PropertyUtil;

public class FileTest extends BaseJunitTest {
	
	@Test
	public void testEhcache() {
		
	}

	public static void main(String[] args) throws IOException {
		//String content = BASE64Util.encode("E:/images/2.bmp");
		//System.out.println(content.replaceAll("\\s", ""));
		//FileUtil.writeFile("E:/images/4.jpg", content);
		
		/*String content = "";
		String body = "{'name': '1', 'simple': 'dfdfd', 'type', '2'}";
		boolean flag = body.contains("simple");
		if (flag) {
			content = body.substring(0, body.indexOf("simple")) + body.substring(body.indexOf("type"));
		}
		System.out.println(content);*/

		/*String[] keys = StringUtils.split("F:/down/grgfingervein2016110101/config/system.cfg#-#0#-#349", Symbol.COLONS);
		for (String key : keys) {
			System.out.println(key);
		}*/
		
		//List<String> content = FileUtil.readFile("F:/home/version.txt");
		//System.out.println(content);
		//String content = PropertyUtil.readByKey("F:/home/grgfingervein-2016110101/config/system.cfg", "version");
		//System.out.println(content);
		
		/*String content = BASE64Util.encode("E:/testData/G0107813_null_路超_安全产品部_96_2016-10-26_16-54-36.bin");
		System.out.println(content.replaceAll("\\s", ""));
		byte[] b = BASE64Util.decoder(content);
		System.out.println(b.length);*/
		
		checkMaster(true);
	}

	private static boolean checkMaster(boolean flag) {
		while(true) {
			System.out.println(flag);
			return flag;
		}
	}
}
