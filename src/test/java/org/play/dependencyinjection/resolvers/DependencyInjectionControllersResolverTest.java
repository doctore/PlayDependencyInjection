package org.play.dependencyinjection.resolvers;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.play.dependencyinjection.resources.Constants;
import org.play.dependencyinjection.resources.controllers.ParentController;

public class DependencyInjectionControllersResolverTest {


	@Test
    public void checksSingletonPatternTest() throws DependencyInjectionException {

        assertNotNull (DependencyInjectionControllersResolver.instance());
    }


	@Test(expected=DependencyInjectionException.class)
    public void initializeWithNullControllersPackageTest() throws DependencyInjectionException {

		DependencyInjectionControllersResolver.instance().init (null, ParentController.class);
    }


	@Test(expected=DependencyInjectionException.class)
    public void initializeWithNullParentControllerTest() throws DependencyInjectionException {

		DependencyInjectionControllersResolver.instance().init (Constants.controllerSimplePath
				                                               ,ParentController.class);
    }

}