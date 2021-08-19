package com.xabe.quarkus.kafka.producer.infrastructure.integration.kafka;

import com.xabe.avro.v1.MessageEnvelope;
import groovy.lang.Tuple2;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.util.Properties;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaConsumer {

  private final Consumer consumer;

  public KafkaConsumer() {
    final Properties properties = new Properties();
    // normal consumer
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
    properties.setProperty("group.id", "customer-consumer-group-v1");
    properties.setProperty("auto.commit.enable", "false");
    properties.setProperty("auto.offset.reset", "latest");

    // avro part
    properties.setProperty("key.deserializer", StringDeserializer.class.getName());
    properties.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
    properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
    properties.setProperty("use.latest.version", "true");
    properties.setProperty("auto.register.schemas", "false");
    properties.setProperty("specific.avro.reader", "true");
    this.consumer = new Consumer(properties);
  }

  public void before() {
    this.consumer.clear();
  }

  public void close() {
    this.consumer.stop();
  }

  public Tuple2<String, MessageEnvelope> expectMessagePipe(final Class payloadClass, final long milliseconds)
      throws InterruptedException {
    final Tuple2<String, MessageEnvelope> message = this.consumer.poll(milliseconds);
    if (message == null) {
      throw new RuntimeException("An exception happened while polling the queue for " + payloadClass.getName());
    }
    if (!message.getV2().getPayload().getClass().equals(payloadClass)) {
      throw new AssertionError("payload cant be casted");
    }
    return message;
  }
}
