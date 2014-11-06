package org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.impl;

import org.play.dependencyinjection.resources.dependencyInjectionLayer.differentBranches.spi.ITestInterfaceDifferentBranchesOne;

public class ImplementationDifferentBranchesOne implements ITestInterfaceDifferentBranchesOne {

	@Override
	public String testInterfaceDifferentBranchesOne() {

		return "testDifferentBranchesInterface One";
	}

}