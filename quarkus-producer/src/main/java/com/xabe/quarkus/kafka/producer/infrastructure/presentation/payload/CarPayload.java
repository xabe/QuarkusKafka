package com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
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
@RegisterForReflection
public class CarPayload implements Serializable {

  @NotNull
  private final String id;

  @NotNull
  private final String name;

  private final Long sentAt;

}
