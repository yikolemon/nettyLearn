package com.zko0.server.handler;

import com.zko0.message.LoginRequestMessage;
import com.zko0.message.LoginResponseMessage;
import com.zko0.server.service.UserServiceFactory;
import com.zko0.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable

public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage loginRequestMessage) throws Exception {
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage responseMessage = null;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            responseMessage = new LoginResponseMessage(true, "login suc");
        } else {
            responseMessage = new LoginResponseMessage(false, "login fail");
        }
        ctx.writeAndFlush(responseMessage);
    }

}
