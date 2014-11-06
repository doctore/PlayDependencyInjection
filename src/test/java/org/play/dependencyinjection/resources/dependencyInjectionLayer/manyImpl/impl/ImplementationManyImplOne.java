package org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImpl.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImpl.spi.ITestInterfaceManyImpl;

public class ImplementationManyImplOne implements ITestInterfaceManyImpl {


	@Override
	public String testInterfaceManyImpl() {

		return "testManyImplInterface One";
	}

}