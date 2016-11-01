package com.grgbanking.ftpserver.hold;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grgbanking.ftpserver.common.Symbol;
import com.grgbanking.ftpserver.enums.OptEnum;
import com.grgbanking.ftpserver.util.BASE64Util;
import com.grgbanking.ftpserver.util.FileUtil;
import com.grgbanking.ftpserver.util.PropertyUtil;

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
	
	/**
	 * 目录版本
	 */
	@Value("${version.file}")
	private String file;
	
	/**
	 * 版本前缀
	 */
	@Value("${version.prefix}")
	private String prefix;
	
	/**
	 * 文件版本
	 */
	@Value("${version.config}")
	private String config;
	
	/**
	 * 版本key
	 */
	@Value("${version.key}")
	private String key;
	
	//版本号
	private String version;
	
	private Map<String, String> results = new LinkedHashMap<String, String>();
	
	public String getFolder() {
		return this.folder;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getKey() {
		return key;
	}

	public Map<String, String> getCache() {
		return this.results;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void readFile() {
		// 从文件中取得升级版本目录
		List<String> contents = FileUtil.readFile(folder + File.separator
				+ file);
		if (CollectionUtils.isNotEmpty(contents)) {
			String version = contents.get(0);
			if (StringUtils.isNotBlank(version)) {
				setFolder(folder + File.separator + prefix + version);
				// 从文件中取得升级版本号
				version = PropertyUtil.readByKey(getFolder()
						+ File.separator + config, key);
				setVersion(version);
				readFile(getFolder(), Integer.parseInt(limit));
			}
		}
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
		if (length > 0) {
			//results.put(keys.toString(), BASE64Util.encode(flush).replaceAll("\\s", ""));
		}
		
		StringBuilder keys = null;
		//缓冲区
        byte[] flush = new byte[limit];
		RandomAccessFile randomFile = null;
		for (int i = 0; i < size; i++) {
			keys = new StringBuilder();
			keys.append(file.getPath()).append(Symbol.COLONS).append(i)
					.append(Symbol.COLONS).append(limit);
			try {
				randomFile = new RandomAccessFile(file, "r");
				// 将写文件指针移到文件尾
				randomFile.seek(limit * i);
				randomFile.read(flush);
				
				results.put(keys.toString(), BASE64Util.encode(flush).replaceAll("\\s", ""));
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
			keys = new StringBuilder();
			keys.append(file.getPath()).append(Symbol.COLONS).append(size)
					.append(Symbol.COLONS).append(remainder);
			try {
				randomFile = new RandomAccessFile(file, "r");
				// 将写文件指针移到文件尾
				randomFile.seek(limit * size);
				randomFile.read(flush);
				
				results.put(keys.toString(), BASE64Util.encode(flush).replaceAll("\\s", ""));
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
