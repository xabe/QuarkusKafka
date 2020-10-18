package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;

import com.xabe.avro.v1.MessageEnvelope;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.avro.specific.SpecificRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CarProcessor {

  private final Logger logger = Logger.getLogger(CarProcessor.class);

  @Inject
  Map<Class, EventHandler> handlers;

  public CarProcessor() {
  }

  CarProcessor(final Map<Class, EventHandler> handlers) {
    this.handlers = handlers;
  }

  @Incoming("input")
  public CompletionStage<Void> consumeKafka(final IncomingKafkaRecord<String, MessageEnvelope> message) {
    final Metadata metadata = message.getMetadata();
    this.logger.infof("Received a message. message: {%s} metadata {%s}", message, metadata);
    final MessageEnvelope messageEnvelope = message.getPayload();
    final Class<?> msgClass = messageEnvelope.getPayload().getClass();
    final SpecificRecord payload = SpecificRecord.class.cast(messageEnvelope.getPayload());
    final EventHandler handler = this.handlers.get(msgClass);
    if (handler == null) {
      this.logger.warnf("Received a non supported message. Type: {%s}, toString: {%s}", msgClass.getName(), payload);
    } else {
      handler.handle(payload);
    }
    return message.ack();
  }
}