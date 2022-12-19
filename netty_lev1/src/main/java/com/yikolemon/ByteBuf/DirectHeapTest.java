package com.yikolemon.ByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class DirectHeapTest {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.directBuffer();
        System.out.println(buf1.getClass());
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        System.out.println(buf.getClass());
    }
}
