package org.play.dependencyinjection.resolvers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.play.dependencyinjection.resources.Constants;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.impl.ImplementationCrossReferencesOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.impl.ImplementationCrossReferencesTwo;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.IInterfaceCrossReferences;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.ITestInterfaceCrossReferencesOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.ITestInterfaceCrossReferencesTwo;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.impl.ImplementationDifferentBranchesOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.impl.ImplementationDifferentBranchesTwo;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.spi.ITestInterfaceDifferentBranchesOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.spi.ITestInterfaceDifferentBranchesTwo;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.impl.ImplementationNested;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.spi.ITestInterfaceNested;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.alt.ImplementationSimpleAlt;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.impl.ImplementationSimple;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

public class DependencyInjectionResolverTest {


	@Test(expected=DependencyInjectionException.class)
    public void newInstanceUsingFirstConstructorWithNullInterfacePackageToResolveTest() throws DependencyInjectionException {

        new DependencyInjectionResolver (null);
    }


	@Test
    public void newInstanceUsingFirstConstructorWithNotNullInterfacePackageToResolveTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.nestedDILInterfacesPath);
		
		assertNotNull (resolver.getInterfacesPackage());
		assertTrue (resolver.getInterfacesPackage().equals (Constants.nestedDILInterfacesPath));
    }


	@Test(expected=DependencyInjectionException.class)
    public void newInstanceUsingSecondConstructorWithEmptyInterfacePackageToResolveTest() throws DependencyInjectionException {

        new DependencyInjectionResolver ("", Constants.nestedDILImplementationPath);
    }


	@Test(expected=DependencyInjectionException.class)
    public void newInstanceUsingSecondConstructorWithNullImplementationPackageTest() throws DependencyInjectionException {

        new DependencyInjectionResolver (Constants.nestedDILInterfacesPath, null);
    }


	@Test(expected=DependencyInjectionException.class)
    public void newInstanceUsingSecondConstructorWithEmptyImplementationPackageTest() throws DependencyInjectionException {

        new DependencyInjectionResolver (Constants.nestedDILInterfacesPath, "");
    }


	@Test(expected=DependencyInjectionException.class)
    public void testingInterfaceWithoutImplementationTest() throws DependencyInjectionException {

		new DependencyInjectionResolver (Constants.withoutImplDILInterfacesPath, Constants.withoutImplDILImplementationPath);
    }


	@Test(expected=DependencyInjectionException.class)
    public void testingInterfaceManyImplementationTest() throws DependencyInjectionException {

		new DependencyInjectionResolver (Constants.manyImplDILInterfacesPath, Constants.manyImplDILImplementationPath);
    }


	@Test
    public void testingSimpleDependencyInjectionTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
				                                                               ,Constants.simpleDILImplementationPath
				                                                               ,ITestInterfaceSimple.class);
		assertNotNull (resolver.getInterfacesPackage());
		assertTrue (resolver.getInterfacesPackage().equals (Constants.simpleDILInterfacesPath));
		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class));

		assertEquals (new ImplementationSimple().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());
    }


	@Test
    public void testingSimpleDependencyInjectionWithoutInterfaceFilterTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
				                                                               ,Constants.simpleDILImplementationPath);
		assertNotNull (resolver.getInterfacesPackage());
		assertTrue (resolver.getInterfacesPackage().equals (Constants.simpleDILInterfacesPath));
		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class));

		assertEquals (new ImplementationSimple().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());
    }


	@Test
    public void testingAlternativeSimpleDependencyInjectionTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
				                                                               ,Constants.simpleDILImplementationPath
				                                                               ,ITestInterfaceSimple.class);

		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class));
		assertTrue (resolver.getImplementation (ITestInterfaceSimple.class).getClass() == ImplementationSimple.class);
		assertFalse (resolver.getImplementation (ITestInterfaceSimple.class).getClass() == ImplementationSimpleAlt.class);

		assertEquals (new ImplementationSimple().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());

		// Binds to the "alternative implementation"
		resolver.bind (ITestInterfaceSimple.class, ImplementationSimpleAlt.class);
		assertFalse (resolver.getImplementation (ITestInterfaceSimple.class).getClass() == ImplementationSimple.class);
		assertTrue (resolver.getImplementation (ITestInterfaceSimple.class).getClass() == ImplementationSimpleAlt.class);

		assertEquals (new ImplementationSimpleAlt().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class).testSimpleInterface());
    }


	@Test
    public void testingLoadingAllDependenciesOfInterfaceTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath);

		resolver.bind (ITestInterfaceNested.class, ImplementationNested.class)
		        .bind (ITestInterfaceSimple.class, ImplementationSimpleAlt.class)
                .resolveDependenciesOfInterface (ITestInterfaceNested.class);

		assertNotNull (resolver.getImplementation (ITestInterfaceNested.class));
		assertTrue (resolver.getImplementation (ITestInterfaceNested.class).getClass() == ImplementationNested.class);

		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class));
		assertTrue (resolver.getImplementation (ITestInterfaceSimple.class).getClass() == ImplementationSimpleAlt.class);

		assertEquals ("testNestedInterface" + " / " + new ImplementationSimpleAlt().testSimpleInterface(),
			          resolver.getImplementation (ITestInterfaceNested.class).testInterfaceNested());
    }


	@Test
	public void testingCrossReferences() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.crossReferencesDILInterfacesPath
				                                                               ,Constants.crossReferencesDILImplementationPath
				                                                               ,IInterfaceCrossReferences.class);
		resolver.resolveAllClassPropertiesOfImplementations();

		assertNotNull (resolver.getImplementation (ITestInterfaceCrossReferencesOne.class));
		assertNotNull (resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class));

		assertTrue (resolver.getImplementation (ITestInterfaceCrossReferencesOne.class).getClass() == ImplementationCrossReferencesOne.class);
		assertTrue (resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class).getClass() == ImplementationCrossReferencesTwo.class);

		assertEquals (new ImplementationCrossReferencesOne().testInterfaceCrossReferencesOne(),
				      resolver.getImplementation (ITestInterfaceCrossReferencesOne.class).testInterfaceCrossReferencesOne());

		assertEquals (new ImplementationCrossReferencesOne().testInterfaceCrossReferencesOne(),
			          resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class).testCallCrossReferencesOne());

		assertEquals (new ImplementationCrossReferencesTwo().testInterfaceCrossReferencesTwo(),
			          resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class).testInterfaceCrossReferencesTwo());

		assertEquals (new ImplementationCrossReferencesTwo().testInterfaceCrossReferencesTwo(),
			          resolver.getImplementation (ITestInterfaceCrossReferencesOne.class).testCallCrossReferencesTwo());
	}


	@Test
	public void testingFilterResolverByInterfaceClass() throws DependencyInjectionException {

		DependencyInjectionResolver resolverOne = new DependencyInjectionResolver (Constants.differentBranchesDILInterfacesPath
                                                                                  ,Constants.differentBranchesDILImplementationPath
                                                                                  ,ITestInterfaceDifferentBranchesOne.class);

		DependencyInjectionResolver resolverTwo = new DependencyInjectionResolver (Constants.differentBranchesDILInterfacesPath
                                                                                  ,Constants.differentBranchesDILImplementationPath
                                                                                  ,ITestInterfaceDifferentBranchesTwo.class);

		assertNotNull (resolverOne.getImplementation (ITestInterfaceDifferentBranchesOne.class));
		assertNotNull (resolverTwo.getImplementation (ITestInterfaceDifferentBranchesTwo.class));

		assertTrue (resolverOne.getImplementation (ITestInterfaceDifferentBranchesOne.class).getClass() == ImplementationDifferentBranchesOne.class);
		assertTrue (resolverTwo.getImplementation (ITestInterfaceDifferentBranchesTwo.class).getClass() == ImplementationDifferentBranchesTwo.class);

		assertEquals (new ImplementationDifferentBranchesOne().testInterfaceDifferentBranchesOne(),
				      resolverOne.getImplementation (ITestInterfaceDifferentBranchesOne.class).testInterfaceDifferentBranchesOne());

		assertEquals (new ImplementationDifferentBranchesTwo().testInterfaceDifferentBranchesTwo(),
			          resolverTwo.getImplementation (ITestInterfaceDifferentBranchesTwo.class).testInterfaceDifferentBranchesTwo());
	}

}