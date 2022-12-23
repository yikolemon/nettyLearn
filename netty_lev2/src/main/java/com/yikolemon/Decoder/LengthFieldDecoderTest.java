package com.yikolemon.Decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class LengthFieldDecoderTest {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                //int length是4个字节
                new LengthFieldBasedFrameDecoder(1024,0,4,4,0),
                new LoggingHandler(LogLevel.INFO)
        );
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        send(buf,"Fuck u");
        send(buf,"Shit");
        channel.writeInbound(buf);
    }
    public static void send(ByteBuf buf,String str) {
        //4个字节的内容长度，    实际内容
        byte[] bytes =str.getBytes();
        int length = bytes.length;//实际内容长度
        buf.writeInt(length);
        //假装写入了一个版本号，所有需要adjustment对此（header）造成的影响进行修正
        buf.writeBytes("why:".getBytes());
        buf.writeBytes(bytes);
    }
}
