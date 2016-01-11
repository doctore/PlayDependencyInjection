package org.play.dependencyinjection.resources.dependencyInjectionLayer.preInitializedObjects.spi;

import org.play.dependencyinjection.annotations.Injectable;

@Injectable
public interface ITestInterfacePreInitializedObjectsTwo {
	
	public Integer getValueOfPrivateIntegerProperty();

}