package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.avro.v1.Car;
import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.quarkus.kafka.consumer.domain.entity.CarDO;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarMapperImplTest {

  private CarMapper carMapper;

  @BeforeEach
  public void setUp() throws Exception {
    this.carMapper = new CarMapperImpl();
  }

  @Test
  public void givenACarCreatedWhenInvokeToCarCreateCarDTOThenReturnCarDTO() throws Exception {
    final Car car = Car.newBuilder().setId("id").setName("name").build();
    final Instant now = Instant.now();
    final CarCreated carCreated = CarCreated.newBuilder().setCar(car).setSentAt(now).build();

    final CarDO result = this.carMapper.toCarCreateCarDTO(carCreated);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("id"));
    assertThat(result.getName(), is("name"));
    assertThat(result.getSentAt(), is(now));
  }

  @Test
  public void givenACarUpdateWhenInvokeToCarCreateCarDTOThenReturnCarDTO() throws Exception {
    final Car car = Car.newBuilder().setId("id").setName("name").build();
    final Car carOld = Car.newBuilder().setId("id1").setName("name1").build();
    final Instant now = Instant.now();
    final CarUpdated carUpdated = CarUpdated.newBuilder().setCarBeforeUpdate(carOld).setCar(car).setSentAt(now).build();

    final CarDO result = this.carMapper.toCarUpdateCarDTO(carUpdated);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("id"));
    assertThat(result.getName(), is("name"));
    assertThat(result.getSentAt(), is(now));
  }

  @Test
  public void givenACarDeleteWhenInvokeToCarCreateCarDTOThenReturnCarDTO() throws Exception {
    final Car car = Car.newBuilder().setId("id").setName("name").build();
    final Instant now = Instant.now();
    final CarDeleted carDeleted = CarDeleted.newBuilder().setCar(car).setSentAt(now).build();

    final CarDO result = this.carMapper.toCarDeleteCarDTO(carDeleted);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("id"));
    assertThat(result.getName(), is("name"));
    assertThat(result.getSentAt(), is(now));
  }

}