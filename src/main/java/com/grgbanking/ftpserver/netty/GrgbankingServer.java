package com.grgbanking.ftpserver.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrgbankingServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GrgbankingServer.class);
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	//监听端口
	private int port;
    
    public GrgbankingServer(int port) {
    	this.port = port;
    }

    public void run() throws Exception {
    	//配置服务器端的NIO线程组
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 //ch.pipeline().addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                	 //ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                	 //以("\n")为结尾分割的 解码器
                	 //ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
                	 ch.pipeline().addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                	 //ch.pipeline().addLast(new StringDecoder());
                     ch.pipeline().addLast(new GrgbankingServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
            System.out.println(f.channel().localAddress());
            LOGGER.info("FTP服务启动成功，在端口[" + port + "]监听服务");
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
        	//System.out.println("finally");
        	LOGGER.info("指静脉服务正在关闭中...finally");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    public void shutdown() {
    	if (workerGroup != null && bossGroup != null) {
    		try {
    			System.out.println("指静脉服务正在关闭中...");
				workerGroup.shutdownGracefully().syncUninterruptibly();
				bossGroup.shutdownGracefully().syncUninterruptibly();
				System.out.println("指静脉服务关闭成功");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("指静脉服务关闭异常");
			}
            
    	} 
    }
}
