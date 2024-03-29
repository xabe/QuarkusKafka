package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.xabe.avro.v1.Car;
import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.avro.v1.MessageEnvelope;
import com.xabe.avro.v1.Metadata;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.smallrye.reactive.messaging.kafka.commit.KafkaCommitHandler;
import io.smallrye.reactive.messaging.kafka.commit.KafkaIgnoreCommit;
import io.smallrye.reactive.messaging.kafka.fault.KafkaFailureHandler;
import io.smallrye.reactive.messaging.kafka.fault.KafkaIgnoreFailure;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarProcessorTest {

  private EventHandler eventHandler;

  private CarProcessor carProcessor;

  @BeforeEach
  public void setUp() throws Exception {
    this.eventHandler = mock(EventHandler.class);
    this.carProcessor = new CarProcessor(Map.of(CarCreated.class, this.eventHandler));
  }

  @Test
  @DisplayName("Should handler event")
  public void shouldHandlerEvent() throws Exception {
    final Instant now = Instant.now();
    final Car car = Car.newBuilder().setId("id").setName("name").build();
    final CarCreated carCreated = CarCreated.newBuilder().setSentAt(now).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carCreated)
        .build();
    final ConsumerRecord<String, MessageEnvelope> consumerRecord = new ConsumerRecord<>("topic", 1, 1L, "key", messageEnvelope);
    final KafkaCommitHandler kafkaCommitHandler = new KafkaIgnoreCommit();
    final KafkaFailureHandler kafkaFailureHandler = new KafkaIgnoreFailure("channel");
    final IncomingKafkaRecord<String, MessageEnvelope> incomingKafkaRecord =
        new IncomingKafkaRecord<>(consumerRecord, kafkaCommitHandler, kafkaFailureHandler, false, false);

    final CompletionStage result = this.carProcessor.consumeKafka(incomingKafkaRecord);

    assertThat(result, is(notNullValue()));
    assertThat(result.toCompletableFuture().get(), is(nullValue()));
    verify(this.eventHandler).handle(any());
  }

  @Test
  @DisplayName("Not should handler event")
  public void notShouldHandlerEvent() throws Exception {
    final Instant now = Instant.now();
    final Car car = Car.newBuilder().setId("id").setName("name").build();
    final CarUpdated carUpdated = CarUpdated.newBuilder().setSentAt(now).setCar(car).setCarBeforeUpdate(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carUpdated)
        .build();
    final ConsumerRecord<String, MessageEnvelope> consumerRecord = new ConsumerRecord<>("topic", 1, 1L, "key", messageEnvelope);
    final KafkaCommitHandler kafkaCommitHandler = new KafkaIgnoreCommit();
    final KafkaFailureHandler kafkaFailureHandler = new KafkaIgnoreFailure("channel");
    final IncomingKafkaRecord<String, MessageEnvelope> incomingKafkaRecord =
        new IncomingKafkaRecord<>(consumerRecord, kafkaCommitHandler, kafkaFailureHandler, false, false);

    final CompletionStage result = this.carProcessor.consumeKafka(incomingKafkaRecord);

    assertThat(result, is(notNullValue()));
    assertThat(result.toCompletableFuture().get(), is(nullValue()));
    verify(this.eventHandler, never()).handle(any());
  }

  private Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("car").setName("car").setAction("update").setVersion("vTest")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }
}