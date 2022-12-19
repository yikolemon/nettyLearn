package com.yikolemon;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        //1.启动器
        new Bootstrap()
                //2.添加EventLopp
                .group(new NioEventLoopGroup())
                //3.选择客户端Channel实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //连接建立后会调用
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //1.添加编码器 String转ByteBuf
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost",8080))
                .sync()
                .channel()
                //6.向服务器发送数
                .writeAndFlush("hello,fuck this worlld");
    }
}
