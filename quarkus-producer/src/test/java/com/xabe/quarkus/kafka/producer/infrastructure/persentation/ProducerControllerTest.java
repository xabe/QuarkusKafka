package com.xabe.quarkus.kafka.producer.infrastructure.persentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.quarkus.kafka.producer.infrastructure.application.ProducerUseCase;
import com.xabe.quarkus.kafka.producer.infrastructure.persentation.payload.CarPayload;
import java.time.Clock;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerControllerTest {

  private ProducerUseCase producerUseCase;

  private Clock clock;

  private ProducerController producerController;

  @BeforeEach
  public void setUp() throws Exception {
    this.producerUseCase = mock(ProducerUseCase.class);
    this.clock = mock(Clock.class);
    when(this.clock.millis()).thenReturn(1L);
    this.producerController = new ProducerController(this.clock, this.producerUseCase);
  }

  @Test
  public void givenACarPayloadWhenInvokeCreateCarThenReturnResponseEntity() throws Exception {
    final CarPayload carPayload = CarPayload.builder().build();

    final Response result = this.producerController.createCar(carPayload);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatusInfo(), is(Status.OK));
    verify(this.producerUseCase).createCar(eq(carPayload.toBuilder().sentAt(1L).build()));
  }

  @Test
  public void givenACarPayloadWhenInvokeUpdateCarThenReturnResponseEntity() throws Exception {
    final CarPayload carPayload = CarPayload.builder().build();

    final Response result = this.producerController.updateCar(carPayload);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatusInfo(), is(Status.OK));
    verify(this.producerUseCase).updateCar(eq(carPayload.toBuilder().sentAt(1L).build()));
  }

  @Test
  public void givenACarPayloadWhenInvokeDeleteCarThenReturnResponseEntity() throws Exception {
    final String id = "id";

    final Response result = this.producerController.deleteCar(id);

    assertThat(result, is(notNullValue()));
    assertThat(result.getStatusInfo(), is(Status.OK));
    verify(this.producerUseCase).deleteCar(eq(CarPayload.builder().id(id).name(ProducerController.DELETE).sentAt(1L).build()));
  }

}