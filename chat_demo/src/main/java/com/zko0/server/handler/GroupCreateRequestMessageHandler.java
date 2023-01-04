package com.zko0.server.handler;

import com.zko0.message.GroupCreateRequestMessage;
import com.zko0.message.GroupCreateResponseMessage;
import com.zko0.server.session.Group;
import com.zko0.server.session.GroupSession;
import com.zko0.server.session.GroupSessionFactory;
import com.zko0.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable

public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        //群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group==null){
            //创建成功
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入"+groupName));
            }
            //成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建成功"));
        }else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName+"已经存在"));
        }

    }
}
