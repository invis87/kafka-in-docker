# kafka-in-docker
This project contains 4 classes: 2 kafka producers and 2 consumers. All is written in scala. 

Also it contains `docker-compose.yml` with one zookeeper and 2 kafka brokers inside.
## Requirements
You need  `docker`, `docker-compose`, `sbt` installed.
## Run
To start go to `<project-dir>/docker/` and execute `docker-compose up -d` command. 
It will:
* create(if not exists) docker-network with "docker_kafka-network" name.
* start `zookeeper` inside this network, forward `2181` port.
* start `kafka0` and `kafka1` inside this network, forward `9092` and `9093` ports respectively.

## Useful docker commands
Those commands use hostnames because we start them inside docker-network `docker_kafka-network`.

Topic creation: 
```
docker run --rm -i --net=docker_kafka-network ches/kafka \
/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 2 --partitions 3 --topic test
```

Describe topic: 
```
docker run --rm -i --net=docker_kafka-network ches/kafka \
/kafka/bin/kafka-topics.sh --describe --zookeeper zookeeper:2181 --topic test
```

Console producer: 
```
docker run --rm -i --net=docker_kafka-network ches/kafka \
/kafka/bin/kafka-console-producer.sh --topic test --broker-list kafka0:9092,kafka1:9093
```

Console consumer: 
```
docker run --rm -i --net=docker_kafka-network ches/kafka \
/kafka/bin/kafka-console-consumer.sh --zookeeper zookeeper:2181 --topic test
```

## Scala producer/consumer
Inside `<project-dir>` start `sbt`.

Old style:
```
> runMain com.pronvis.kafka.SimpleProducer 10 test localhost:9092
> runMain com.pronvis.kafka.SimpleConsumer localhost:2181 scalaConsumer test 10
```

Reactive style:
```
> runMain com.pronvis.kafka.ReactiveProducer
> runMain com.pronvis.kafka.ReactiveConsumer
```
