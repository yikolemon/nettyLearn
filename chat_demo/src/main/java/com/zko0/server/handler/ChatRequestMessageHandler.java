package com.zko0.server.handler;

import com.zko0.message.ChatRequestMessage;
import com.zko0.message.ChatResponseMessage;
import com.zko0.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel!=null){
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else {
            //离线
            channelHandlerContext.writeAndFlush(new ChatResponseMessage(false,"对方不在线或者不存在"));
        }
    }
}
