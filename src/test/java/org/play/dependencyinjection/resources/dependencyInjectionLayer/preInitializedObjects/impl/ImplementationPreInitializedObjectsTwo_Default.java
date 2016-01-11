package org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.spi.ITestInterfacePreInitializedObjectsTwo;

public class ImplementationPreInitializedObjectsTwo_Default implements ITestInterfacePreInitializedObjectsTwo {

	private Integer privateIntegerProperty;


	@Override
	public Integer getValueOfPrivateIntegerProperty() {

		return privateIntegerProperty;
	}

}