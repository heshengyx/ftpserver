package com.grgbanking.ftpserver.hold;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grgbanking.ftpserver.util.BASE64Util;

@Service
public class CacheHold {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CacheHold.class);
	
	/**
	 * 文件下载目录
	 */
	@Value("${down.folder}")
	private String folder;
	
	/**
	 * 文件分割大小
	 */
	@Value("${down.limit}")
	private String limit;
	
	private Map<String, String> results = new LinkedHashMap<String, String>();
	
	public Map<String, String> getCache() {
		return results;
	}
	
	public void readFile() {
		readFile(folder, Integer.parseInt(limit));
	}
	
	public void readFile(int limit) {
		readFile(folder, limit);
	}
	
	public void readFile(String folder, int limit) {
		if (StringUtils.isBlank(folder)) {
			throw new IllegalArgumentException("文件不能为空");
		}
		File file = new File(folder); 
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				readFile(f.getPath(), limit);
			}
		} else {
			cacheFile(file, limit);
		}
	}
	
	private void cacheFile(File file, int limit) {
		long length = file.length();
		int size = (int) (length / limit);
		int remainder = (int) (length % limit);
		
		//缓冲区
        byte[] flush = new byte[limit];
		RandomAccessFile randomFile = null;
		for (int i = 0; i < size; i++) {
			try {
				randomFile = new RandomAccessFile(file, "r");
				// 将写文件指针移到文件尾
				System.out.println(limit + " * " + i + "=" + (limit * i));
				randomFile.seek(limit * i);
				randomFile.read(flush);
				
				results.put(file.getPath() + "_" + i, BASE64Util.encode(flush).replaceAll("\\s", ""));
			} catch (FileNotFoundException e) {
				LOGGER.error("文件不存在", e);
				throw new IllegalArgumentException("文件不存在");
			} catch (IOException e) {
				LOGGER.error("写人文件失败", e);
				throw new IllegalArgumentException("写人文件失败");
			} finally {
				if (randomFile != null) {
					try {
						randomFile.close();
					} catch (IOException e) {
						LOGGER.error("关闭文件失败", e);
					}
				}
			}
		}
		if (remainder != 0) {
			flush = new byte[remainder];
			try {
				randomFile = new RandomAccessFile(file, "r");
				// 将写文件指针移到文件尾
				randomFile.seek(limit * size);
				randomFile.read(flush);
				
				results.put(file.getPath() + "_" + size, BASE64Util.encode(flush).replaceAll("\\s", ""));
			} catch (FileNotFoundException e) {
				LOGGER.error("文件不存在", e);
				throw new IllegalArgumentException("文件不存在");
			} catch (IOException e) {
				LOGGER.error("写人文件失败", e);
				throw new IllegalArgumentException("写人文件失败");
			} finally {
				if (randomFile != null) {
					try {
						randomFile.close();
					} catch (IOException e) {
						LOGGER.error("关闭文件失败", e);
					}
				}
			}
		}
	}
}
