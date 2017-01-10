package com.grgbanking.ftpserver.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.common.Symbol;
import com.grgbanking.ftpserver.enums.OptEnum;
import com.grgbanking.ftpserver.enums.StatusEnum;
import com.grgbanking.ftpserver.handler.ApplicationContextHandler;
import com.grgbanking.ftpserver.handler.FtpHandler;
import com.grgbanking.ftpserver.hold.CacheHold;
import com.grgbanking.ftpserver.json.JSONResult;

public class GrgbankingServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GrgbankingServerHandler.class);

	private FtpHandler ftpHandler;
	private CacheHold cacheHold;

	public GrgbankingServerHandler() {
		this.ftpHandler = ApplicationContextHandler.getBean("ftpHandler");
		this.cacheHold = ApplicationContextHandler.getBean("cacheHold");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		String ip = insocket.getAddress().getHostAddress();

		ByteBuf buf = (ByteBuf) msg;
		int length = buf.readableBytes();
		if (length > 0) {
			byte[] req = new byte[buf.readableBytes()];// 获得缓冲区可读的字节数
			buf.readBytes(req);

			String content = null;
			String body = new String(req, "UTF-8");

			boolean flag = body.contains("simple");
			if (flag) {
				content = body.substring(0, body.indexOf("simple"))
						+ body.substring(body.indexOf("type"));
			} else {
				content = body;
			}
			LOGGER.info("终端[{}]，接收消息：{}", new Object[] { ip, content });

			praseMessage(ctx, body, ip);
		}
	}

	private void praseMessage(ChannelHandlerContext ctx, String body, final String ip)
			throws UnsupportedEncodingException {
		String message = null;
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(body);
		} catch (Exception e) {
			LOGGER.error("请求数据不是JSON格式", e);
			message = "请求数据不是JSON格式";
		}
		if (json != null) {
			String opt = json.optString("type");
			if (StringUtils.isBlank(opt)) {
				message = "请求数据没有操作标示";
			} else if (OptEnum.UpgradeStart.name().equals(opt)) {
				// 升级消息
				downFile(ctx, ip, json.optString("Version"));
			} else {
				FtpServer server = ftpHandler.getFtpServer(opt);
				if (server != null) {
					message = server.handler(body, ip);
				}
			}
		}
		if (StringUtils.isNotBlank(message)) {
			final String msg = message;
			ByteBuf resp = Unpooled.copiedBuffer(message.getBytes("UTF-8"));
			ChannelFuture future = ctx.write(resp);
			future.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
					LOGGER.info("终端[{}]，响应消息：{}", new Object[] { ip, msg });
				}
			});
		}
	}

	private void downFile(ChannelHandlerContext ctx, String ip, String version)
			throws UnsupportedEncodingException {
		JSONResult result = new JSONResult();
		Map<String, String> results = cacheHold.getCache();
		if (!CollectionUtils.isEmpty(results)) {
			// 取得版本号
			String v = cacheHold.getVersion();
			LOGGER.info("终端版本[{}]，服务端版本：[{}]", new Object[] { version, v });
			if (!v.equals(version) && StringUtils.isNotBlank(version)) {
				result.setType(OptEnum.UpgradeStart.name());
				result.setRetcode(String.valueOf(StatusEnum.SUCCESS.getValue()));
				ByteBuf resp = Unpooled.copiedBuffer(result.toJson().getBytes(
						"UTF-8"));
				LOGGER.info("终端[{}]，响应消息：{}",
						new Object[] { ip, result.toJson() });
				ctx.writeAndFlush(resp);
				
				String[] keys = null;
				for (Map.Entry<String, String> entry : results.entrySet()) {
					keys = StringUtils.split(entry.getKey(), Symbol.COLONS);
					result.setType(OptEnum.UpgradeFile.name());
					result.setFilename(keys[0].substring(
							cacheHold.getFolder().length() + 1).replaceAll("\\\\", "/"));
					result.setFrameLength(keys[2]);
					result.setSample(entry.getValue());

					resp = Unpooled.copiedBuffer(result.toCacheJson().getBytes(
							"UTF-8"));
					LOGGER.info("终端[{}]，响应消息：{}",
							new Object[] { ip, result.toCacheJson() });
					try {
						Thread.sleep(Long.parseLong(cacheHold.getTime()));
					} catch (InterruptedException e) {
					}
					ctx.writeAndFlush(resp);
				}
				try {
					Thread.sleep(Long.parseLong(cacheHold.getTime()));
				} catch (InterruptedException e) {
				}
				result.setType(OptEnum.UpgradeOver.name());
				result.setRetcode(String.valueOf(StatusEnum.SUCCESS.getValue()));
				resp = Unpooled.copiedBuffer(result.toJson().getBytes(
						"UTF-8"));
				LOGGER.info("终端[{}]，响应消息：{}",
						new Object[] { ip, result.toJson() });
				ctx.writeAndFlush(resp);
			} else {
				result.setType(OptEnum.UpgradeStart.name());
				result.setRetcode(String.valueOf(StatusEnum.FAIL.getValue()));
				ByteBuf resp = Unpooled.copiedBuffer(result.toJson().getBytes(
						"UTF-8"));
				LOGGER.info("终端[{}]，响应消息：{}",
						new Object[] { ip, result.toJson() });
				ctx.writeAndFlush(resp);
			}
		}
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// 将消息发送队列中的消息写入到SocketChannel中发送给对方。
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.error("异常退出", cause);
		ctx.close();
	}
}
