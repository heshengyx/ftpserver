package com.grgbanking.ftpserver.hold;

import java.io.File;

import javax.annotation.PostConstruct;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.enums.OptEnum;
import com.grgbanking.ftpserver.enums.StatusEnum;
import com.grgbanking.ftpserver.json.Result;
import com.grgbanking.ftpserver.util.FileUtil;

/**
 * 确认业务
 * @author
 *
 */
@Service
public class ConfirmHold extends FtpServer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConfirmHold.class);

	@Value("${ftp.folder}")
	private String folder;

	@PostConstruct
	public void init() {
		this.opt = OptEnum.FtpEnd.name();
	}

	@Override
	protected Result process(String json) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		String fileName = jsonObject.optString("filename");
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}
		String fileLength = jsonObject.optString("fileLength");
		if ("0".equals(fileLength)) {
			throw new IllegalArgumentException("文件不能为空");
		}

		// 创建文件目录
		File file = FileUtil.mkdirs(folder, ip);
		// 取得文件大小
		long lenght = FileUtil.getFileLength(file + File.separator + fileName);
		LOGGER.info("源文件[{}]大小：{}，目标文件[{}]大小：{}", new Object[] { fileName,
				fileLength, fileName, lenght });

		Result result = null;
		// 比较源文件和上传文件大小，如不一致则删除
		if (fileLength.equals(String.valueOf(lenght))) {
			LOGGER.info("文件[{}]上传成功", fileName);
			result = new Result(String.valueOf(StatusEnum.SUCCESS.getValue()),
					fileName);
		} else {
			LOGGER.info("文件[{}]上传失败，正在删除文件", fileName);
			FileUtil.deleteFile(file + File.separator + fileName);
			result = new Result(String.valueOf(StatusEnum.FAIL.getValue()),
					fileName);
		}
		return result;
	}

}
