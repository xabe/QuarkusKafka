package com.xabe.quarkus.kafka.consumer.infrastructure.application;

import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.consumer.domain.repository.ConsumerRepository;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RequiredArgsConstructor
public class ConsumerUseCaseImpl implements ConsumerUseCase {

  private final Logger logger = LoggerFactory.getLogger(ConsumerUseCaseImpl.class);

  private final ConsumerRepository consumerRepository;

  @Override
  public List<CarDO> getCars() {
    return this.consumerRepository.getCarDOS();
  }
}