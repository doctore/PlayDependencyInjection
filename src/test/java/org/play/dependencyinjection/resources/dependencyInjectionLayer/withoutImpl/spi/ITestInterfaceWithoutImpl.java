package org.play.dependencyinjection.resources.dependencyInjectionLayer.withoutImpl.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceWithoutImpl extends IInterfaceWithoutImpl {

	public String testInterfaceWithoutImpl();

}