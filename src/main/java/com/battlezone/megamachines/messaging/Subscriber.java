package com.battlezone.megamachines.messaging;

import java.lang.reflect.Method;

public class Subscriber {

    private final Object subscriber;
    private final Method method;

    public Subscriber(Object subscriber, Method method) {
        this.subscriber = subscriber;
        this.method = method;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public Method getMethod() {
        return method;
    }
}
