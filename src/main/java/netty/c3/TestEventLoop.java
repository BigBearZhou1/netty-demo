package netty.c3;

import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
//        System.out.println(NettyRuntime.availableProcessors());
        log.info("availableProcessors = {}",NettyRuntime.availableProcessors());
    }
}
