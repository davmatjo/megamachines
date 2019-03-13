package com.battlezone.megamachines.messaging;

import java.lang.reflect.Method;

public class Subscriber {

    private final Object subscriber;
    private final Method method;

    /**
     * Creates a subscriber which stores the instance that is listening and the method that needs to be called
     * @param subscriber The particular instance that is listening
     * @param method The method that needs to be called for this type
     */
    Subscriber(Object subscriber, Method method) {
        this.subscriber = subscriber;
        this.method = method;
    }

    Object getSubscriber() {
        return subscriber;
    }

    public Method getMethod() {
        return method;
    }
}
