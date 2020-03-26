package com.example.demo

import com.example.demo.protobuf.Hello
import com.example.demo.protobuf.HelloServiceGrpc
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class HelloService: HelloServiceGrpc.HelloServiceImplBase() {
    override fun hello(request: Hello.MessageRequest, responseObserver: StreamObserver<Hello.MessageResponse>) {
        Thread.sleep(1000L*30)
        val array = Array(request.count) { request.message }
        val response = Hello.MessageResponse.newBuilder().setMessage(array.joinToString(" ")).build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun helloStream(request: Hello.MessageRequest, responseObserver: StreamObserver<Hello.MessageResponse>) {
        for (i in 1..request.count){
            Thread.sleep(1000L*60*i)
            responseObserver.onNext(Hello.MessageResponse
                    .newBuilder()
                    .setMessage(request.message)
                    .build())
        }
        responseObserver.onCompleted()
    }
}
