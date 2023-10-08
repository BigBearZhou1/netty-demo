package netty.c3.future;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class NettyPromiseDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup eventLoop = new NioEventLoopGroup();

        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop.next());

        new Thread(() -> {
            log.info("开始计算...");
            try {
                int i = 1/0;
                Thread.sleep(1000);
                promise.setSuccess(1);
            } catch (Exception e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();

        log.info("等待结果...");
        log.info("结果是 = {}", promise.isSuccess());
    }
}
