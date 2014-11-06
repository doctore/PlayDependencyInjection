package org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.spi.ITestInterfaceDifferentBranchesTwo;

public class ImplementationDifferentBranchesTwo implements ITestInterfaceDifferentBranchesTwo {

	@Override
	public String testInterfaceDifferentBranchesTwo() {

		return "testDifferentBranchesInterface Two";
	}

}