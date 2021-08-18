package com.xabe.quarkus.kafka.producer.infrastructure.messaging;

import com.xabe.avro.v1.Car;
import com.xabe.avro.v1.CarCreated;
import com.xabe.avro.v1.CarDeleted;
import com.xabe.avro.v1.CarUpdated;
import com.xabe.avro.v1.MessageEnvelope;
import com.xabe.avro.v1.Metadata;
import com.xabe.quarkus.kafka.producer.domain.entity.CarDO;
import com.xabe.quarkus.kafka.producer.domain.repository.ProducerRepository;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class ProducerRepositoryImpl implements ProducerRepository {

  public static final String V_TEST = "vTest";

  public static final String CAR = "car";

  private final Logger logger = Logger.getLogger(ProducerRepositoryImpl.class);

  private final Clock clock;

  @Channel("output")
  private final Emitter<MessageEnvelope> kafkaEmitter;

  @Override
  public void saveCar(final CarDO carDO) {
    final Car car = Car.newBuilder().setId(carDO.getId()).setName(carDO.getName()).build();
    final CarCreated carCreated = CarCreated.newBuilder().setSentAt(carDO.getSentAt()).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData("create")).setPayload(carCreated)
        .build();
    this.kafkaEmitter.send(Message.of(messageEnvelope, this.createMetaDataKafka(car.getId())));
    this.logger.infof("Send Command CarCreated {%s}", messageEnvelope);
  }

  @Override
  public void updateCar(final CarDO carDO) {
    final Car car = Car.newBuilder().setId(carDO.getId()).setName(carDO.getName()).build();
    final CarUpdated carUpdated = CarUpdated.newBuilder().setSentAt(carDO.getSentAt()).setCar(car).setCarBeforeUpdate(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData("update")).setPayload(carUpdated)
        .build();
    this.kafkaEmitter.send(Message.of(messageEnvelope, this.createMetaDataKafka(car.getId())));
    this.logger.infof("Send Command CarUpdate {%s}", messageEnvelope);
  }

  @Override
  public void deleteCar(final CarDO carDO) {
    final Car car = Car.newBuilder().setId(carDO.getId()).setName(carDO.getName()).build();
    final CarDeleted carDeleted = CarDeleted.newBuilder().setSentAt(carDO.getSentAt()).setCar(car).build();
    final MessageEnvelope messageEnvelope = MessageEnvelope.newBuilder().setMetadata(this.createMetaData("delete")).setPayload(carDeleted)
        .build();
    this.kafkaEmitter.send(Message.of(messageEnvelope, this.createMetaDataKafka(car.getId())));
    this.logger.infof("Send Command CarDelete {%s}", messageEnvelope);
  }

  private Metadata createMetaData(final String action) {
    return Metadata.newBuilder().setDomain(CAR).setName(CAR).setAction(action).setVersion(V_TEST)
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now(this.clock))).build();
  }

  private org.eclipse.microprofile.reactive.messaging.Metadata createMetaDataKafka(final String key) {
    return org.eclipse.microprofile.reactive.messaging.Metadata.of(OutgoingKafkaRecordMetadata.builder().withKey(key).build());
  }
}
