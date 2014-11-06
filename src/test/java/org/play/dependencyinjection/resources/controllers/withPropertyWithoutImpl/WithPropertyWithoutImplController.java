package org.play.dependencyinjection.resources.controllers.withPropertyWithoutImpl;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.controllers.ParentController;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.withoutImpl.spi.ITestInterfaceWithoutImpl;

public class WithPropertyWithoutImplController extends ParentController {

	@WithDependencyInjection
	private static ITestInterfaceWithoutImpl iTestInterfaceWithoutImpl;


    public static String interfaceWithoutImpl() {

        return iTestInterfaceWithoutImpl.testInterfaceWithoutImpl();
    }

}