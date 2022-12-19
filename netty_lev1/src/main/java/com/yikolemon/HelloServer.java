package com.yikolemon;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        //1.启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2.BossEventLoop， WorkEventLoop(selector,thread)组
                .group(new NioEventLoopGroup())
                //3.选择服务器的ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class) //Oio，Nio
                //4.boss 负责连接，work（child）负责处理读写，决定了work（child）能执行哪些操作（handler）
                .childHandler(
                    //5.channel代表和客户端进行数据读写的通道  初始化（负责添加别的handler）
                    new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());//ByteBuf（不同于ByteBuffer）转化为字符串
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){//自定义handler
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //打印上一步转换的字符串
                                System.out.println(msg);
                            }
                        });
                    }
                })
                //6. 绑定的监听端口
                .bind(8080);
    }
}
