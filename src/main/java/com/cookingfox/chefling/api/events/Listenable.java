package com.cookingfox.chefling.api.events;

import java.util.Collection;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 14/12/15.
 */
public interface Listenable {

    Collection<Listener> getListeners();

}
