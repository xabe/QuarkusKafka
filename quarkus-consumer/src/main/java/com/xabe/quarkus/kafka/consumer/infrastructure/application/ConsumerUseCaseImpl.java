package com.xabe.quarkus.kafka.consumer.infrastructure.application;

import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.consumer.domain.repository.ConsumerRepository;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ConsumerUseCaseImpl implements ConsumerUseCase {

  private final Logger logger = LoggerFactory.getLogger(ConsumerUseCaseImpl.class);

  @Inject
  ConsumerRepository consumerRepository;

  public ConsumerUseCaseImpl() {
  }

  ConsumerUseCaseImpl(final ConsumerRepository consumerRepository) {
    this.consumerRepository = consumerRepository;
  }

  @Override
  public List<CarDO> getCars() {
    return this.consumerRepository.getCarDOS();
  }
}