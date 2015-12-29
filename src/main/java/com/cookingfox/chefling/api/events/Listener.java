package com.cookingfox.chefling.api.events;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 14/12/15.
 */
public interface Listener<T> {

    void onEvent(T event);

}
