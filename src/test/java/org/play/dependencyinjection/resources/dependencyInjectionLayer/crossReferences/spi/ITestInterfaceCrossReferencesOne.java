package org.play.dependencyinjection.resources.dependencyInjectionLayer.crossReferences.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceCrossReferencesOne extends IInterfaceCrossReferences {

	public String testInterfaceCrossReferencesOne();

	public String testCallCrossReferencesTwo();

}