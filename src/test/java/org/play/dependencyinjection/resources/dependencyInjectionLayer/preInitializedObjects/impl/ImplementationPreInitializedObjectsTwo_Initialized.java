package org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.impl;

import org.play.dependencyinjection.annotations.DependencyInjectionQualifier;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.spi.ITestInterfacePreInitializedObjectsTwo;

@DependencyInjectionQualifier("implementationTwo_Initialized")
public class ImplementationPreInitializedObjectsTwo_Initialized implements ITestInterfacePreInitializedObjectsTwo {

	private Integer privateIntegerProperty;
	
	
	public ImplementationPreInitializedObjectsTwo_Initialized (Integer privateIntegerProperty) {

		this.privateIntegerProperty = privateIntegerProperty;
	}


	@Override
	public Integer getValueOfPrivateIntegerProperty() {

		return privateIntegerProperty;
	}

}