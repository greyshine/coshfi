package de.greyshine.coffeeshopfinder.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

@Slf4j
public class MessageBus {

    private static final MessageBus INSTANCE = new MessageBus();

    private final Map<String, List<Handler>> listeners = new HashMap<>();

    private MessageBus() {
    }

    private static MessageBus getInstance() {
        return INSTANCE;
    }

    public <T> void send(String topic, T object) {

        notNull(topic, "Topic must be declared");

        final List<Handler> listeners = this.listeners.get(topic);

        listeners.forEach(handler -> handler.handle(object));
    }

    public void register(String topic, Handler handler) {

        notNull(topic, "Topic must be declared");
        notNull(handler, "Handler must be declared");

        listeners.putIfAbsent(topic, new ArrayList<>());
        listeners.get(topic).add(handler);
    }

    @FunctionalInterface
    public interface Handler {
        void handle(Object data);
    }


}
