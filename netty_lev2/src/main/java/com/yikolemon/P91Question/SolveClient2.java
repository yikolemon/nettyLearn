package com.yikolemon.P91Question;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class SolveClient2 {
    public static StringBuilder makeString(char c, int len) {
        StringBuilder sb = new StringBuilder(len + 2);
        for (int i = 0; i < len; i++) {
            sb.append(c);
        }
        sb.append("\n");
        return sb;
    }
    public static void main(String[] args) {
        send();
    }
    public static void send() {

        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        // channel连接建立好之后 出发 channelActive() 时间
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //把所有的数据全都写到一个Buf中，让服务器去解码
                            ByteBuf buf = ctx.alloc().buffer();
                            char c='0';
                            for (int i = 0; i < 10; i++) {
                                int nextInt = new Random().nextInt(256+1);
                                StringBuilder str = makeString(c, nextInt);
                                byte[] bytes = str.toString().getBytes();
                                buf.writeBytes(bytes);
                                c++;
                            }
                            ctx.writeAndFlush(buf);
                        }
                    });
                }
            });
            final ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e)
        {
            log.error("Client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
