package com.xabe.quarkus.kafka.consumer.infrastructure.messaging;

import com.xabe.avro.v1.MessageEnvelope;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import org.apache.avro.specific.SpecificRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class CarProcessor {

    private final Logger logger = LoggerFactory.getLogger(CarProcessor.class);

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
        this.logger.info("Received a message. message: {} metadata {}", message, metadata);
        final MessageEnvelope messageEnvelope = message.getPayload();
        final Class<?> msgClass = messageEnvelope.getPayload().getClass();
        final SpecificRecord payload = SpecificRecord.class.cast(messageEnvelope.getPayload());
        final EventHandler handler = this.handlers.get(msgClass);
        if (handler == null) {
            this.logger.warn("Received a non supported message. Type: {}, toString: {}", msgClass.getName(), payload.toString());
        } else {
            handler.handle(payload);
        }
        return message.ack();
    }
}