package com.grgbanking.ftpserver.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtil {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PropertyUtil.class);

	private PropertyUtil() {}
	
	/**
	 * 根据key读取value
	 * @param fileName
	 * @param key
	 * @return
	 */
	public static String readByKey(String fileName, String key) {
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		String content = null;
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(
					fileName));
			props.load(in);
			
			content = props.getProperty(key);
		} catch (Exception e) {
			LOGGER.error("读取文件失败", e);
			throw new IllegalArgumentException("读取文件失败");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("关闭文件失败", e);
				}
			}
		}
		return content;
	}
}
