package com.zko0.client;


import com.zko0.message.RpcRequestMessage;
import com.zko0.protocol.MessageCodecSharable;
import com.zko0.protocol.ProcotolFrameDecoder;
import com.zko0.protocol.SequenceIdGenerator;
import com.zko0.server.handler.RpcResponseMessageHandler;
import com.zko0.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientMananger {
    private static final Object LOCK=new Object();
    private static Channel channel=null;

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK){
            if (channel!=null){
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    //初始化channel方法
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }

    public static void main(String[] args) {
        HelloService proxyService = getProxyService(HelloService.class);
        System.out.println(proxyService.sayHi("尼玛"));
        //System.out.println(proxyService.sayHi("我测"));
        //System.out.println(proxyService.sayHi("fuckU"));
    }

    //创建代理类
    public static <T>T getProxyService(Class<T> serviceClass){
        ClassLoader loader=serviceClass.getClassLoader();
        Class<?>[] interfaces=new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            //1.将方法调用转化为消息对象\
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            //准备一个Promise对象，来接受结果                   指定promise对象异步接收结果线程
            DefaultPromise<Object> promise=new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId,promise);

            getChannel().writeAndFlush(msg);
            //等待promise结果
            promise.await();
            if (promise.isSuccess()) {
                //调用正常
                return promise.getNow();
            }else {
                //调用失败
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

}
