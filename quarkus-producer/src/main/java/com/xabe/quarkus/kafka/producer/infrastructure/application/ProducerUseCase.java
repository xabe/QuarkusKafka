package com.xabe.quarkus.kafka.producer.infrastructure.application;

import com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload.CarPayload;

public interface ProducerUseCase {

  void createCar(CarPayload carPayload);

  void updateCar(CarPayload carPayload);

  void deleteCar(CarPayload carPayload);
}
