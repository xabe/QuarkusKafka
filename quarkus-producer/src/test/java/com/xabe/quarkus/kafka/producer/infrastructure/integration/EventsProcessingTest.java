package com.xabe.quarkus.kafka.producer.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.avro.v1.MessageEnvelope;
import com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload.CarPayload;
import groovy.lang.Tuple2;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class EventsProcessingTest {

  private static final long DEFAULT_TIMEOUT_MS = 5000;

  private final int serverPort = 8009;

  @BeforeAll
  public static void init() throws IOException, InterruptedException {
    final InputStream car = EventsProcessingTest.class.getClassLoader().getResourceAsStream("avro-car.json");
    Unirest.post(UrlUtil.getInstance().getSchemaRegistryCar()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(IOUtils.toString(car, StandardCharsets.UTF_8)).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityCar()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body("{\"compatibility\":\"Forward\"}").asJson();
    KafkaConsumer.create();
  }

  @AfterAll
  public static void end() {
    KafkaConsumer.close();
  }

  @BeforeEach
  public void before() {
    KafkaConsumer.before();
  }

  @Test
  public void shouldCreatedCar() throws Exception {
    final CarPayload carPayload = CarPayload.builder().withId("id").withName("mazda 3").build();

    final HttpResponse<JsonNode> response = Unirest.post(String.format("http://localhost:%d/api/producer/car", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).body(carPayload).asJson();

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));

    final Tuple2<String, MessageEnvelope> result = KafkaConsumer.expectMessagePipe(CarCreated.class, DEFAULT_TIMEOUT_MS);
    assertThat(result, is(notNullValue()));
    assertThat(result.getV1(), is("id"));
    assertThat(result.getV2(), is(notNullValue()));
  }

  @Test
  public void shouldUpdateCar() throws Exception {
    final CarPayload carPayload = CarPayload.builder().withId("id").withName("mazda 5").build();

    final HttpResponse<JsonNode> response = Unirest.put(String.format("http://localhost:%d/api/producer/car", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).body(carPayload).asJson();

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));

    final Tuple2<String, MessageEnvelope> result = KafkaConsumer.expectMessagePipe(CarUpdated.class, DEFAULT_TIMEOUT_MS);
    assertThat(result, is(notNullValue()));
    assertThat(result.getV1(), is("id"));
    assertThat(result.getV2(), is(notNullValue()));
  }

  @Test
  public void shouldDeleteCar() throws Exception {

    final HttpResponse<JsonNode> response = Unirest.delete(String.format("http://localhost:%d/api/producer/car/1", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asJson();

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));

    final Tuple2<String, MessageEnvelope> result = KafkaConsumer.expectMessagePipe(CarDeleted.class, DEFAULT_TIMEOUT_MS);
    assertThat(result, is(notNullValue()));
    assertThat(result.getV1(), is("1"));
    assertThat(result.getV2(), is(notNullValue()));
  }

}