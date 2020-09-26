package com.xabe.quarkus.kafka.consumer.infrastructure.application;


import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;

import java.util.List;

public interface ConsumerUseCase {

  List<CarDO> getCars();

}
