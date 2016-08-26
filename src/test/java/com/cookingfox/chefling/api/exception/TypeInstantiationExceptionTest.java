package com.cookingfox.chefling.api.exception;

import com.cookingfox.fixtures.chefling.PrivateConstructor;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link TypeInstantiationException}.
 */
public class TypeInstantiationExceptionTest {

    @Test
    public void constructor_should_accept_invocation_target_exception() throws Exception {
        String message = "example error";
        InvocationTargetException target = new InvocationTargetException(new Exception(message));

        try {
            throw new TypeInstantiationException(PrivateConstructor.class, target);
        } catch (TypeInstantiationException e) {
            assertTrue(e.getMessage().contains(message));
        }
    }

    @Test
    public void constructor_should_accept_normal_exception() throws Exception {
        String message = "example error";
        Exception target = new Exception(message);

        try {
            throw new TypeInstantiationException(PrivateConstructor.class, target);
        } catch (TypeInstantiationException e) {
            assertTrue(e.getMessage().contains(message));
        }
    }

}
