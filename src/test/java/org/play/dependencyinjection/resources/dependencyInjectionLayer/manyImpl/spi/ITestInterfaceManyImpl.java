package org.play.dependencyinjection.resources.dependencyInjectionLayer.manyImpl.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceManyImpl extends IInterfaceManyImpl {

	public String testInterfaceManyImpl();

}