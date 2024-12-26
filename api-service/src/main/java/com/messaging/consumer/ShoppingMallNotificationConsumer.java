package com.messaging.consumer;

import com.messaging.model.ShoppingMallNotificationPayloadDto;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ShoppingMallNotificationConsumer {

    @Incoming("shopping-mall-inbox")
    public Uni<Void> consume(ShoppingMallNotificationPayloadDto payload) {
        Log.info("Customer ID: " + payload.personId() + ", Message: " + payload.message());
        return Uni.createFrom().voidItem();
    }
}
