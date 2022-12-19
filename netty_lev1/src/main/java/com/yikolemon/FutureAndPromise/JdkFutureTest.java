package com.yikolemon.FutureAndPromise;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;
@Slf4j
public class JdkFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        //2.提交任务
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 50;
            }
        });
        //3.主线程获取future获取结果
        log.debug("等待结果");
        Integer res = future.get();
        log.debug("res= {}",res);
    }
}
