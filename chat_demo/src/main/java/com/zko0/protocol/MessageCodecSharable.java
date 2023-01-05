package com.zko0.protocol;

import com.zko0.config.Config;
import com.zko0.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 在前面必须添加LengthFieldBasedFrameDecoder处理器
 * 确保接到的ByteBuf消息完整
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        //魔数写入
        out.writeBytes(new byte[]{1,2,3,4});
        //版本
        out.writeByte(1);
        //序列化算法 0:jdk 1:json
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        //指令类型
        out.writeByte(msg.getMessageType());
        //4个字节的请求序号
        out.writeInt(msg.getSequenceId());
        //对齐填充用
        out.writeByte(0xff);
        //长度
        //内容字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //长度
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        list.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte();
        //serializerAlgorithm 0或1
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes=new byte[length];
        in.readBytes(bytes,0,length);
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];

        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message deserialize = algorithm.deserialize(messageClass, bytes);
        out.add(deserialize);
    }
}
