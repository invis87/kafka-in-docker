package com.pronvis.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

object ReactiveProducer {
  implicit val system = ActorSystem("reactive-producer")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092,localhost:9093")

    Source(1 to 100)
      .map(i => s"message #$i")
      .map(elem => new ProducerRecord[Array[Byte], String]("test", elem))
      .to(Producer.plainSink(producerSettings))
      .run()

    Thread.sleep(1000)
    system.terminate()
  }
}
