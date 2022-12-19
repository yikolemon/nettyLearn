package com.yikolemon.ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteBUfTest {
    //测试ByteBuf的自动扩容机制
    public static void main(String[] args) {
        //扩容前的ByteBUf
        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buf);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 300; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
        //扩容后的ByteBuf，cap代表容量
        System.out.println(buf);
    }
}
