package com.grgbanking.ftpserver.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.enums.OptEnum;
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

		String message = null;
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];// 获得缓冲区可读的字节数
		buf.readBytes(req);

		String content = null;
		String body = new String(req, "UTF-8");
		// 清空变量，释放内存
		buf = null;
		req = null;

		boolean flag = body.contains("simple");
		if (flag) {
			content = body.substring(0, body.indexOf("simple"))
					+ body.substring(body.indexOf("type"));
		} else {
			content = body;
		}
		LOGGER.info("终端[{}]，接收消息：{}", new Object[] { ip, content });
		if (StringUtils.isNotBlank(body)) {
			message = praseJSON(body, ip);
			LOGGER.info("终端[{}]，响应消息：{}", new Object[] { ip, message });
		}
		if (StringUtils.isNotBlank(message)) {
			ByteBuf resp = Unpooled.copiedBuffer(message.getBytes("UTF-8"));
			ctx.write(resp);// 性能考虑，仅将待发送的消息发送到缓冲数组中，再通过调用flush方法，写入channel中
		} else if (body.contains(OptEnum.UpgradeStart.name())) {
			//升级消息
			downFile(ip, ctx);	
		}
	}

	private void downFile(String ip, ChannelHandlerContext ctx)
			throws UnsupportedEncodingException {
		JSONResult result = new JSONResult();
		Map<String, String> results = cacheHold.getCache();
		
		for (Map.Entry<String, String> entry : results.entrySet()) {
			result.setType(OptEnum.UpgradeFile.name());
			result.setFilename(entry.getKey().substring(7, entry.getKey().lastIndexOf("_")));
			result.setSample(entry.getValue());
			
			ByteBuf resp = Unpooled.copiedBuffer(result.toCacheJson().getBytes(
					"UTF-8"));
			LOGGER.info("终端[{}]，响应消息：{}", new Object[] { ip, result.toCacheJson() });
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			ctx.writeAndFlush(resp);
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

	private String praseJSON(String body, String ip) {
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
			} else {
				FtpServer server = ftpHandler.getFtpServer(opt);
				if (server != null) {
					server.setIp(ip);
					message = server.handler(body);
				}
			}
		}
		return message;
	}
}
