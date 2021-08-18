package com.xabe.quarkus.kafka.producer.infrastructure.application;

import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.producer.domain.repository.ProducerRepository;
import com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload.CarPayload;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RequiredArgsConstructor
public class ProducerUseCaseImpl implements ProducerUseCase {

  private final Logger logger = LoggerFactory.getLogger(ProducerUseCaseImpl.class);

  private final ProducerRepository producerRepository;

  @Override
  public void createCar(final CarPayload carPayload) {
    this.producerRepository.saveCar(this.toCarDO(carPayload));
    this.logger.info("Created carPayload {}", carPayload);
  }

  @Override
  public void updateCar(final CarPayload carPayload) {
    this.producerRepository.updateCar(this.toCarDO(carPayload));
    this.logger.info("Update carPayload {}", carPayload);
  }

  @Override
  public void deleteCar(final CarPayload carPayload) {
    this.producerRepository.deleteCar(this.toCarDO(carPayload));
    this.logger.info("Delete carPayload {}", carPayload);
  }

  private CarDO toCarDO(final CarPayload carPayload) {
    return CarDO.builder().id(carPayload.getId()).name(carPayload.getName()).sentAt(carPayload.getSentAt()).build();
  }
}
