package com.grgbanking.ftpserver.netty;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grgbanking.ftpserver.FtpServer;
import com.grgbanking.ftpserver.handler.FtpHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.json.JSONObject;

public class GrgbankingServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GrgbankingServerHandler.class);
	
	private FtpHandler ftpHandler;

	public GrgbankingServerHandler() {
		ftpHandler = new FtpHandler();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		String ip = insocket.getAddress().getHostAddress();

		String message = "";
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];// 获得缓冲区可读的字节数
		buf.readBytes(req);

		String body = new String(req, "UTF-8");
		LOGGER.info("终端[" + ip + "]，接收消息：" + body);
		if (StringUtils.isNotBlank(body)) {
			message = praseJSON(body);
			LOGGER.info("终端[" + ip + "]，响应消息：" + message);
		}
		if (StringUtils.isNotBlank(message)) {
			ByteBuf resp = Unpooled.copiedBuffer(message.getBytes("UTF-8"));
			ctx.write(resp);// 性能考虑，仅将待发送的消息发送到缓冲数组中，再通过调用flush方法，写入channel中
		} else {
			// 清空变量，释放内存
			buf = null;
			req = null;
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

	private String praseJSON(String body) {
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
				List<FtpServer> servers = ftpHandler.getServers();
				for (FtpServer server : servers) {
					if (opt.equals(server.getOpt())) {
						message = server.handler(body);
						break;
					}
				}
			}
		}
		return message;
	}
}
