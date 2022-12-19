package com.yikolemon.FutureAndPromise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class NettyPromiseTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //1.可以主动创建promise
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(()->{
                //3.任何一个线程执行计算
                log.debug("计算结果");
                try {
                    Thread.sleep(1000);
                    promise.setSuccess(30);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    promise.setFailure(e);
                }
            }
        );
        //4.接受结果
        log.debug("等待结果");
        log.debug("结果为：{}",promise.get());

    }
}
