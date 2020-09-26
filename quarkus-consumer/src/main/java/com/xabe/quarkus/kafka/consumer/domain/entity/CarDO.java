package com.xabe.quarkus.kafka.consumer.domain.entity;

import lombok.*;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CarDO implements DO {

  private final String id;

  private final String name;

  private final Long sentAt;

}
