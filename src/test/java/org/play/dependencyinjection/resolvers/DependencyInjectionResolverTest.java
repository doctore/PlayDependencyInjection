package org.play.dependencyinjection.resolvers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.play.dependencyinjection.annotations.DependencyInjectionQualifier;
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
import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImplementationsWithoutSameQualifier.impl.ImplementationManyImplementationsWithoutSameQualifierOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImplementationsWithoutSameQualifier.impl.ImplementationManyImplementationsWithoutSameQualifierTwo;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImplementationsWithoutSameQualifier.spi.ITestInterfaceManyImplementationsWithoutSameQualifier;
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

		new DependencyInjectionResolver (Constants.withoutImplementationDILInterfacesPath, Constants.withoutImplementationDILImplementationPath);
    }


	@Test(expected=DependencyInjectionException.class)
    public void testingInterfaceManyImplementationWithoutQualifierTest() throws DependencyInjectionException {

		new DependencyInjectionResolver (Constants.manyImplementationsWithoutQualifierDILInterfacesPath
				                        ,Constants.manyImplementationsWithoutQualifierDILImplementationPath);
    }


	@Test(expected=DependencyInjectionException.class)
    public void testingInterfaceManyImplementationWithSameQualifierTest() throws DependencyInjectionException {

		new DependencyInjectionResolver (Constants.manyImplementationsWithSameQualifierDILInterfacesPath
				                        ,Constants.manyImplementationsWithSameQualifierDILImplementationPath);
    }


	@Test
    public void testingInterfaceManyImplementationWithoutSameQualifierTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.manyImplementationsWithoutSameQualifierDILInterfacesPath
				                                                               ,Constants.manyImplementationsWithoutSameQualifierDILImplementationPath);
		assertNotNull (resolver);
		assertNotNull (resolver.getInterfacesPackage());
		assertTrue (resolver.getInterfacesPackage().equals (Constants.manyImplementationsWithoutSameQualifierDILInterfacesPath));

		assertNotNull (resolver.getImplementation (ITestInterfaceManyImplementationsWithoutSameQualifier.class, null));
		assertEquals (new ImplementationManyImplementationsWithoutSameQualifierTwo().testInterfaceManyImplementationsWithoutSameQualifier()
				     ,resolver.getImplementation (ITestInterfaceManyImplementationsWithoutSameQualifier.class, null).testInterfaceManyImplementationsWithoutSameQualifier());

		String qualifierValue = null;
		DependencyInjectionQualifier annotation = (DependencyInjectionQualifier)ImplementationManyImplementationsWithoutSameQualifierOne.class.getAnnotation (DependencyInjectionQualifier.class);
		if (annotation != null)
			qualifierValue = annotation.value();

		assertNotNull (resolver.getImplementation (ITestInterfaceManyImplementationsWithoutSameQualifier.class, qualifierValue));
		assertEquals (new ImplementationManyImplementationsWithoutSameQualifierOne().testInterfaceManyImplementationsWithoutSameQualifier()
				     ,resolver.getImplementation (ITestInterfaceManyImplementationsWithoutSameQualifier.class, qualifierValue).testInterfaceManyImplementationsWithoutSameQualifier());
    }


	@Test
    public void testingSimpleDependencyInjectionTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
				                                                               ,Constants.simpleDILImplementationPath
				                                                               ,ITestInterfaceSimple.class);
		assertNotNull (resolver);
		assertNotNull (resolver.getInterfacesPackage());
		assertTrue (resolver.getInterfacesPackage().equals (Constants.simpleDILInterfacesPath));
		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class, null));

		assertEquals (new ImplementationSimple().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());
    }


	@Test
    public void testingSimpleDependencyInjectionWithoutInterfaceFilterTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
				                                                               ,Constants.simpleDILImplementationPath);
		assertNotNull (resolver);
		assertNotNull (resolver.getInterfacesPackage());
		assertTrue (resolver.getInterfacesPackage().equals (Constants.simpleDILInterfacesPath));
		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class, null));

		assertEquals (new ImplementationSimple().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());
    }


	@Test
    public void testingAlternativeSimpleDependencyInjectionTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath
				                                                               ,Constants.simpleDILImplementationPath
				                                                               ,ITestInterfaceSimple.class);
		assertNotNull (resolver);
		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class, null));
		assertTrue (resolver.getImplementation (ITestInterfaceSimple.class, null).getClass() == ImplementationSimple.class);
		assertFalse (resolver.getImplementation (ITestInterfaceSimple.class, null).getClass() == ImplementationSimpleAlt.class);

		assertEquals (new ImplementationSimple().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());

		// Binds to the "alternative implementation"
		resolver.bind (ITestInterfaceSimple.class, ImplementationSimpleAlt.class);
		assertFalse (resolver.getImplementation (ITestInterfaceSimple.class, null).getClass() == ImplementationSimple.class);
		assertTrue (resolver.getImplementation (ITestInterfaceSimple.class, null).getClass() == ImplementationSimpleAlt.class);

		assertEquals (new ImplementationSimpleAlt().testSimpleInterface(),
				      resolver.getImplementation (ITestInterfaceSimple.class, null).testSimpleInterface());
    }


	@Test
    public void testingLoadingAllDependenciesOfInterfaceTest() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.simpleDILInterfacesPath);

		resolver.bind (ITestInterfaceNested.class, ImplementationNested.class)
		        .bind (ITestInterfaceSimple.class, ImplementationSimpleAlt.class)
                .resolveDependenciesOfInterface (ITestInterfaceNested.class, null);

		assertNotNull (resolver.getImplementation (ITestInterfaceNested.class, null));
		assertTrue (resolver.getImplementation (ITestInterfaceNested.class, null).getClass() == ImplementationNested.class);

		assertNotNull (resolver.getImplementation (ITestInterfaceSimple.class, null));
		assertTrue (resolver.getImplementation (ITestInterfaceSimple.class, null).getClass() == ImplementationSimpleAlt.class);

		assertEquals ("testNestedInterface" + " / " + new ImplementationSimpleAlt().testSimpleInterface(),
			          resolver.getImplementation (ITestInterfaceNested.class, null).testInterfaceNested());
    }


	@Test
	public void testingCrossReferences() throws DependencyInjectionException {

		DependencyInjectionResolver resolver = new DependencyInjectionResolver (Constants.crossReferencesDILInterfacesPath
				                                                               ,Constants.crossReferencesDILImplementationPath
				                                                               ,IInterfaceCrossReferences.class);
		resolver.resolveAllClassPropertiesOfImplementations();

		assertNotNull (resolver.getImplementation (ITestInterfaceCrossReferencesOne.class, null));
		assertNotNull (resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class, null));

		assertTrue (resolver.getImplementation (ITestInterfaceCrossReferencesOne.class, null).getClass() == ImplementationCrossReferencesOne.class);
		assertTrue (resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class, null).getClass() == ImplementationCrossReferencesTwo.class);

		assertEquals (new ImplementationCrossReferencesOne().testInterfaceCrossReferencesOne(),
				      resolver.getImplementation (ITestInterfaceCrossReferencesOne.class, null).testInterfaceCrossReferencesOne());

		assertEquals (new ImplementationCrossReferencesOne().testInterfaceCrossReferencesOne(),
			          resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class, null).testCallCrossReferencesOne());

		assertEquals (new ImplementationCrossReferencesTwo().testInterfaceCrossReferencesTwo(),
			          resolver.getImplementation (ITestInterfaceCrossReferencesTwo.class, null).testInterfaceCrossReferencesTwo());

		assertEquals (new ImplementationCrossReferencesTwo().testInterfaceCrossReferencesTwo(),
			          resolver.getImplementation (ITestInterfaceCrossReferencesOne.class, null).testCallCrossReferencesTwo());
	}


	@Test
	public void testingFilterResolverByInterfaceClass() throws DependencyInjectionException {

		DependencyInjectionResolver resolverOne = new DependencyInjectionResolver (Constants.differentBranchesDILInterfacesPath
                                                                                  ,Constants.differentBranchesDILImplementationPath
                                                                                  ,ITestInterfaceDifferentBranchesOne.class);

		DependencyInjectionResolver resolverTwo = new DependencyInjectionResolver (Constants.differentBranchesDILInterfacesPath
                                                                                  ,Constants.differentBranchesDILImplementationPath
                                                                                  ,ITestInterfaceDifferentBranchesTwo.class);

		assertNotNull (resolverOne.getImplementation (ITestInterfaceDifferentBranchesOne.class, null));
		assertNotNull (resolverTwo.getImplementation (ITestInterfaceDifferentBranchesTwo.class, null));

		assertTrue (resolverOne.getImplementation (ITestInterfaceDifferentBranchesOne.class, null).getClass() == ImplementationDifferentBranchesOne.class);
		assertTrue (resolverTwo.getImplementation (ITestInterfaceDifferentBranchesTwo.class, null).getClass() == ImplementationDifferentBranchesTwo.class);

		assertEquals (new ImplementationDifferentBranchesOne().testInterfaceDifferentBranchesOne(),
				      resolverOne.getImplementation (ITestInterfaceDifferentBranchesOne.class, null).testInterfaceDifferentBranchesOne());

		assertEquals (new ImplementationDifferentBranchesTwo().testInterfaceDifferentBranchesTwo(),
			          resolverTwo.getImplementation (ITestInterfaceDifferentBranchesTwo.class, null).testInterfaceDifferentBranchesTwo());
	}

}