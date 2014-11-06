package org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.impl;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.spi.ITestInterfaceNested;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

public class ImplementationNested implements ITestInterfaceNested {

	@WithDependencyInjection
	private ITestInterfaceSimple iTestInterfaceSimple;


	@Override
	public String testInterfaceNested() {

		return "testNestedInterface" + " / " + iTestInterfaceSimple.testSimpleInterface();
	}

}