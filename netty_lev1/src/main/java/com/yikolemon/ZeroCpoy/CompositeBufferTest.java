package com.yikolemon.ZeroCpoy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class CompositeBufferTest {
    public static void main(String[] args) {
        ByteBuf buf1= ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{'a','b','c','d','e'});
        ByteBuf buf2= ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{'f','g','h','i','j'});
        CompositeByteBuf bufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        bufs.addComponents(true,buf1,buf2);
        log(bufs);
    }
    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
