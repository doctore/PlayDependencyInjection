package org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.alt;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

public class ImplementationSimpleAlt implements ITestInterfaceSimple {


	@Override
	public String testSimpleInterface() {

		return "testSimpleInterface alternative";
	}

}