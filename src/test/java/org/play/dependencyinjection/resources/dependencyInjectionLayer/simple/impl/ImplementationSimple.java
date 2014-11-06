package org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi.ITestInterfaceSimple;

public class ImplementationSimple implements ITestInterfaceSimple {


	@Override
	public String testSimpleInterface() {

		return "testSimpleInterface";
	}

}