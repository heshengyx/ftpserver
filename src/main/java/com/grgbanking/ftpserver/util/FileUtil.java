package com.grgbanking.ftpserver.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUtil.class);

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private FileUtil() {
	}

	/**
	 * 创建目录
	 * 
	 * @param folder
	 *            文件夹名称
	 * @param ip
	 *            IP地址
	 * @return
	 */
	public static File mkdirs(String folder, String ip) {
		File file = new File(folder + File.separator
				+ DATE_FORMAT.format(new Date()) + File.separator + ip);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 返回文件字节长度
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static long getFileLength(String fileName) {
		long length = 0;
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		File file = new File(fileName);
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(file, "r");
			length = randomFile.length();
		} catch (FileNotFoundException e) {
			LOGGER.error("文件不存在", e);
			throw new IllegalArgumentException("文件不存在");
		} catch (IOException e) {
			LOGGER.error("文件读取失败", e);
			throw new IllegalArgumentException("文件读取失败");
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e) {
					LOGGER.error("关闭文件失败", e);
				}
			}
		}
		return length;
	}

	/**
	 * 写入文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件内容
	 * @return
	 */
	public static File writeFile(String fileName, String content) {
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		if (StringUtils.isBlank(content)) {
			throw new IllegalArgumentException("文件内容不能为空");
		}
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile(); // 创建文件
			} catch (IOException e) {
				LOGGER.error("创建文件失败", e);
				throw new IllegalArgumentException("创建文件失败");
			}
		}

		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(file, "rw");
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾
			randomFile.seek(fileLength);
			randomFile.write(BASE64Util.decoder(content));
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
		return file;
	}
	
	/**
	 * 删除文件
	 * @param fileName
	 */
	public static void deleteFile(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		File file = new File(fileName);
		if (file.exists()) {
			if (file.delete()) {
				LOGGER.info("文件[{}]删除成功", fileName);
			} else {
				LOGGER.info("文件[{}]删除失败", fileName);
			}
		}
	}
}
