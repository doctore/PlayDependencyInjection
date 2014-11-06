package org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImpl.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImpl.spi.ITestInterfaceManyImpl;

public class ImplementationManyImplTwo implements ITestInterfaceManyImpl {


	@Override
	public String testInterfaceManyImpl() {

		return "testManyImplInterface Two";
	}

}