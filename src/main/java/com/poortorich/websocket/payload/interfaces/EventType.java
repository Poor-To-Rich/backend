package com.poortorich.websocket.payload.interfaces;

public interface EventType {

    default EventType getType() {
        return this;
    }
}
