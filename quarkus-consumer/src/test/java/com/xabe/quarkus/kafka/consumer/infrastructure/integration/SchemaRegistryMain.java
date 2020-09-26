package com.xabe.quarkus.kafka.consumer.infrastructure.integration;

import kong.unirest.Unirest;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SchemaRegistryMain {


    public static void main(String[] args) throws IOException {
        final InputStream car = SchemaRegistryMain.class.getClassLoader().getResourceAsStream("avro-car.json");
        Unirest.post(UrlUtil.getInstance().getSchemaRegistryCar()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(IOUtils.toString(car, StandardCharsets.UTF_8)).asJson();
        Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityCar()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body("{\"compatibility\":\"Forward\"}").asJson();
    }
}
