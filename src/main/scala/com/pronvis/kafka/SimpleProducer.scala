package com.pronvis.kafka

import java.time.Instant
import java.util.Properties
import java.util.concurrent.{Future => JFuture}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Promise, Future => SFuture}
import scala.util.{Failure, Random, Success, Try}

object SimpleProducer extends App {
  val events = args(0).toInt
  val topic = args(1)
  val brokers = args(2)
  println(s"Starting producer: #events: $events; topic: $topic; brokers: $brokers")
  val rnd = new Random()
  val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("producer.type", "async")
  props.put("acks", "all")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val t = System.currentTimeMillis()
  for (nEvents <- Range(0, events)) {
    val runtime = Instant.now()
    val ip = "192.168.2." + rnd.nextInt(255)
    val msg = runtime + "," + nEvents + ",www.example.com," + ip
    val data = new ProducerRecord(topic, ip, msg)
    val result: JFuture[RecordMetadata] = producer.send(data)

    val promise = Promise[RecordMetadata]()
    new Thread(new Runnable { def run() { promise.complete(Try{ result.get }) }}).start()
    val future = promise.future
    future.onComplete {
      case Failure(e) => println("Failed send message because: ", e)
      case Success(data) => println(s"data $data sended!")
    }
  }

  producer.close()
  Thread.sleep(5000)
}
