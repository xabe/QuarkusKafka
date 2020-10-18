package com.xabe.quarkus.kafka.producer.infrastructure.integration;

import com.xabe.avro.v1.MessageEnvelope;
import groovy.lang.Tuple2;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.logging.Logger;

public class KafkaConsumer {

  private static final BlockingQueue<Tuple2<String, MessageEnvelope>> MESSAGE_KAFKA = new ArrayBlockingQueue<>(100);

  private static final Logger LOGGER = Logger.getLogger(KafkaConsumer.class);

  private static Consumer consumer;

  public static void create() throws InterruptedException {
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
    consumer = new Consumer(properties);
  }

  public static void before() {
    MESSAGE_KAFKA.clear();
  }

  public static void close() {
    consumer.stop();
  }

  public static Tuple2<String, MessageEnvelope> expectMessagePipe(final Class payloadClass, final long milliseconds)
      throws InterruptedException {
    final Tuple2<String, MessageEnvelope> message = MESSAGE_KAFKA.poll(milliseconds, TimeUnit.MILLISECONDS);
    if (message == null) {
      throw new RuntimeException("An exception happened while polling the queue for " + payloadClass.getName());
    }
    if (!message.getV2().getPayload().getClass().equals(payloadClass)) {
      throw new AssertionError("payload cant be casted");
    }
    return message;
  }

  private static class Consumer {

    private final TopicPartition topicPartition;

    private final org.apache.kafka.clients.consumer.Consumer<String, MessageEnvelope> consumer;

    private final ExecutorService executor;

    private final AtomicBoolean start = new AtomicBoolean(true);

    public Consumer(final Properties properties) {
      this.topicPartition = new TopicPartition("car.v1", 0);
      this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);
      this.consumer.assign(Collections.singleton(this.topicPartition));
      this.consumer.seekToEnd(Collections.singleton(this.topicPartition));
      this.executor = Executors.newSingleThreadExecutor();

      LOGGER.info("Waiting for data...");

      this.executor.submit(() -> {
        while (this.start.get()) {
          LOGGER.info("Polling...");
          final ConsumerRecords<String, MessageEnvelope> records = this.consumer.poll(Duration.ofMillis(250));

          for (final ConsumerRecord<String, MessageEnvelope> record : records) {
            try {
              MESSAGE_KAFKA.put(Tuple2.tuple(record.key(), record.value()));
              LOGGER.infof("Received event key: {%s} message: {%s}", record.key(), record.value());
            } catch (final Exception e) {
              e.printStackTrace();
            }
          }
          this.consumer.commitSync();
        }
        this.consumer.endOffsets(Collections.singleton(this.topicPartition));
        this.consumer.close();
      });
    }

    public void stop() {
      this.start.set(false);
      this.consumer.wakeup();
      this.executor.shutdown();
    }
  }
}
