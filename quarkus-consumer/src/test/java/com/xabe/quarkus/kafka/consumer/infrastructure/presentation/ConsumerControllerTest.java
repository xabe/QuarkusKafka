package com.xabe.quarkus.kafka.consumer.infrastructure.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.consumer.infrastructure.application.ConsumerUseCase;
import com.xabe.quarkus.kafka.consumer.infrastructure.presentation.payload.CarPayload;
import java.time.Instant;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsumerControllerTest {

  private ConsumerUseCase consumerUseCase;

  private ConsumerController consumerController;

  @BeforeEach
  public void setUp() throws Exception {
    this.consumerUseCase = mock(ConsumerUseCase.class);
    this.consumerController = new ConsumerController(this.consumerUseCase);
  }

  @Test
  @DisplayName("Should get all cars")
  public void getAllCars() throws Exception {
    final Instant now = Instant.now();
    when(this.consumerUseCase.getCars()).thenReturn(List.of(CarDO.builder().id("id").name("name").sentAt(now).build()));

    final Response result = this.consumerController.getCars();

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(Status.OK.getStatusCode()));
    final List<CarPayload> payloads = List.class.cast(result.getEntity());
    assertThat(payloads, is(hasSize(1)));
    assertThat(payloads, is(containsInAnyOrder(CarPayload.builder().id("id").name("name").sentAt(now).build())));
  }

}