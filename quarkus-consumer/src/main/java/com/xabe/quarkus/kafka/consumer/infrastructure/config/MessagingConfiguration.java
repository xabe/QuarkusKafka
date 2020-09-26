package com.xabe.quarkus.kafka.consumer.infrastructure.config;

import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.quarkus.kafka.consumer.domain.repository.ConsumerRepository;
import com.xabe.quarkus.kafka.consumer.infrastructure.messaging.CarMapper;
import com.xabe.quarkus.kafka.consumer.infrastructure.messaging.EventHandler;
import com.xabe.quarkus.kafka.consumer.infrastructure.messaging.SimpleEventHandler;
import io.quarkus.arc.DefaultBean;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Dependent
public class MessagingConfiguration {

  @Named("carHandlers")
  @Produces
  @DefaultBean
  public Map<Class, EventHandler> carEventHandlers(final CarMapper carMapper, final ConsumerRepository consumerRepository) {
    final Map<Class, EventHandler> eventHandlers = new HashMap<>();
    eventHandlers.put(CarCreated.class, new SimpleEventHandler<>(carMapper::toCarCreateCarDTO, consumerRepository::addCar));
    eventHandlers.put(CarUpdated.class, new SimpleEventHandler<>(carMapper::toCarUpdateCarDTO, consumerRepository::updateCar));
    eventHandlers.put(CarDeleted.class, new SimpleEventHandler<>(carMapper::toCarDeleteCarDTO, consumerRepository::deleteCar));
    return eventHandlers;
  }
}
