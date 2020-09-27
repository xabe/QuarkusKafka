package com.xabe.quarkus.kafka.producer.infrastructure.config;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationLifeCycle {

  private static final Logger LOGGER = LoggerFactory.getLogger("ApplicationLifeCycle");

  void onStart(@Observes final StartupEvent ev) {
    LOGGER.info("The application is starting with profile {}", ProfileManager.getActiveProfile());
  }
}
