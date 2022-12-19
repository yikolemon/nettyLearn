package com.yikolemon.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.concurrent.TimeUnit;

public class TestEventLoop {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(2);//io事件，普通的任务，定时的任务
        //NioEventLoopGroup的构造方法，对线程数的定义，如果默认设置为0，那么设定为电脑核心数*2
//      DefaultEventLoopGroup group = new DefaultEventLoopGroup();//普通的任务，定时的任务
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        //3.执行普通的任务,或者使用execute
        group.next().submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("thread");
        });
        System.out.println("main");
        //4.定时任务
        group.next().scheduleAtFixedRate(()->{
            System.out.println("定时任务");
        },0,1, TimeUnit.SECONDS);
    }
}
