# grpc_demo

## Spring boot x gRPC
Refer to the following document and READMEs for the implementation.
* [gRPC](https://www.grpc.io/)
* [LogNet / grpc-spring-boot-starter](https://github.com/LogNet/grpc-spring-boot-starter)
* [Reactive stubs for gRPC](https://github.com/salesforce/reactive-grpc)

## Deadline
[gRPC and Deadlines](https://grpc.io/blog/deadlines/)

> Deadlines allow gRPC clients to specify how long they are willing to wait for an RPC to complete before the RPC is terminated with the error `DEADLINE_EXCEEDED`. By default this deadline is a very large number, dependent on the language implementation.

> In general, when you don’t set a deadline, resources will be held for all in-flight requests, and all requests can potentially reach the maximum timeout. This puts the service at risk of running out of resources, like memory, which would increase the latency of the service, or could crash the entire process in the worst case.

### Implementation
```
val stub = ReactorHelloServiceGrpc
        .newReactorStub(channel)
        .withDeadlineAfter(15, TimeUnit.SECONDS)
return stub.hello(request).map { it.message }
```

Given the following code, Deadline can be set up to 100 years in the implementation ...?
* https://github.com/grpc/grpc-java/blob/a289605/context/src/main/java/io/grpc/Deadline.java#L38-L39
* https://github.com/grpc/grpc-java/blob/a289605/context/src/main/java/io/grpc/Deadline.java#L105-L112

## KeepAlive
It can check periodically the health of the connection by sending an HTTP/2 PING to determine whether the connection is still alive.<br>
And the perception of non-idle connection is created with PING.

### Implementation
#### client

```
val channel = ManagedChannelBuilder.forAddress("localhost", 6567)
        .usePlaintext()
        .keepAliveTime(120, TimeUnit.SECONDS) // the time without read activity before sending a keepalive ping
        .keepAliveTimeout(10, TimeUnit.SECONDS) // the time waiting for read activity after sending a keepalive ping
        .build()
```

#### server

```
@Configuration
class ServerConfiguration: GRpcServerBuilderConfigurer() {
    override fun configure(serverBuilder: ServerBuilder<*>?) {
                super.configure(serverBuilder)
                (serverBuilder as NettyServerBuilder)
                        .keepAliveTime(60, TimeUnit.SECONDS) // the delay time for sending next keepalive ping
                        .keepAliveTimeout(10, TimeUnit.SECONDS) // the timeout for keepalive ping requests
    }
}
```

Given the following code, keepalive of server-side can be set up to between 1 millisec and 1000 days.
* https://github.com/grpc/grpc-java/blob/a289605/netty/src/main/java/io/grpc/netty/NettyServerBuilder.java#L73
* https://github.com/grpc/grpc-java/blob/a289605/netty/src/main/java/io/grpc/netty/NettyServerBuilder.java#L77
* https://github.com/grpc/grpc-java/blob/a289605/netty/src/main/java/io/grpc/netty/NettyServerBuilder.java#L400-L420

####  too_many_pings
We will get following error if send ping in short intervals.

```
io.grpc.StatusRuntimeException: RESOURCE_EXHAUSTED: Bandwidth exhausted
HTTP/2 error code: ENHANCE_YOUR_CALM
Received Goaway
too_many_pings
```

It is able to set a lower keepalive like this.

```
@Configuration
class ServerConfiguration: GRpcServerBuilderConfigurer() {
    override fun configure(serverBuilder: ServerBuilder<*>?) {
                super.configure(serverBuilder)
                (serverBuilder as NettyServerBuilder)
                        .permitKeepAliveTime(60, TimeUnit.SECONDS) // the most aggressive keep-alive time clients are permitted to configure
    }
}
```
