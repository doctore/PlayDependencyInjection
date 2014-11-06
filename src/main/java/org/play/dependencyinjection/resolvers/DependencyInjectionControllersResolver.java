package org.play.dependencyinjection.resolvers;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.Executors;

import org.play.dependencyinjection.DependencyInjectionPool;
import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Class that manages the dependency injection inside the {@link Controller} objects.
 */
public class DependencyInjectionControllersResolver {
	
	private static final DependencyInjectionControllersResolver instance = new DependencyInjectionControllersResolver();


	/**
	 * Singleton pattern
	 */
	public static DependencyInjectionControllersResolver instance() {
		return instance;
	}


	/**
	 *    Initializes the dependency injections of the properties inside the {@link Controller}s
	 * annotated with {@link WithDependencyInjection} interface.
	 * 
	 * @param controllersPackage
	 *    Package name that stores the implementation of {@link Controller}s
	 * @param parentControllerClazz
	 *    Class to which all controllers must belong   
	 * 
	 * @throws DependencyInjectionException
	 */
	public <T> void init (final String controllersPackage, final Class<T> parentControllerClazz) throws DependencyInjectionException {

		if (controllersPackage == null || parentControllerClazz == null)
			throw new DependencyInjectionException ((controllersPackage    == null ? "The given controllersPackage must not be null. "    : "")
                                                  + (parentControllerClazz == null ? "The given parentControllerClazz must not be null. " : ""));
		// Get implementation of controllers
		Set<Class<? extends T>> controllers = new Reflections (new ConfigurationBuilder()
                                                                  .filterInputsBy (new FilterBuilder().includePackage (controllersPackage))
                                                                  .setUrls (ClasspathHelper.forPackage (controllersPackage))
                                                                  .setScanners (new SubTypesScanner()
                                                                               ,new TypeAnnotationsScanner())
                                                                  .setExecutorService (Executors.newFixedThreadPool (2)))
		                                                     .getSubTypesOf (parentControllerClazz);
		if (controllers != null) {

			for (Class<?> controllerClass : controllers) {

				try {
					Class<?> concreteClass = Class.forName (controllerClass.toString().replace("class ", ""));

					// Resolves dependency injection of the properties inside the current controller
					resolveDependenciesOfClass (concreteClass);

				} catch (Exception e) {
					throw new DependencyInjectionException (e);
				}
			}
		}
	}


	/**
	 *    Resolves the dependencies within the class clazz (the properties
	 * with the {@link WithDependencyInjection} annotation).
	 * 
	 * @param controllerClazz
	 *    Class of current controller
	 *    
	 * @throws DependencyInjectionException
	 */
	@SuppressWarnings("unchecked")
	private void resolveDependenciesOfClass (Class<?> controllerClazz) throws DependencyInjectionException {

		// Get all controller properties with WithDependencyInjection annotation
		Set<Field> controllerProperties = ReflectionUtils.getAllFields (controllerClazz,
				                                                        ReflectionUtils.withAnnotation (WithDependencyInjection.class));
		if (controllerProperties != null) {

			for (Field field : controllerProperties) {

				// Due to is a private property
				field.setAccessible (true);
				try {
					// Searching inside the resolvers
					Object controllerPropertyImplementation = findInResolvers (field);
					if (controllerPropertyImplementation != null)
						field.set (controllerClazz, controllerPropertyImplementation);
					else
						throw new DependencyInjectionException ("The property: " + field.getName() + " in the class: "
								                              + controllerClazz.getCanonicalName() 
								                              + "  has not an implementation");							
				} catch (Exception e) {
					throw new DependencyInjectionException (e);
				}
				finally {
					field.setAccessible (false);
				}
			}
		}
	}


	/**
	 *   Searches in {@link DependencyInjectionPool} the implementation of a given property
	 * annotated with {@link WithDependencyInjection} interface.
	 * 
	 * @param field
	 *    Property for which we need to find its implementation
	 * 
	 * @return implementation of the given property
	 * 
	 * @throws DependencyInjectionException
	 */
	private Object findInResolvers (Field field) throws DependencyInjectionException {

		for (DependencyInjectionResolver additionalResolver : DependencyInjectionPool.instance()
				                                                                     .getResolversLessGivenInterfacePackage (null)) {
			try {
				Object implementation = additionalResolver.getImplementation ((Class<?>) field.getType());
				if (implementation != null)
					return implementation;

			} catch (DependencyInjectionException e) {}
		}
		return null;
	}

}