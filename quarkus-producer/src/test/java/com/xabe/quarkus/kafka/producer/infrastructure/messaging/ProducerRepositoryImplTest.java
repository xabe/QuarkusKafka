package com.xabe.quarkus.kafka.producer.infrastructure.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.avro.v1.MessageEnvelope;
import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.producer.domain.repository.ProducerRepository;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ProducerRepositoryImplTest {

  private Clock clock;

  private Emitter emitter;

  private ProducerRepository producerRepository;

  @BeforeEach
  public void setUp() throws Exception {
    this.clock = mock(Clock.class);
    this.emitter = mock(Emitter.class);
    this.producerRepository = new ProducerRepositoryImpl(this.clock, this.emitter);
  }

  @Test
  public void givenACarDOWhenInvokeSaveCarThenSendEvent() throws Exception {
    final CarDO carDO = CarDO.builder().id("id").name("name").sentAt(1L).build();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(this.clock.instant()).thenReturn(Instant.parse("2018-11-30T18:35:24.00Z"));

    this.producerRepository.saveCar(carDO);

    verify(this.emitter).send(messageArgumentCaptor.capture());
    final Message result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getPayload(), is(notNullValue()));
    final MessageEnvelope messageEnvelope = MessageEnvelope.class.cast(result.getPayload());
    this.assertMetadata(messageEnvelope, "create");
    final CarCreated carCreated = CarCreated.class.cast(messageEnvelope.getPayload());
    assertThat(carCreated, is(notNullValue()));
    assertThat(carCreated.getSentAt(), is(1L));
    assertThat(carCreated.getCar().getId(), is("id"));
    assertThat(carCreated.getCar().getName(), is("name"));
    assertThat(result.getMetadata(OutgoingKafkaRecordMetadata.class).isPresent(), is(true));
    assertThat(((OutgoingKafkaRecordMetadata) result.getMetadata(OutgoingKafkaRecordMetadata.class).get()).getKey(), is("id"));
  }

  @Test
  public void givenACarDOWhenInvokeUpdateCarThenSendEvent() throws Exception {
    final CarDO carDO = CarDO.builder().id("id").name("name").sentAt(1L).build();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(this.clock.instant()).thenReturn(Instant.parse("2018-11-30T18:35:24.00Z"));

    this.producerRepository.updateCar(carDO);

    verify(this.emitter).send(messageArgumentCaptor.capture());
    final Message result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getPayload(), is(notNullValue()));
    final MessageEnvelope messageEnvelope = MessageEnvelope.class.cast(result.getPayload());
    this.assertMetadata(messageEnvelope, "update");
    final CarUpdated carUpdated = CarUpdated.class.cast(messageEnvelope.getPayload());
    assertThat(carUpdated, is(notNullValue()));
    assertThat(carUpdated.getSentAt(), is(1L));
    assertThat(carUpdated.getCar().getId(), is("id"));
    assertThat(carUpdated.getCar().getName(), is("name"));
    assertThat(result.getMetadata(OutgoingKafkaRecordMetadata.class).isPresent(), is(true));
    assertThat(((OutgoingKafkaRecordMetadata) result.getMetadata(OutgoingKafkaRecordMetadata.class).get()).getKey(), is("id"));
  }

  @Test
  public void givenACarDOWhenInvokeDeleteCarThenSendEvent() throws Exception {
    final CarDO carDO = CarDO.builder().id("id").name("name").sentAt(1L).build();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(this.clock.instant()).thenReturn(Instant.parse("2018-11-30T18:35:24.00Z"));

    this.producerRepository.deleteCar(carDO);

    verify(this.emitter).send(messageArgumentCaptor.capture());
    final Message result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getPayload(), is(notNullValue()));
    final MessageEnvelope messageEnvelope = MessageEnvelope.class.cast(result.getPayload());
    this.assertMetadata(messageEnvelope, "delete");
    final CarDeleted carDeleted = CarDeleted.class.cast(messageEnvelope.getPayload());
    assertThat(carDeleted, is(notNullValue()));
    assertThat(carDeleted.getSentAt(), is(1L));
    assertThat(carDeleted.getCar().getId(), is("id"));
    assertThat(carDeleted.getCar().getName(), is("name"));
    assertThat(result.getMetadata(OutgoingKafkaRecordMetadata.class).isPresent(), is(true));
    assertThat(((OutgoingKafkaRecordMetadata) result.getMetadata(OutgoingKafkaRecordMetadata.class).get()).getKey(), is("id"));
  }

  private void assertMetadata(final MessageEnvelope messageEnvelope, final String action) {
    assertThat(messageEnvelope, is(notNullValue()));
    assertThat(messageEnvelope.getMetadata().getDomain(), is("car"));
    assertThat(messageEnvelope.getMetadata().getName(), is("car"));
    assertThat(messageEnvelope.getMetadata().getAction(), is(action));
    assertThat(messageEnvelope.getMetadata().getVersion(), is("vTest"));
    assertThat(messageEnvelope.getMetadata().getTimestamp(), is(("2018-11-30T19:35:24+01:00")));
  }

}