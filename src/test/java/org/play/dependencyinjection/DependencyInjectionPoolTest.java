package org.play.dependencyinjection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.play.dependencyinjection.resolvers.DependencyInjectionResolver;
import org.play.dependencyinjection.resources.Constants;
import org.play.dependencyinjection.resources.controllers.ParentController;
import org.play.dependencyinjection.resources.controllers.simple.SimpleController;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.spi.ITestInterfaceNested;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.impl.ImplementationSimple;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

public class DependencyInjectionPoolTest {


	@Test
    public void checksSingletonPatternTest() throws DependencyInjectionException {

        assertNotNull (DependencyInjectionPool.instance());
    }


	@Test(expected=DependencyInjectionException.class)
    public void addNewNullResolverTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (null);
    }


	@Test(expected=DependencyInjectionException.class)
    public void initializeControllersResolverWithNullControllersPackageTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().initializeControllersResolver (null, ParentController.class);
    }


	@Test(expected=DependencyInjectionException.class)
    public void initializeControllersResolverWithNullParentControllerTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().initializeControllersResolver (Constants.controllerSimplePath, null);
    }


	@Test
    public void initializeControllersResolverWithDescendingResolverHierarchyTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class))

                                          .addNewResolver (new DependencyInjectionResolver (Constants.nestedDILInterfacesPath
                                                                                           ,Constants.nestedDILImplementationPath
                                                                                           ,ITestInterfaceNested.class))

                                          .initializeControllersResolver (Constants.controllerSimplePath, ParentController.class);

		assertEquals (new ImplementationSimple().testSimpleInterface(), SimpleController.interfaceSimple());
		assertEquals ("testNestedInterface" + " / " + new ImplementationSimple().testSimpleInterface(),
				      SimpleController.interfaceNested());
    }


	@Test
    public void initializeControllersResolverWithAscendingResolverHierarchyTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.nestedDILInterfacesPath
                                                                                           ,Constants.nestedDILImplementationPath
                                                                                           ,ITestInterfaceNested.class))

		                                  .addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class))

                                          .initializeControllersResolver (Constants.controllerSimplePath, ParentController.class);

		assertEquals (new ImplementationSimple().testSimpleInterface(), SimpleController.interfaceSimple());
		assertEquals ("testNestedInterface" + " / " + new ImplementationSimple().testSimpleInterface(),
				      SimpleController.interfaceNested());
    }


	@Test(expected=DependencyInjectionException.class)
    public void getResolverWithNullInterfaceClazzTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().getResolver (null);
    }


	@Test(expected=DependencyInjectionException.class)
    public void getResolverThatPoolDoesNotManageTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class));
		DependencyInjectionPool.instance().getResolver (Constants.nestedDILInterfacesPath);
    }


	@Test
    public void getResolverThatPoolManagesTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class));

		DependencyInjectionResolver resolver = DependencyInjectionPool.instance().getResolver (Constants.simpleDILInterfacesPath);

		assertNotNull (resolver);
		assertEquals (new ImplementationSimple().testSimpleInterface(),
			          resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());
    }


	@Test
    public void overwriteDuplicateResolverTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class))

                                          .addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class));

		Set<DependencyInjectionResolver> resolvers = DependencyInjectionPool.instance().getResolversLessGivenInterfacePackage (null);

		assertNotNull (resolvers);
		assertEquals (1, resolvers.size());

		DependencyInjectionResolver resolver = null;
		for (DependencyInjectionResolver currentResolver : resolvers)
			resolver = currentResolver;

		assertNotNull (resolver);
		assertEquals (new ImplementationSimple().testSimpleInterface(),
			          resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());
    }


	@Test
    public void getResolversLessGivenNullInterfaceTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class))

                                          .addNewResolver (new DependencyInjectionResolver (Constants.nestedDILInterfacesPath
                                                                                           ,Constants.nestedDILImplementationPath
                                                                                           ,ITestInterfaceNested.class));                                                                                            

		Set<DependencyInjectionResolver> resolvers = DependencyInjectionPool.instance().getResolversLessGivenInterfacePackage (null);

		assertNotNull (resolvers);
		assertEquals (2, resolvers.size());

		boolean[] expectedImplementations = {false, false, false};
		for (DependencyInjectionResolver currentResolver : resolvers) {

			if (currentResolver.getInterfacesPackage().equals (Constants.simpleDILInterfacesPath))
				expectedImplementations[0] = true;

			if (currentResolver.getInterfacesPackage().equals (Constants.nestedDILInterfacesPath))
				expectedImplementations[1] = true;

			if (currentResolver.getInterfacesPackage().equals (Constants.withoutImplDILInterfacesPath))
				expectedImplementations[2] = true;
		}
		assertTrue (expectedImplementations[0]);
		assertTrue (expectedImplementations[1]);
		assertFalse (expectedImplementations[2]);
    }


	@Test
    public void getResolversLessGivenInterfaceTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class))

                                          .addNewResolver (new DependencyInjectionResolver (Constants.nestedDILInterfacesPath
                                                                                           ,Constants.nestedDILImplementationPath
                                                                                           ,ITestInterfaceNested.class));                                                                                            

		Set<DependencyInjectionResolver> resolvers = DependencyInjectionPool.instance().getResolversLessGivenInterfacePackage (Constants.nestedDILInterfacesPath);

		assertNotNull (resolvers);
		assertEquals (1, resolvers.size());

		DependencyInjectionResolver resolver = null;
		for (DependencyInjectionResolver currentResolver : resolvers)
			resolver = currentResolver;

		assertNotNull (resolver);
		assertEquals (new ImplementationSimple().testSimpleInterface(),
			          resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());
    }

}