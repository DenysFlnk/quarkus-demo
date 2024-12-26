package com.messaging.producer;

import com.messaging.model.ShoppingMallNotificationPayloadDto;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.reactive.messaging.Channel;

@ApplicationScoped
public class ShoppingMallNotificationProducer {

    @Channel("shopping-mall-outbox")
    MutinyEmitter<ShoppingMallNotificationPayloadDto> emitter;

    public Uni<Void> buildAndPublish(List<UUID> personIds, String message) {
        List<Uni<Void>> publishedNotifications = new ArrayList<>();

        for (UUID personId : personIds) {
            publishedNotifications.add(emitter.send(new ShoppingMallNotificationPayloadDto(personId, message)));
        }

        return Uni.combine().all().unis(publishedNotifications).discardItems();
    }
}
