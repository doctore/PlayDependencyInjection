package org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.impl;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.ITestInterfaceCrossReferencesOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.ITestInterfaceCrossReferencesTwo;

public class ImplementationCrossReferencesOne implements ITestInterfaceCrossReferencesOne {

	@WithDependencyInjection
	private ITestInterfaceCrossReferencesTwo iTestInterfaceCrossReferencesTwo;


	@Override
	public String testInterfaceCrossReferencesOne() {

		return "testCrossReferencesInterface One";
	}


	@Override
	public String testCallCrossReferencesTwo() {

		return iTestInterfaceCrossReferencesTwo.testInterfaceCrossReferencesTwo();
	}

}