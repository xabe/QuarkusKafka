package com.xabe.quarkus.kafka.producer.domain.entity;

import java.time.Instant;
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
public class CarDO implements DO {

  String id;

  String name;

  Instant sentAt;

}
