package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.xabe.avro.v1.MessageEnvelope;
import io.smallrye.faulttolerance.api.CircuitBreakerMaintenance;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;
import io.smallrye.reactive.messaging.kafka.KafkaConsumer;
import javax.enterprise.context.ApplicationScoped;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CarProcessor {

  private final static Logger LOGGER = Logger.getLogger(CarProcessor.class);

  private final Map<Class, EventHandler> handlers;

  private final KafkaClientService kafkaClientService;

  private final ScheduledExecutorService scheduledExecutorService;

  private final KafkaConsumer<Object, Object> consumer;

  private final AtomicInteger counter;

  private Set<TopicPartition> topicPartitions;

  public CarProcessor(final Map<Class, EventHandler> handlers, final KafkaClientService kafkaClientService,
      final CircuitBreakerMaintenance maintenance) {
    this.handlers = handlers;
    this.kafkaClientService = kafkaClientService;
    this.scheduledExecutorService = Executors.newScheduledThreadPool(20);
    this.consumer = this.kafkaClientService.getConsumer("input");
    this.counter = new AtomicInteger(1);
  }

  @Incoming("input")
  public CompletionStage<Void> consumeKafka(final IncomingKafkaRecord<String, MessageEnvelope> message) {
    if (this.counter.get() % 10 == 0) {
      LOGGER.infof("Send stop consumer kafka");
      this.stopKafka();
    }
    return this.consumer(message);
  }

  private void stopKafka() {
    LOGGER.infof("Go to Pause Kafka.");
    this.consumer.pause().subscribe().with(item -> {
      this.topicPartitions = item;
      LOGGER.infof("Is a Pause Kafka for topics {%s}", this.topicPartitions);
      this.scheduledExecutorService.schedule(this::resumeKafka, 5, TimeUnit.SECONDS);
    }, error -> {
      this.scheduledExecutorService.schedule(this::resumeKafka, 5, TimeUnit.SECONDS);
      LOGGER.errorf("Error to Pause Kafka: {%s}", error.getMessage(), error);
    });
  }

  private void resumeKafka() {
    LOGGER.infof("Go to Resume topic");
    this.consumer.resume().subscribe().with(item -> {
      LOGGER.infof("Is a resume Kafka for topics {%s}", this.topicPartitions);
    }, error -> {
      LOGGER.errorf("Error to resume Kafka: {%s}", error.getMessage(), error);
    });
  }

  public CompletionStage<Void> consumer(final IncomingKafkaRecord<String, MessageEnvelope> message) {
    final int i = this.counter.incrementAndGet();
    final Metadata metadata = message.getMetadata();
    LOGGER.infof("Received a message %d. message: {%s} metadata {%s}", i, message, metadata);
    final MessageEnvelope messageEnvelope = message.getPayload();
    final Class<?> msgClass = messageEnvelope.getPayload().getClass();
    final SpecificRecord payload = SpecificRecord.class.cast(messageEnvelope.getPayload());
    final EventHandler handler = this.handlers.get(msgClass);
    if (handler == null) {
      LOGGER.warnf("Received a non supported message. Type: {%s}, toString: {%s}", msgClass.getName(), payload.toString());
    } else {
      handler.handle(payload);
    }
    return message.ack();
  }
}