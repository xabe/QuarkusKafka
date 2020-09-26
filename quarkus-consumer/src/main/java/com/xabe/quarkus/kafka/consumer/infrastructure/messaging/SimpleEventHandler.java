package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;


import com.xabe.quarkus.kafka.consumer.domain.entity.DO;
import org.apache.avro.specific.SpecificRecord;

import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleEventHandler<T extends SpecificRecord, R extends DO> implements EventHandler<T> {

  private final Function<T, R> mapper;

  private final Consumer<R> consumer;

  public SimpleEventHandler(final Function<T, R> mapper, final Consumer<R> consumer) {
    this.mapper = mapper;
    this.consumer = consumer;
  }

  @Override
  public void handle(final T paylooad) {
    this.consumer.accept(this.mapper.apply(paylooad));
  }
}
