package com.grgbanking.ftpserver.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class BASE64Util {
	
	private BASE64Util() {}

	/**
	 * 解码
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static byte[] decoder(String content) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] data = decoder.decodeBuffer(content);
		for (int i = 0; i < data.length; ++i) {
			if (data[i] < 0) {
				data[i] += 256;
			}
		}
		return data;
	}
	
	/**
	 * 编码
	 * @param fileName
	 * @return
	 */
	public static String encode(String fileName) throws IOException {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(fileName);
			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {throw e;}
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}
}
