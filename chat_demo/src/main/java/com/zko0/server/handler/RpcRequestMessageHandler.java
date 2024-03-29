package com.zko0.server.handler;

import com.zko0.message.RpcRequestMessage;
import com.zko0.message.RpcResponseMessage;
import com.zko0.server.service.HelloService;
import com.zko0.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());
        try {
            HelloService service = (HelloService)ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String wrong = e.getCause().getMessage();
            response.setExceptionValue(new Exception("远程调用出错"+wrong));
        }
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage msg = new RpcRequestMessage(1,
                "com.zko0.server.service.HelloService",
                "sayHi",
                String.class,
                new Class[]{String.class},
                new Object[]{"Yiko"}
        );
        HelloService service = (HelloService)ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
        Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
        Object invoke = method.invoke(service, msg.getParameterValue());
        System.out.println(invoke);
    }
}
