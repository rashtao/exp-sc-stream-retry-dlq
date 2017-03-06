package com.example

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat


@EnableScheduling
@SpringBootApplication
class Consumer

fun main(args: Array<String>) {
    SpringApplication.run(Consumer::class.java, *args)
}


@Service
@EnableBinding(Processor::class)
class MyConsumer {

    private val log = LoggerFactory.getLogger(MyConsumer::class.java)

    @SendTo(Processor.OUTPUT)
    @StreamListener(Processor.INPUT)
    fun handle(data: String): String {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = sdfDate.parse(data)
        val seconds = date.seconds

        log.info("received: $data --> $seconds")

        if (seconds % 2 == 0) {
            log.warn("throwing error...")
            throw RuntimeException()
        }

        return data
    }

}

interface Processor {
    companion object {
        const val INPUT = "input"
        const val OUTPUT = "output"
    }

    @Output(Processor.OUTPUT)
    fun output(): MessageChannel

    @Input(Processor.INPUT)
    fun input(): SubscribableChannel
}

