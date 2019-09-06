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
* start `kafka0`, `kafka1` and `kafka2` inside this network, forward `9092` and `9093` ports respectively.

## Connect to nodes
* if you want to connect to nodes (consume, produce events, describe topics, etc) from your host, then you should use `localhost` with `9092`, `9093` and `9094` kafka ports. 
* if you connect to kafka from inside docker network, then use `kafka0:29092`, `kafka1:29092` and `kafka2:29092`

## Useful docker commands
Those commands use hostnames because we start them inside docker-network `docker_kafka-network`.

Topic creation: 
```
docker run --rm -i --net=docker_kafka-network confluentinc/cp-kafka:5.3.0 \
kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 2 --partitions 3 --topic test
```

Describe topic: 
```
docker run --rm -i --net=docker_kafka-network confluentinc/cp-kafka:5.3.0 \
kafka-topics --describe --zookeeper zookeeper:2181 --topic test
```

Console producer: 
```
docker run --rm -i --net=docker_kafka-network confluentinc/cp-kafka:5.3.0 \
kafka-console-producer --topic test --broker-list kafka0:29092,kafka1:29092
```

Console consumer: 
```
docker run --rm -i --net=docker_kafka-network confluentinc/cp-kafka:5.3.0 \
kafka-console-consumer --bootstrap-server kafka0:29092 --topic test
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
