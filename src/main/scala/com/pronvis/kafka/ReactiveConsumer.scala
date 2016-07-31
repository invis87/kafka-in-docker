package com.pronvis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl._
import akka.kafka.{ConsumerMessage, _}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

object ReactiveConsumer {

  implicit val system = ActorSystem("reactive-consumer")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {

    val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092,localhost:9093")
      .withGroupId("reactiveGroup")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    val subscription = Subscriptions.topics("test")
    Consumer.atMostOnceSource(consumerSettings.withClientId("client1"), subscription)
      .to(Sink.foreach(receiveMsg))
      .run()
  }

  def receiveMsg(msg: ConsumerMessage.Message[Array[Byte], String]): Unit = {
    println(s"consume message: ${msg.value}; offset: ${msg.partitionOffset}")
  }
}