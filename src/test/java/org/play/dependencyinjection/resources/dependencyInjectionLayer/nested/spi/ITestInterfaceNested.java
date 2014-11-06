package org.play.dependencyinjection.resources.dependencyInjectionLayer.nested.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfaceNested extends IInterfaceNested {

	public String testInterfaceNested();

}