package org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.impl;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.ITestInterfaceCrossReferencesOne;
import org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi.ITestInterfaceCrossReferencesTwo;

public class ImplementationCrossReferencesTwo implements ITestInterfaceCrossReferencesTwo {

	@WithDependencyInjection
	private ITestInterfaceCrossReferencesOne iTestInterfaceCrossReferencesOne;


	@Override
	public String testInterfaceCrossReferencesTwo() {

		return "testCrossReferencesInterface Two";
	}


	@Override
	public String testCallCrossReferencesOne() {

		return iTestInterfaceCrossReferencesOne.testInterfaceCrossReferencesOne();
	}

}