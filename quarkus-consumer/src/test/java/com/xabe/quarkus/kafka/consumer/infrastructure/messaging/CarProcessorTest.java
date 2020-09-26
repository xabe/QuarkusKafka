package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;

import com.xabe.avro.v1.*;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.smallrye.reactive.messaging.kafka.commit.KafkaCommitHandler;
import io.smallrye.reactive.messaging.kafka.commit.KafkaIgnoreCommit;
import io.smallrye.reactive.messaging.kafka.fault.KafkaFailureHandler;
import io.smallrye.reactive.messaging.kafka.fault.KafkaIgnoreFailure;
import io.vertx.kafka.client.consumer.impl.KafkaConsumerRecordImpl;
import io.vertx.mutiny.kafka.client.consumer.KafkaConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        final Car car = Car.newBuilder().setId("id").setName("name").build();
        final CarCreated carCreated = CarCreated.newBuilder().setSentAt(System.currentTimeMillis()).setCar(car).build();
        final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carCreated)
                .build();
        final ConsumerRecord consumerRecord = new ConsumerRecord("topic", 1, 1L, "key", messageEnvelope);
        final KafkaConsumerRecord kafkaConsumerRecord = KafkaConsumerRecord.newInstance(new KafkaConsumerRecordImpl(consumerRecord));
        final KafkaCommitHandler kafkaCommitHandler = new KafkaIgnoreCommit();
        final KafkaFailureHandler kafkaFailureHandler = new KafkaIgnoreFailure("channel");
        final IncomingKafkaRecord incomingKafkaRecord = new IncomingKafkaRecord<>(kafkaConsumerRecord, kafkaCommitHandler, kafkaFailureHandler);

        final CompletionStage result = this.carProcessor.consumeKafka(incomingKafkaRecord);

        assertThat(result, is(notNullValue()));
        assertThat(result.toCompletableFuture().get(), is(nullValue()));
        verify(this.eventHandler).handle(any());
    }

    @Test
    @DisplayName("Not should handler event")
    public void notShouldHandlerEvent() throws Exception {
        final Car car = Car.newBuilder().setId("id").setName("name").build();
        final CarUpdated carUpdated = CarUpdated.newBuilder().setSentAt(System.currentTimeMillis()).setCar(car).setCarBeforeUpdate(car).build();
        final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carUpdated)
                .build();
        final ConsumerRecord consumerRecord = new ConsumerRecord("topic", 1, 1L, "key", messageEnvelope);
        final KafkaConsumerRecord kafkaConsumerRecord = KafkaConsumerRecord.newInstance(new KafkaConsumerRecordImpl(consumerRecord));
        final KafkaCommitHandler kafkaCommitHandler = new KafkaIgnoreCommit();
        final KafkaFailureHandler kafkaFailureHandler = new KafkaIgnoreFailure("channel");
        final IncomingKafkaRecord incomingKafkaRecord = new IncomingKafkaRecord<>(kafkaConsumerRecord, kafkaCommitHandler, kafkaFailureHandler);

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