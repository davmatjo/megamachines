package com.battlezone.megamachines.messaging;

import com.battlezone.megamachines.events.Pooled;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBus {

    private static Map<Class, List<Subscriber>> subscribers = new HashMap<>();

    /**
     * Registers an object onto the message bus. All methods annotated with @EventListener will be called when an
     * object with the correct type is fired onto the bus
     * @param toRegister The class to register
     */
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
    }

    /**
     * Fires an object onto the message bus. Every instance that is listening for this object will be notified
     * @param payload object to fire
     */
    public static void fire(Object payload) {
        // Fire events to all listeners
        List<Subscriber> listeners = subscribers.getOrDefault(payload.getClass(), new ArrayList<>());
        for (int i=0; i<listeners.size(); i++) {
            Subscriber listener = listeners.get(i);
            try {
                listener.getMethod().invoke(listener.getSubscriber(), payload);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (listeners.isEmpty()) {
            if (payload instanceof Pooled) {
                ((Pooled) payload).delete();
            }
            System.err.println("Dead event " + payload.toString());
        }
    }
}
