package com.yikolemon.Codec;

import com.yikolemon.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MessageCodecTest {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                //使用帧解码器能够解决半包和粘包问题
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(LogLevel.INFO),
                new MessageCodecSharable()
        );
        //encode
        LoginRequestMessage message = new LoginRequestMessage("yiko", "111");
        embeddedChannel.writeOutbound(message);

        //decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buf);
        //入站
        embeddedChannel.writeInbound(buf);
    }
}
