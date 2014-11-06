package org.play.dependencyinjection.resources.dependencyInjectionLayer.simple.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceSimple extends IInterfaceSimple {

	public String testSimpleInterface();

}