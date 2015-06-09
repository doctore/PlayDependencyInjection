package org.play.dependencyinjection.resources.controllers.withPropertiesWithoutSameQualifier;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.controllers.ParentController;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImplementationsWithoutSameQualifier.spi.ITestInterfaceManyImplementationsWithoutSameQualifier;

public class WithPropertiesWithoutSameQualifier extends ParentController {

	@WithDependencyInjection(qualifier="implementationOne")
	private static ITestInterfaceManyImplementationsWithoutSameQualifier iTestInterfaceManyImplWSQOne;

	@WithDependencyInjection
	private static ITestInterfaceManyImplementationsWithoutSameQualifier iTestInterfaceManyImplWSQTwo;


    public static String interfaceWithoutImplementationOne() {

        return iTestInterfaceManyImplWSQOne.testInterfaceManyImplementationsWithoutSameQualifier();
    }


    public static String interfaceWithoutImplementationTwo() {

        return iTestInterfaceManyImplWSQTwo.testInterfaceManyImplementationsWithoutSameQualifier();
    }

}