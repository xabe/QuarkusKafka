package com.xabe.quarkus.kafka.consumer.infrastructure.presentation;

import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.consumer.infrastructure.application.ConsumerUseCase;
import com.xabe.quarkus.kafka.consumer.infrastructure.presentation.payload.CarPayload;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/consumer")
@Singleton
@Consumes(MediaType.APPLICATION_JSON)
@Produces({MediaType.APPLICATION_JSON})
@RequiredArgsConstructor
public class ConsumerController {

  private final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

  private final ConsumerUseCase consumerUseCase;

  @GET
  public Response getCars() {
    return Response.ok(this.consumerUseCase.getCars().stream().map(this::mapper).collect(Collectors.toList())).build();
  }

  private CarPayload mapper(final CarDO carDO) {
    return CarPayload.builder().sentAt(carDO.getSentAt()).id(carDO.getId()).name(carDO.getName()).build();
  }
}
