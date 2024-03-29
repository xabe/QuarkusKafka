package com.xabe.quarkus.kafka.consumer.infrastructure.integration.kafka;

import com.xabe.avro.v1.MessageEnvelope;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import java.util.function.Supplier;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

  private final Producer<String, MessageEnvelope> producer;

  public KafkaProducer() {
    final Properties properties = new Properties();
    // normal producer
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
    properties.setProperty("acks", "all");
    properties.setProperty("max-inflight-messages", "5");
    properties.setProperty("compression.type", "none");
    properties.setProperty("enable.idempotence", "true");
    properties.setProperty("retries", "10");
    properties.setProperty("request.timeout.ms", "30000");
    properties.setProperty("max.block.ms", "1000");
    properties.setProperty("linger.ms", "20");
    properties.setProperty("batch.size", "32768");
/*    properties.setProperty("auto.create.topics.enable", "true");
    properties.setProperty("allow.auto.create.topics", "true");
    properties.setProperty("num.partitions", "3");
    properties.setProperty("min.insync.replicas", "2");*/
    // avro part
    properties.setProperty("key.serializer", StringSerializer.class.getName());
    properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
    properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
    properties.setProperty("use.latest.version", "true");
    properties.setProperty("auto.register.schemas", "false");

    this.producer = new org.apache.kafka.clients.producer.KafkaProducer(properties);
  }

  public void send(final MessageEnvelope messageEnvelope, final Supplier<String> getKey) {
    final ProducerRecord<String, MessageEnvelope> producerRecord = new ProducerRecord<>(
        "car.v1", getKey.get(), messageEnvelope
    );
    this.producer.send(producerRecord, (metadata, exception) -> {
      if (exception == null) {
        LOGGER.info("Send event : {}", metadata);
      } else {
        LOGGER.error("Error send event : {}", exception.getMessage(), exception);
      }
    });
    this.producer.flush();
  }

  public void close() {
    this.producer.close();
  }
}
