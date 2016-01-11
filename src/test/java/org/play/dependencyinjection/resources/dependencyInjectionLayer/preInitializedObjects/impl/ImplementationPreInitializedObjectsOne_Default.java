package org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.spi.ITestInterfacePreInitializedObjectsOne;

public class ImplementationPreInitializedObjectsOne_Default implements ITestInterfacePreInitializedObjectsOne {

	private String privateStringProperty;


	@Override
	public String getValueOfPrivateStringProperty() {

		return privateStringProperty;
	}

}