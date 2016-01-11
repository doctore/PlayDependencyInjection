package org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.impl;

import org.play.dependencyinjection.annotations.DependencyInjectionQualifier;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.spi.ITestInterfacePreInitializedObjectsOne;

@DependencyInjectionQualifier("implementationOne_Initialized")
public class ImplementationPreInitializedObjectsOne_Initialized implements ITestInterfacePreInitializedObjectsOne {

	private String privateStringProperty;
	
	
	public ImplementationPreInitializedObjectsOne_Initialized (String privateStringProperty) {

		this.privateStringProperty = privateStringProperty;
	}


	@Override
	public String getValueOfPrivateStringProperty() {

		return privateStringProperty;
	}

}