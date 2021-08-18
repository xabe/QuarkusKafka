package com.xabe.quarkus.kafka.producer.infrastructure.presentation;

import com.xabe.quarkus.kafka.producer.infrastructure.application.ProducerUseCase;
import com.xabe.quarkus.kafka.producer.infrastructure.presentation.payload.CarPayload;
import java.time.Clock;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/producer/car")
@Singleton
@Consumes(MediaType.APPLICATION_JSON)
@Produces({MediaType.APPLICATION_JSON})
@RequiredArgsConstructor
public class ProducerController {

  public static final String DELETE = "delete";

  private final Logger logger = LoggerFactory.getLogger(ProducerController.class);

  private final Clock clock;

  private final ProducerUseCase producerUseCase;

  @POST
  public Response createCar(@Valid final CarPayload carPayload) {
    this.producerUseCase.createCar(carPayload.toBuilder().withSentAt(this.clock.instant()).build());
    this.logger.info("Create carPayload {}", carPayload);
    return Response.ok().build();
  }

  @PUT
  public Response updateCar(@Valid final CarPayload carPayload) {
    this.producerUseCase.updateCar(carPayload.toBuilder().withSentAt(this.clock.instant()).build());
    this.logger.info("Update carPayload {}", carPayload);
    return Response.ok().build();
  }

  @Path(value = "/{id}")
  @DELETE
  public Response deleteCar(final @PathParam("id") String id) {
    final CarPayload carPayload = CarPayload.builder().withId(id).withName(DELETE).withSentAt(this.clock.instant()).build();
    this.producerUseCase.deleteCar(carPayload);
    this.logger.info("Delete carPayload {}", carPayload);
    return Response.ok().build();
  }
}
