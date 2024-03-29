package com.xabe.quarkus.kafka.producer.infrastructure.application;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.producer.domain.repository.ProducerRepository;
import com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload.CarPayload;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerUseCaseImplTest {

  private ProducerRepository producerRepository;

  private ProducerUseCase producerUseCase;

  @BeforeEach
  public void setUp() throws Exception {
    this.producerRepository = mock(ProducerRepository.class);
    this.producerUseCase = new ProducerUseCaseImpl(this.producerRepository);
  }

  @Test
  public void shouldSaveCar() throws Exception {
    final Instant now = Instant.now();
    final CarPayload carPayload = CarPayload.builder().withName("name").withId("id").withSentAt(now).build();

    this.producerUseCase.createCar(carPayload);

    verify(this.producerRepository).saveCar(eq(CarDO.builder().sentAt(now).name("name").id("id").build()));
  }

  @Test
  public void shouldUpdateCar() throws Exception {
    final Instant now = Instant.now();
    final CarPayload carPayload = CarPayload.builder().withName("name").withId("id").withSentAt(now).build();

    this.producerUseCase.updateCar(carPayload);

    verify(this.producerRepository).updateCar(eq(CarDO.builder().sentAt(now).name("name").id("id").build()));
  }

  @Test
  public void shouldDeleteCar() throws Exception {
    final Instant now = Instant.now();
    final CarPayload carPayload = CarPayload.builder().withName("name").withId("id").withSentAt(now).build();

    this.producerUseCase.deleteCar(carPayload);

    verify(this.producerRepository).deleteCar(eq(CarDO.builder().sentAt(now).name("name").id("id").build()));
  }

}