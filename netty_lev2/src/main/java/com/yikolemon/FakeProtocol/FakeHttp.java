package com.yikolemon.FakeProtocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class FakeHttp {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,worker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                ch.pipeline().addLast(new HttpServerCodec());
                /*ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("{}",msg.getClass());
                        if (msg instanceof HttpRequest){
                            //请求行，请求头
                        }
                        else if (msg instanceof HttpContent){
                            //请求体
                        }
                    }
                });*/
                ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) throws Exception {
                        String uri = httpRequest.getUri();
                        log.info(uri);
                        DefaultFullHttpResponse response =
                                new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
                        byte[] bytes = "<h1>Fuck this</h1>".getBytes();
                        response.content().writeBytes(bytes);
                        //告诉浏览器，content长度，防止浏览器一直读取
                        response.headers().setInt(CONTENT_LENGTH,bytes.length);
                        channelHandlerContext.writeAndFlush(response);
                    }
                });
            }
        });
        ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
