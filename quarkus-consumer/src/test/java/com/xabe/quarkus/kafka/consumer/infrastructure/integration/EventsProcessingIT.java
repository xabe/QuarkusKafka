package com.xabe.quarkus.kafka.consumer.infrastructure.integration;

import com.xabe.avro.v1.Metadata;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@QuarkusIntegrationTest
public class EventsProcessingIT extends EventsProcessingTest {

  @Override
  protected Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("carNative").setName("carNative").setAction("updateNative").setVersion("vTestNative")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

  private String generateId() {
    return UUID.randomUUID().toString() + "-Native";
  }

}