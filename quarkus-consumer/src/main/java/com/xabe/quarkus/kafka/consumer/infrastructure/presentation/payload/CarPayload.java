package com.xabe.quarkus.kafka.consumer.infrastructure.presentation.payload;

import java.time.Instant;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CarPayload {

  @NotNull
  String id;

  @NotNull
  String name;

  Instant sentAt;
}
