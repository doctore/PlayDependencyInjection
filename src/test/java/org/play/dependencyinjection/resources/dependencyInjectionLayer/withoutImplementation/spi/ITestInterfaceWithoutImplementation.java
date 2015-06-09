package org.play.dependencyinjection.resources.dependencyInjectionLayer.withoutImplementation.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceWithoutImplementation extends IInterfaceWithoutImplementation {

	public String testInterfaceWithoutImplementation();

}