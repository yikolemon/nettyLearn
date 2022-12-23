package com.yikolemon.Codec;

import com.yikolemon.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 自定义编解码器
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //魔数写入
        out.writeBytes(new byte[]{1,2,3,4});
        //版本
        out.writeByte(1);
        //序列化算法 0:jdk 1:json
        out.writeByte(0);
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
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes=new byte[length];
        in.readBytes(bytes,0,length);
        if (serializerType==0){
            //使用jdk反序列化
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Message message = (Message)objectInputStream.readObject();
            log.info("{},{},{},{},{},{}",magicNum,version,serializerType,messageType,sequenceId,length);
            log.info("{}",message);
            out.add(message);
        }
    }
}
