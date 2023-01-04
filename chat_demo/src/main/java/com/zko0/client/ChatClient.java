package com.zko0.client;

import com.zko0.message.*;
import com.zko0.protocol.MessageCodecSharable;
import com.zko0.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_IN = new CountDownLatch(1);
        AtomicBoolean LOGIN=new AtomicBoolean();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            //触发读共享事件
                            if (event.state()== IdleState.WRITER_IDLE){
                                log.info("3s没有写数据了");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    ch.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){
                        @Override
                        //连接触发后此方法启动
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(()->{
                                //负责接受用户在控制台的输入，负责向服务器发送各种消息
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("输入密码");
                                String password = scanner.nextLine();
                                //构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                //发送消息
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续");
                                try {
                                    WAIT_FOR_IN.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                if (!LOGIN.get()){
                                    ctx.channel().close();
                                    return;
                                }
                                while (true){
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":
                                            ChatRequestMessage msg = new ChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(msg);
                                            break;
                                        case "gsend":
                                            GroupChatRequestMessage groupMsg = new GroupChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(groupMsg);
                                            break;
                                        case "gcreate":
                                            List<String> list = Arrays.asList(s[2].split(","));
                                            HashSet<String> set = new HashSet<>(list);
                                            set.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1],set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username,s[1]));
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username,s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            break;
                                    }
                                }
                            },"system in").start();
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg:{}",msg);
                            if (msg instanceof LoginResponseMessage){
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()){
                                    //登陆成功
                                    LOGIN.set(true);
                                }
                                //唤醒system in线程
                                WAIT_FOR_IN.countDown();
                            }
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
