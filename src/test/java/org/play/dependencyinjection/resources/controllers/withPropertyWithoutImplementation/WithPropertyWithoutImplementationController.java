package org.play.dependencyinjection.resources.controllers.withPropertyWithoutImplementation;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.controllers.ParentController;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.withoutImplementation.spi.ITestInterfaceWithoutImplementation;

public class WithPropertyWithoutImplementationController extends ParentController {

	@WithDependencyInjection
	private static ITestInterfaceWithoutImplementation iTestInterfaceWithoutImplementation;


    public static String interfaceWithoutImplementation() {

        return iTestInterfaceWithoutImplementation.testInterfaceWithoutImplementation();
    }

}