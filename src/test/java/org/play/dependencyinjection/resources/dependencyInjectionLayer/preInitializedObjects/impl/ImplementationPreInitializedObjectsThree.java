package org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.spi.ITestInterfacePreInitializedObjectsThree;

public class ImplementationPreInitializedObjectsThree implements ITestInterfacePreInitializedObjectsThree {

	@Override
	public String testInterfacePreInitializedObjectsThree() {
		
		return "testInterfacePreInitializedObjectsThree";
	}

}