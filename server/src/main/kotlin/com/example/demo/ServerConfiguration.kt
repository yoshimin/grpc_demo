package com.example.demo

import io.grpc.ServerBuilder
import io.grpc.netty.NettyServerBuilder
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class ServerConfiguration: GRpcServerBuilderConfigurer() {
    override fun configure(serverBuilder: ServerBuilder<*>?) {
                super.configure(serverBuilder)
                (serverBuilder as NettyServerBuilder)
                        .permitKeepAliveTime(5, TimeUnit.SECONDS)
    }
}
