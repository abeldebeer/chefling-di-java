package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.exception.*;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MapTypeCommandImpl}.
 */
public class MapTypeCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void mapType_should_throw_if_type_null() throws Exception {
        container.mapType(null, NoConstructor.class);
    }

    @Test(expected = NullValueNotAllowedException.class)
    public void mapType_should_throw_if_subType_null() throws Exception {
        container.mapType(getClass(), null);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapType_should_throw_if_mapping_exists() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
    }

    @Test(expected = NotASubTypeException.class)
    public void mapType_should_throw_if_sub_type_same_as_type() throws Exception {
        container.mapType(NoMethodImplementation.class, NoMethodImplementation.class);
    }

    @Test(expected = NotASubTypeException.class)
    public void mapType_should_throw_if_sub_type_not_extends_type() throws Exception {
        Class type = NoConstructor.class;
        Class subType = Object.class;

        container.mapType(type, subType);
    }

    @Test
    public void mapType_should_throw_if_base_type_not_allowed() throws Exception {
        for (Map.Entry<Class, Class> entry : getNotAllowedSubTypes().entrySet()) {
            try {
                container.mapType(entry.getKey(), entry.getValue());

                fail("Did not receive expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = TypeNotInstantiableException.class)
    public void mapType_should_throw_if_sub_type_not_instantiable() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapType_should_throw_when_first_mapInstance_then_mapType() throws Exception {
        container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
    }

    @Test
    public void mapType_should_allow_to_map_to_previous_mapping() throws Exception {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);
    }

    @Test
    public void mapType_should_take_into_account_subType_instance_mapping() throws Exception {
        container.mapInstance(InterfaceSegregation.Person.class, new InterfaceSegregation.JohnDoe());
        container.mapType(InterfaceSegregation.Talkable.class, InterfaceSegregation.Person.class);
        container.mapType(InterfaceSegregation.Walkable.class, InterfaceSegregation.Person.class);
    }

    @Test
    public void mapType_should_pass_concurrency_test() {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<>();

        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
                } catch (ContainerException e) {
                    exceptions.add(e);
                }
            }
        };

        runConcurrencyTest(test, numTests);

        assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

}
