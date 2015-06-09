package org.play.dependencyinjection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.play.dependencyinjection.resolvers.DependencyInjectionResolver;
import org.play.dependencyinjection.resources.Constants;
import org.play.dependencyinjection.resources.controllers.ParentController;
import org.play.dependencyinjection.resources.controllers.simple.SimpleController;
import org.play.dependencyinjection.resources.controllers.withPropertiesWithoutSameQualifier.WithPropertiesWithoutSameQualifier;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImplementationsWithoutSameQualifier.impl.ImplementationManyImplementationsWithoutSameQualifierOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImplementationsWithoutSameQualifier.impl.ImplementationManyImplementationsWithoutSameQualifierTwo;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.spi.ITestInterfaceNested;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.impl.ImplementationSimple;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DependencyInjectionPoolTest {


	@Test
	public void testA_ChecksSingletonPatternTest() throws DependencyInjectionException {

        assertNotNull (DependencyInjectionPool.instance());
    }


	@Test(expected=DependencyInjectionException.class)
    public void testB_AddNewNullResolverTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (null);
    }


	@Test(expected=DependencyInjectionException.class)
    public void testC_InitializeControllersResolverWithNullControllersPackageTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().initializeControllersResolver (null, ParentController.class);
    }


	@Test(expected=DependencyInjectionException.class)
    public void testD_InitializeControllersResolverWithNullParentControllerTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().initializeControllersResolver (Constants.controllerSimplePath, null);
    }


	@Test(expected=DependencyInjectionException.class)
    public void testE_GetResolverThatPoolDoesNotManageTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class));
		DependencyInjectionPool.instance().getResolver (Constants.nestedDILInterfacesPath);
    }


	@Test
    public void testF_OverwriteDuplicateResolverTest() throws DependencyInjectionException {

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
			          resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());
    }


	@Test
    public void testG_InitializeControllersResolverWithDescendingResolverHierarchyTest() throws DependencyInjectionException {

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
    public void testH_InitializeControllersResolverWithAscendingResolverHierarchyTest() throws DependencyInjectionException {

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
    public void testI_GetResolverWithNullInterfaceClazzTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().getResolver (null);
    }


	@Test
    public void testJ_GetResolverThatPoolManagesTest() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
                                                                                           ,Constants.simpleDILImplementationPath
                                                                                           ,ITestInterfaceSimple.class));

		DependencyInjectionResolver resolver = DependencyInjectionPool.instance().getResolver (Constants.simpleDILInterfacesPath);

		assertNotNull (resolver);
		assertEquals (new ImplementationSimple().testSimpleInterface(),
			          resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());
    }


	@Test
    public void testK_GetResolversLessGivenNullInterfaceTest() throws DependencyInjectionException {

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

			if (currentResolver.getInterfacesPackage().equals (Constants.withoutImplementationDILInterfacesPath))
				expectedImplementations[2] = true;
		}
		assertTrue (expectedImplementations[0]);
		assertTrue (expectedImplementations[1]);
		assertFalse (expectedImplementations[2]);
    }


	@Test
    public void testL_GetResolversLessGivenInterfaceTest() throws DependencyInjectionException {

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
			          resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());
    }


	@Test
    public void testM_InitializeControllersResolverWithPropertiesWithoutSameQualifier() throws DependencyInjectionException {

		DependencyInjectionPool.instance().addNewResolver (new DependencyInjectionResolver (Constants.manyImplementationsWithoutSameQualifierDILInterfacesPath
                                                                                           ,Constants.manyImplementationsWithoutSameQualifierDILImplementationPath))

                                          .initializeControllersResolver (Constants.controllerWithPropertiesWithoutSameQualifierPath, ParentController.class);

		assertEquals (new ImplementationManyImplementationsWithoutSameQualifierOne().testInterfaceManyImplementationsWithoutSameQualifier()
				     ,WithPropertiesWithoutSameQualifier.interfaceWithoutImplementationOne());

		assertEquals (new ImplementationManyImplementationsWithoutSameQualifierTwo().testInterfaceManyImplementationsWithoutSameQualifier()
			         ,WithPropertiesWithoutSameQualifier.interfaceWithoutImplementationTwo());
    }

}