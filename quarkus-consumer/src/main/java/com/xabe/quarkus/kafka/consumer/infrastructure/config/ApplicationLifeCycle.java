package com.xabe.quarkus.kafka.consumer.infrastructure.config;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class ApplicationLifeCycle {
    private static final Logger LOGGER = LoggerFactory.getLogger("ApplicationLifeCycle");

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting with profile {}", ProfileManager.getActiveProfile());
    }
}
