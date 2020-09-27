package com.xabe.quarkus.kafka.producer.domain.repository;

import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;

public interface ProducerRepository {

  void saveCar(CarDO carDO);

  void updateCar(CarDO carDO);

  void deleteCar(CarDO carDO);
}
