package com.grgbanking.ftpserver;

import java.util.Map;

import org.apache.log4j.Logger;

import com.grgbanking.ftpserver.enums.StatusEnum;
import com.grgbanking.ftpserver.json.JSONResult;
import com.grgbanking.ftpserver.json.Result;

/**
 * 模板
 * @author
 *
 */
public abstract class FtpServer {

	private static final Logger LOGGER = Logger.getLogger(FtpServer.class);

	protected String opt; //业务类型
	protected String ip;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public final String getOpt() {
		return this.opt;
	}

	/**
	 * 模板方法
	 * @param json
	 * @return
	 */
	public final String handler(String json) {
		JSONResult jsonResult = null;
		try {
			Result result = process(json);
			if (result != null) {
				jsonResult = new JSONResult(result.getCode(),
						result.getMessage());
			} else {
				jsonResult = new JSONResult();
			}
		} catch (IllegalArgumentException e) {
			//业务异常
			jsonResult = new JSONResult(String.valueOf(StatusEnum.ERROR
					.getValue()), e.getMessage());
		} catch (Exception e) {
			//系统异常
			LOGGER.error("系统异常", e);
			jsonResult = new JSONResult(String.valueOf(StatusEnum.ERROR
					.getValue()), "系统异常");
		}
		return jsonResult.toJson();
	}

	/**
	 * 业务处理
	 * @param json
	 * @return
	 */
	protected abstract Result process(String json);
}
