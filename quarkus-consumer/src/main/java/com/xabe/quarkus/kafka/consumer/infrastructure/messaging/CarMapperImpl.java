package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;


import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarMapperImpl implements CarMapper {

  @Override
  public CarDO toCarCreateCarDTO(final CarCreated carCreated) {
    return CarDO.builder().name(carCreated.getCar().getName()).id(carCreated.getCar().getId()).sentAt(carCreated.getSentAt()).build();
  }

  @Override
  public CarDO toCarUpdateCarDTO(final CarUpdated carUpdated) {
    return CarDO.builder().name(carUpdated.getCar().getName()).id(carUpdated.getCar().getId()).sentAt(carUpdated.getSentAt()).build();
  }

  @Override
  public CarDO toCarDeleteCarDTO(final CarDeleted carDeleted) {
    return CarDO.builder().name(carDeleted.getCar().getName()).id(carDeleted.getCar().getId()).sentAt(carDeleted.getSentAt()).build();
  }
}
