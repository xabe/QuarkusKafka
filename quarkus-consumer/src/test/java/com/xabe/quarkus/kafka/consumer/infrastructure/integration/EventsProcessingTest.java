package com.xabe.quarkus.kafka.consumer.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.avro.v1.Car;
import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.avro.v1.MessageEnvelope;
import com.xabe.avro.v1.Metadata;
import com.xabe.quarkus.kafka.consumer.infrastructure.presentation.payload.CarPayload;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.io.IOUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class EventsProcessingTest {

  public static final int TIMEOUT_MS = 5000;

  public static final int DELAY_MS = 1500;

  public static final int POLL_INTERVAL_MS = 500;

  @BeforeAll
  public static void init() throws IOException {
    final InputStream car = EventsProcessingTest.class.getClassLoader().getResourceAsStream("avro-car.json");
    Unirest.post(UrlUtil.getInstance().getSchemaRegistryCar()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(IOUtils.toString(car, StandardCharsets.UTF_8)).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityCar()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body("{\"compatibility\":\"Forward\"}").asJson();
    KafkaProducer.create();
  }

  @AfterAll
  public static void end() {
    KafkaProducer.close();
  }

  @Test
  public void shouldCreateCar() throws Exception {
    final String id = this.generateId();
    final Car car = Car.newBuilder().setId(id).setName("name").build();
    final CarCreated carCreated = CarCreated.newBuilder().setSentAt(System.currentTimeMillis()).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carCreated)
        .build();

    KafkaProducer.send(messageEnvelope, () -> id);

    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).untilAsserted(() -> {

      final HttpResponse<CarPayload[]> response = Unirest.get(String.format("http://localhost:%d/api/consumer", 8008))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(CarPayload[].class);

      assertThat(response, is(notNullValue()));
      assertThat(response.getStatus(), is(greaterThanOrEqualTo(200)));
      assertThat(response.getStatus(), is(lessThan(300)));
      assertThat(response.getBody().length, is(greaterThanOrEqualTo(1)));
    });
  }

  @Test
  public void shouldConsumerCarUpdate() throws Exception {
    //Given
    final String id = this.generateId();
    final Car carOld = Car.newBuilder().setId(id).setName("mazda").build();
    final MessageEnvelope messageEnvelopeOld = MessageEnvelope.newBuilder().setMetadata(this.createMetaData())
        .setPayload(CarCreated.newBuilder().setSentAt(Instant.now().toEpochMilli()).setCar(carOld).build()).build();

    KafkaProducer.send(messageEnvelopeOld, () -> id);

    final Car car = Car.newBuilder().setId(id).setName("mazda3").build();
    final CarUpdated carUpdated = CarUpdated.newBuilder().setSentAt(Instant.now().toEpochMilli()).setCarBeforeUpdate(carOld).setCar(car)
        .build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carUpdated).build();

    //When
    KafkaProducer.send(messageEnvelope, () -> id);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).untilAsserted(() -> {

      final HttpResponse<CarPayload[]> response = Unirest.get(String.format("http://localhost:%d/api/consumer", 8008))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(CarPayload[].class);

      assertThat(response, is(notNullValue()));
      assertThat(response.getStatus(), is(greaterThanOrEqualTo(200)));
      assertThat(response.getStatus(), is(lessThan(300)));
      assertThat(response.getBody().length, is(greaterThanOrEqualTo(1)));
    });
  }

  @Test
  public void shouldConsumerCarDelete() throws Exception {
    //Given
    final String id = this.generateId();
    final Car car = Car.newBuilder().setId(id).setName("delete").build();
    final MessageEnvelope messageEnvelopeOld = MessageEnvelope.newBuilder().setMetadata(this.createMetaData())
        .setPayload(CarCreated.newBuilder().setSentAt(Instant.now().toEpochMilli()).setCar(car).build()).build();

    KafkaProducer.send(messageEnvelopeOld, () -> id);

    final CarDeleted carDeleted = CarDeleted.newBuilder().setSentAt(Instant.now().toEpochMilli()).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData()).setPayload(carDeleted).build();

    //When
    KafkaProducer.send(messageEnvelope, () -> id);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).untilAsserted(() -> {

      final HttpResponse<CarPayload[]> response = Unirest.get(String.format("http://localhost:%d/api/consumer", 8008))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(CarPayload[].class);
      final Optional<CarPayload> carPayload = Stream.of(response.getBody()).filter(item -> id.equalsIgnoreCase(item.getId())).findFirst();

      assertThat(response, is(notNullValue()));
      assertThat(response.getStatus(), is(greaterThanOrEqualTo(200)));
      assertThat(response.getStatus(), is(lessThan(300)));
      assertThat(carPayload.isEmpty(), is(true));
    });
  }

  protected Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("car").setName("car").setAction("update").setVersion("vTest")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

  private String generateId() {
    return UUID.randomUUID().toString();
  }

}