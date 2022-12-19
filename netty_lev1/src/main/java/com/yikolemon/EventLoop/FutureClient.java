package com.yikolemon.EventLoop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class FutureClient {
    public static void main(String[] args) throws InterruptedException {
        //关闭client，需要释放调这个group的资源
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //1.添加编码器 String转ByteBuf
                        nioSocketChannel.pipeline().addLast(new LoggingHandler());
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String s = scanner.nextLine();
                if ("q".equals(s)) {
                    channel.close();
                    //log.debug("处理关闭之后的操作");
                    break;
                }
                channel.writeAndFlush(s);
            }
        },"input-thread").start();
        //获取CloseFuture对象：1）同步模式关闭 2）异步模式关闭
        ChannelFuture closeFuture= channel.closeFuture();
        //由close执行的线程来执行这个方法
        closeFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            log.debug("关闭之后的操作");
            //优雅的关闭掉group，先拒绝新任务，等待现在的任务完成
            group.shutdownGracefully();
        });
    }
}
