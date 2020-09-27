package com.xabe.quarkus.kafka.producer.infrastructure.messaging;

import com.xabe.avro.v1.Car;
import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.avro.v1.MessageEnvelope;
import com.xabe.avro.v1.Metadata;
import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.producer.domain.repository.ProducerRepository;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProducerRepositoryImpl implements ProducerRepository {

  public static final String V_TEST = "vTest";

  public static final String CAR = "car";

  private final Logger logger = LoggerFactory.getLogger(ProducerRepositoryImpl.class);

  @Inject
  Clock clock;

  @Inject
  @Channel("output")
  Emitter<MessageEnvelope> kafkaEmitter;

  public ProducerRepositoryImpl() {
  }

  ProducerRepositoryImpl(final Clock clock,
      final Emitter<MessageEnvelope> kafkaEmitter) {
    this.clock = clock;
    this.kafkaEmitter = kafkaEmitter;
  }

  @Override
  public void saveCar(final CarDO carDO) {
    final Car car = Car.newBuilder().setId(carDO.getId()).setName(carDO.getName()).build();
    final CarCreated carCreated = CarCreated.newBuilder().setSentAt(carDO.getSentAt()).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData("create")).setPayload(carCreated)
        .build();
    this.kafkaEmitter.send(Message.of(messageEnvelope, this.createMetaDataKafka(car.getId())));
    this.logger.info("Send Command CarCreated {}", carCreated);
  }

  @Override
  public void updateCar(final CarDO carDO) {
    final Car car = Car.newBuilder().setId(carDO.getId()).setName(carDO.getName()).build();
    final CarUpdated carUpdated = CarUpdated.newBuilder().setSentAt(carDO.getSentAt()).setCar(car).setCarBeforeUpdate(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData("update")).setPayload(carUpdated)
        .build();
    this.kafkaEmitter.send(Message.of(messageEnvelope, this.createMetaDataKafka(car.getId())));
    this.logger.info("Send Command CarUpdate {}", carUpdated);
  }

  @Override
  public void deleteCar(final CarDO carDO) {
    final Car car = Car.newBuilder().setId(carDO.getId()).setName(carDO.getName()).build();
    final CarDeleted carDeleted = CarDeleted.newBuilder().setSentAt(carDO.getSentAt()).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData("delete")).setPayload(carDeleted)
        .build();
    this.kafkaEmitter.send(Message.of(messageEnvelope, this.createMetaDataKafka(car.getId())));
    this.logger.info("Send Command CarDelete {}", carDeleted);
  }

  private Metadata createMetaData(final String action) {
    return Metadata.newBuilder().setDomain(CAR).setName(CAR).setAction(action).setVersion(V_TEST)
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now(this.clock))).build();
  }

  private org.eclipse.microprofile.reactive.messaging.Metadata createMetaDataKafka(final String key) {
    return org.eclipse.microprofile.reactive.messaging.Metadata.of(OutgoingKafkaRecordMetadata.builder().withKey(key).build());
  }
}
