package com.xabe.quarkus.kafka.consumer.infrastructure.presentation;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
@Singleton
public class HealthController {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String healthCheck() {
    return "OK";
  }
}
