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
 * 图片上传
 * @author
 *
 */
@Service
public class ImageHold extends FtpServer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ImageHold.class);

	@Value("${ftp.folder}")
	private String folder;

	@PostConstruct
	public void init() {
		this.opt = OptEnum.Ftping.name();
	}

	@Override
	protected Result process(String json, String ip) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		String content = jsonObject.optString("sample");
		if (StringUtils.isBlank(content)) {
			throw new IllegalArgumentException("图片内容不能为空");
		}
		String fileName = jsonObject.optString("filename");
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("文件名不能为空");
		}

		//创建文件目录
		File file = FileUtil.mkdirs(folder, ip);
		//写入文件
		file = FileUtil.writeFile(file.getPath() + File.separator + fileName,
				content);
		
		Result result = null;
		if (file.exists()) {
			LOGGER.info("文件[{}]创建成功", fileName);
			result = new Result(String.valueOf(StatusEnum.SUCCESS.getValue()),
					fileName);
		} else {
			LOGGER.info("文件[{}]创建失败", fileName);
			result = new Result(String.valueOf(StatusEnum.FAIL.getValue()),
					fileName);
		}
		return result;
	}

}
