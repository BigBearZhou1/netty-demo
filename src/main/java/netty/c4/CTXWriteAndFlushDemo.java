package netty.c4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * in1 -> out1 -> out2 -> in2 -> out3
 * in2中ctx.writeAndFlush()方法的出栈会时out2 -> out1，不会走到out3的处理
 * ch.writeAndFlush()会走out3 -> out2 -> out1
 */
@Slf4j
public class CTXWriteAndFlushDemo {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("inbound 1");
                                ByteBuf buf = (ByteBuf) msg;
                                String msgStr = buf.toString(StandardCharsets.UTF_8);
                                super.channelRead(ctx, msgStr);
                            }
                        });

                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("out 1");
                                super.write(ctx, msg, promise);
                            }
                        });

                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("out 2");
                                super.write(ctx, msg, promise);
                            }
                        });

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("inbound 2");
                                log.info("msg = {}", msg);
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("hello".getBytes()));
                            }
                        });

                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("out 3");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);

    }
}
