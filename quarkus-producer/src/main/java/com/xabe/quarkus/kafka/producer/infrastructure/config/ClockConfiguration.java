package com.xabe.quarkus.kafka.producer.infrastructure.config;

import io.quarkus.arc.DefaultBean;
import java.time.Clock;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class ClockConfiguration {

  @Produces
  @DefaultBean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

}