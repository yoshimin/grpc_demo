package com.example.demo

import com.example.demo.protobuf.Hello
import com.example.demo.protobuf.ReactorHelloServiceGrpc
import io.grpc.ManagedChannelBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/grpc")
class TestController {

    @GetMapping("/unary")
    fun unary(@RequestParam message: String, @RequestParam count: Int): Mono<String> {
        val channel = ManagedChannelBuilder.forAddress("localhost", 6567)
                .usePlaintext()
                .build()
        val request = Hello.MessageRequest.newBuilder()
                .setMessage(message)
                .setCount(count)
                .build()

        val stub = ReactorHelloServiceGrpc
                .newReactorStub(channel)
                .withDeadlineAfter(15, TimeUnit.SECONDS)
        return stub.hello(request).map { it.message }.doOnError { println("$it") }
    }

    @GetMapping("/stream")
    fun stream(@RequestParam message: String, @RequestParam count: Int): Mono<String> {
        val channel = ManagedChannelBuilder.forAddress("localhost", 6567)
                .usePlaintext()
                .keepAliveTime(10, TimeUnit.SECONDS)
                .keepAliveTimeout(10, TimeUnit.SECONDS)
                .build()
        val request = Hello.MessageRequest.newBuilder()
                .setMessage(message)
                .setCount(count)
                .build()

        val stub = ReactorHelloServiceGrpc.newReactorStub(channel)
        return stub.helloStream(request)
                .doOnNext { println(Date().toString()) }
                .collectList()
                .map {
                    it.joinToString(separator = " ") { reply ->  reply.message }
                }
    }
}
