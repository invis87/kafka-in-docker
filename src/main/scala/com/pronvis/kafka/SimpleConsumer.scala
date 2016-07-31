package com.pronvis.kafka

import java.util.Properties
import java.util.concurrent._

import kafka.consumer.{Consumer, ConsumerConfig, KafkaStream}
import kafka.utils.Logging

class SimpleConsumer(val zookeeper: String,
  val groupId: String,
  val topic: String) extends Logging {

  val config = createConsumerConfig(zookeeper, groupId)
  val consumer = Consumer.create(config)
  var executor: ExecutorService = null

  def shutdown() = {
    if (consumer != null)
      consumer.shutdown()
    if (executor != null)
      executor.shutdown()
  }

  def createConsumerConfig(zookeeper: String, groupId: String): ConsumerConfig = {
    val props = new Properties()
    props.put("zookeeper.connect", zookeeper)
    props.put("group.id", groupId)
    props.put("auto.offset.reset", "largest")
    props.put("zookeeper.session.timeout.ms", "400")
    props.put("zookeeper.sync.time.ms", "200")
    props.put("auto.commit.interval.ms", "1000")
    val config = new ConsumerConfig(props)
    config
  }

  def run(numThreads: Int) = {
    val topicCountMap = Map(topic -> numThreads)
    val consumerMap = consumer.createMessageStreams(topicCountMap)
    val streams = consumerMap(topic)

    executor = Executors.newFixedThreadPool(numThreads)
    var threadNumber = 0
    for (stream <- streams) {
      executor.submit(new ScalaConsumerTest(stream, threadNumber))
      threadNumber += 1
    }
  }
}

object SimpleConsumer extends App {
  val zookeeper = args(0)
  val groupId = args(1)
  val topic = args(2)
  val threadsNumber = args(3).toInt

  println(s"Create consumer for zookeeper: $zookeeper; groupId: $groupId; topic: $topic; threadsNumber: $threadsNumber")

  val example = new SimpleConsumer(zookeeper, groupId, topic)
  example.run(threadsNumber)
}

class ScalaConsumerTest(val stream: KafkaStream[Array[Byte], Array[Byte]], val threadNumber: Int) extends Logging with Runnable {
  def run() {
    val it = stream.iterator()

    while (it.hasNext()) {
      val msg = new String(it.next().message())
      System.out.println(System.currentTimeMillis() + ",Thread " + threadNumber + ": " + msg)
    }

    System.out.println("Shutting down Thread: " + threadNumber)
  }
}
