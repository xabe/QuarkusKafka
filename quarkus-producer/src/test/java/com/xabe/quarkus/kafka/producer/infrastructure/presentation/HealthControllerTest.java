package com.xabe.quarkus.kafka.producer.infrastructure.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HealthControllerTest {

  private HealthController healthController;

  @BeforeEach
  public void setUp() throws Exception {
    this.healthController = new HealthController();
  }

  @Test
  public void shouldGetOK() throws Exception {
    assertThat(this.healthController.healthCheck(), is(notNullValue()));
  }
}