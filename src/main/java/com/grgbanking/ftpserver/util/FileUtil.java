package com.grgbanking.ftpserver.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUtil.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private FileUtil() {}

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
		if (file.exists()) {
			length = file.length();
		}
		return length;
	}
	
	/**
	 * 读取文件内容
	 * @param fileName
	 * @return
	 */
	public static List<String> readFile(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		List<String> contents = new ArrayList<String>();
		File file = new File(fileName);
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(file, "r");
			String content = null;
			while((content = randomFile.readLine()) != null) {
				contents.add(content);
			}
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
		return contents;
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
	public static boolean deleteFile(String fileName) {
		boolean flag = false;
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		File file = new File(fileName);
		if (file.exists()) {
			if (file.delete()) {
				LOGGER.info("文件[{}]删除成功", fileName);
				flag = true;
			} else {
				LOGGER.info("文件[{}]删除失败", fileName);
			}
		}
		return flag;
	}
}
