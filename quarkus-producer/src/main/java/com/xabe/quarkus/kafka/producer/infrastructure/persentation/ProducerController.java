package com.xabe.quarkus.kafka.producer.infrastructure.persentation;

import com.xabe.quarkus.kafka.producer.infrastructure.application.ProducerUseCase;
import com.xabe.quarkus.kafka.producer.infrastructure.persentation.payload.CarPayload;
import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/producer/car")
@Singleton
public class ProducerController {

  public static final String DELETE = "delete";

  private final Logger logger = LoggerFactory.getLogger(ProducerController.class);

  @Inject
  Clock clock;

  @Inject
  ProducerUseCase producerUseCase;

  public ProducerController() {
  }

  ProducerController(final Clock clock, final ProducerUseCase producerUseCase) {
    this.clock = clock;
    this.producerUseCase = producerUseCase;
  }

  @POST
  public Response createCar(@Valid final CarPayload carPayload) {
    this.producerUseCase.createCar(carPayload.toBuilder().sentAt(this.clock.millis()).build());
    this.logger.info("Create carPayload {}", carPayload);
    return Response.ok().build();
  }

  @PUT
  public Response updateCar(@Valid final CarPayload carPayload) {
    this.producerUseCase.updateCar(carPayload.toBuilder().sentAt(this.clock.millis()).build());
    this.logger.info("Update carPayload {}", carPayload);
    return Response.ok().build();
  }

  @Path(value = "/{id}")
  @DELETE
  public Response deleteCar(final @PathParam("id") String id) {
    final CarPayload carPayload = CarPayload.builder().id(id).name(DELETE).sentAt(this.clock.millis()).build();
    this.producerUseCase.deleteCar(carPayload);
    this.logger.info("Delete carPayload {}", carPayload);
    return Response.ok().build();
  }
}
