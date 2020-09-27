package com.xabe.quarkus.kafka.producer.infrastructure.application;

import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.producer.domain.repository.ProducerRepository;
import com.xabe.quarkus.kafka.producer.infrastructure.persentation.payload.CarPayload;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProducerUseCaseImpl implements ProducerUseCase {

  private final Logger logger = LoggerFactory.getLogger(ProducerUseCaseImpl.class);

  @Inject
  ProducerRepository producerRepository;

  public ProducerUseCaseImpl() {
  }

  ProducerUseCaseImpl(final ProducerRepository producerRepository) {
    this.producerRepository = producerRepository;
  }

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
