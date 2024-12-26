package com.messaging.model;

import java.util.UUID;

public record ShoppingMallNotificationPayloadDto(UUID personId, String message) {
}
