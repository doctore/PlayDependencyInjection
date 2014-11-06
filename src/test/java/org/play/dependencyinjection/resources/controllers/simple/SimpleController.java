package org.play.dependencyinjection.resources.controllers.simple;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.controllers.ParentController;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.spi.ITestInterfaceNested;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

public class SimpleController extends ParentController {

	@WithDependencyInjection
	private static ITestInterfaceSimple iTestInterfaceSimple;

	@WithDependencyInjection
	private static ITestInterfaceNested iTestInterfaceNested;


    public static String interfaceSimple() {

        return iTestInterfaceSimple.testSimpleInterface();
    }


    public static String interfaceNested() {

        return iTestInterfaceNested.testInterfaceNested();
    }    

}