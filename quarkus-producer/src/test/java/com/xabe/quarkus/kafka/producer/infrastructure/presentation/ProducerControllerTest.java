package com.xabe.quarkus.kafka.producer.infrastructure.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.quarkus.kafka.producer.infrastructure.application.ProducerUseCase;
import com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload.CarPayload;
import java.time.Clock;
import java.time.Instant;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerControllerTest {

  private ProducerUseCase producerUseCase;

  private Instant now;

  private ProducerController producerController;

  @BeforeEach
  public void setUp() throws Exception {
    this.producerUseCase = mock(ProducerUseCase.class);
    final Clock clock = mock(Clock.class);
    this.now = Instant.now();
    when(clock.instant()).thenReturn(this.now);
    this.producerController = new ProducerController(clock, this.producerUseCase);
  }

  @Test
  public void givenACarPayloadWhenInvokeCreateCarThenReturnResponseEntity() throws Exception {
    final CarPayload carPayload = CarPayload.builder().build();

    final Response result = this.producerController.createCar(carPayload);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatusInfo(), is(Status.OK));
    verify(this.producerUseCase).createCar(eq(carPayload.toBuilder().withSentAt(this.now).build()));
  }

  @Test
  public void givenACarPayloadWhenInvokeUpdateCarThenReturnResponseEntity() throws Exception {
    final CarPayload carPayload = CarPayload.builder().build();

    final Response result = this.producerController.updateCar(carPayload);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatusInfo(), is(Status.OK));
    verify(this.producerUseCase).updateCar(eq(carPayload.toBuilder().withSentAt(this.now).build()));
  }

  @Test
  public void givenACarPayloadWhenInvokeDeleteCarThenReturnResponseEntity() throws Exception {
    final String id = "id";

    final Response result = this.producerController.deleteCar(id);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatusInfo(), is(Status.OK));
    verify(this.producerUseCase).deleteCar(
        eq(CarPayload.builder().withId(id).withName(ProducerController.DELETE).withSentAt(this.now).build()));
  }

}