package com.yikolemon.HandlerAndPipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipelineTest {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class) //Oio，Nio
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            protected void initChannel(NioSocketChannel ch)
                                    throws Exception {
                                //1.通过channel拿到1pipeline
                                ChannelPipeline pipeline = ch.pipeline();
                                //2.添加处理器
                                //netty会自动添加head和tail Handler，addLast在尾巴前
                                //head -> h1 -> h2 -> h3 -> h4 -> h5 -> h6 ->tail
                                //Inbound入站,从head到tail，而出站从tail到head
                                pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg)
                                            throws Exception {
                                        log.debug("1");
                                        super.channelRead(ctx, msg);
                                    }
                                });
                                pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg)
                                            throws Exception {
                                        log.debug("2");
                                        super.channelRead(ctx, msg);
                                    }
                                });
                                pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg)
                                            throws Exception {
                                        log.debug("3");
                                        super.channelRead(ctx, msg);
                                        //ch.writeAndFlush(ctx.alloc().buffer().writeBytes("fuck".getBytes()));
                                        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("fuck".getBytes()));
                                    }
                                });
                                pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                            throws Exception {
                                        log.debug("4");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                                pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                            throws Exception {
                                        log.debug("5");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                                pipeline.addLast("h6",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                            throws Exception {
                                        log.debug("6");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                            }
                        })
                .bind(8080);
    }
}