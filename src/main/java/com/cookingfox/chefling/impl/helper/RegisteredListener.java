package com.cookingfox.chefling.impl.helper;

import java.lang.reflect.Method;

/**
 * Created by abeldebeer on 16/03/16.
 */
public class RegisteredListener {

    public final Class eventClass;
    public final Method method;
    public final Object subscriber;

    public RegisteredListener(Object subscriber, Method method, Class eventClass) {
        this.eventClass = eventClass;
        this.method = method;
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "RegisteredListener{" +
                "subscriber=" + subscriber.getClass().getSimpleName() +
                '}';
    }

}
