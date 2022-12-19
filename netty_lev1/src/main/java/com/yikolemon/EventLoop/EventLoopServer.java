package com.yikolemon.EventLoop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class EventLoopServer {
    public static void main(String[] args) {
        //细分2，创建一个新的EventLoopGroup，来处理耗时较长的操作
        DefaultEventLoopGroup group = new DefaultEventLoopGroup();

        new ServerBootstrap()
                //boss 和 worker ,把accept和read事件划分的更清晰些
                //细分1：work只负责socketChannel的读写,NIO中分为了两个
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(
                    new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel)
                            throws Exception {
                        channel.pipeline().addLast(
                                new ChannelInboundHandlerAdapter(){
                                    @Override           //ByteBuf
                                    public void channelRead(ChannelHandlerContext ctx, Object msg)
                                            throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        System.out.println(buf.toString(Charset.defaultCharset()));
                                        ctx.fireChannelRead(msg);//*****让消息传递给下个Handler****
                                    }
                                }
                        ).addLast(group,"耗时的group处理",
                                //这个handler不由NioEventLoopGroup中的Worker处理，而是DefaultXXXX来处理了
                                new ChannelInboundHandlerAdapter(){
                                    @Override           //ByteBuf
                                    public void channelRead(ChannelHandlerContext ctx, Object msg)
                                            throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        System.out.println(buf.toString(Charset.defaultCharset()));
                                    }
                                }
                        );
                    }
                })
                .bind(8080);
    }
}
