package com.battlezone.megamachines.messaging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBus {

    private static Map<Class, List<Subscriber>> subscribers = new HashMap<>();

    public static void register(Object toRegister) {
        Method[] methods = toRegister.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventListener.class)) {
                Class[] parameters = method.getParameterTypes();
                for (Class parameter : parameters) {
                    if (subscribers.containsKey(parameter)) {
                        List<Subscriber> currentMethods = subscribers.get(parameter);
                        currentMethods.add(new Subscriber(toRegister, method));
                        subscribers.put(parameter, currentMethods);
                    } else {
                        List<Subscriber> currentMethods = new ArrayList<>();
                        currentMethods.add(new Subscriber(toRegister, method));
                        subscribers.put(parameter, currentMethods);
                    }
                }
            }
        }
        System.out.println(subscribers);
    }

    public static void remove(Object toRemove) {
        Method[] methods = toRemove.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventListener.class)) {
                Class[] parameters = method.getParameterTypes();
                for (Class parameter : parameters) {
                    if (subscribers.containsKey(parameter)) {
                        List<Subscriber> currentMethods = subscribers.get(parameter);
                        currentMethods.remove(new Subscriber(toRemove, method));
                    }
                }
            }
        }
    }

    public static void fire(Object payload) {
        List<Subscriber> listeners = subscribers.getOrDefault(payload.getClass(), new ArrayList<>());
        for (Subscriber listener : listeners) {
            try {
                listener.getMethod().invoke(listener.getSubscriber(), payload);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (listeners.isEmpty()) {
            System.err.println("Dead event: " + payload.toString());
        }
    }
}
