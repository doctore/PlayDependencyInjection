package org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceCrossReferencesTwo extends IInterfaceCrossReferences {

	public String testInterfaceCrossReferencesTwo();

	public String testCallCrossReferencesOne();

}