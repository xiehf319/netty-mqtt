package com.github.netty.mqtt.broker.launch;

import com.github.netty.mqtt.broker.handler.BrokerHandler;
import com.github.netty.mqtt.broker.handler.MqttMessageHandlerSelector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 10:29
 */
@Component
public class NettyMqttServer {

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    private Channel brokerChannel;

    @Autowired
    private MqttMessageHandlerSelector mqttMessageHandlerSelector;

    @PostConstruct
    public void init() throws Exception {

        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        nettyServer();
    }

    private void nettyServer() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("decoder", new MqttDecoder());
                        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        pipeline.addLast("broker", new BrokerHandler(mqttMessageHandlerSelector));
                    }
                })
        ;
        brokerChannel = bootstrap.bind(10002).sync().channel();
    }

    @PreDestroy
    public void stop() {
        brokerChannel.closeFuture().syncUninterruptibly();
        brokerChannel = null;
    }
}
