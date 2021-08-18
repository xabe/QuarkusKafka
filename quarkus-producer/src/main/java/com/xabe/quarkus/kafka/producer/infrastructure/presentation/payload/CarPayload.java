package com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
@Builder(toBuilder = true, setterPrefix = "with")
@Value
@JsonDeserialize(builder = CarPayload.CarPayloadBuilder.class)
public class CarPayload implements Serializable {

  @NotNull
  String id;

  @NotNull
  String name;

  Instant sentAt;

}
