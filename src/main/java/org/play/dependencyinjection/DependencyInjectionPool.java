package org.play.dependencyinjection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;
import org.play.dependencyinjection.resolvers.DependencyInjectionControllersResolver;
import org.play.dependencyinjection.resolvers.DependencyInjectionResolver;

/**
 * Pool that manages all {@link DependencyInjectionResolver}s
 */
public class DependencyInjectionPool {

	private static final DependencyInjectionPool instance = new DependencyInjectionPool();

	/**
	 * Stores the equivalence between interfaces and {@link DependencyInjectionResolver}
	 */
	private Map<String, DependencyInjectionResolver> resolversPool = new HashMap<String, DependencyInjectionResolver>();


	/**
	 * Singleton pattern
	 */
	public static DependencyInjectionPool instance() {
		return instance;
	}


	/**
	 * Adds a new {@link DependencyInjectionResolver} instance to the pool.
	 * 
	 * @param dependencyInjectionResolver
	 *    {@link DependencyInjectionResolver} instance to add.
	 * 
	 * @return instance of {@link DependencyInjectionPool}
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionPool addNewResolver (final DependencyInjectionResolver dependencyInjectionResolver) throws DependencyInjectionException {

		if (dependencyInjectionResolver == null)
			throw new DependencyInjectionException ("The given dependencyInjectionResolver must not be null");

		if (dependencyInjectionResolver.getInterfacesPackage() == null)
			throw new DependencyInjectionException ("The given dependencyInjectionResolver does not have an interface package to manage");

		instance.resolversPool.put (dependencyInjectionResolver.getInterfacesPackage(), dependencyInjectionResolver);

		return instance;
	}


	/**
	 * Initializes the {@link Controller} properties annotated with {@link WithDependencyInjection} interface. 
	 * 
	 * @param controllersPackage
	 *    Package name that stores the implementation of {@link Controller}s
	 * @param parentControllerClazz
	 *    Class to which all controllers must belong
	 *     
	 * @throws DependencyInjectionException
	 */
	public <T> void initializeControllersResolver (final String controllersPackage, final Class<T> parentControllerClazz)
			                                          throws DependencyInjectionException {

		if (controllersPackage == null || parentControllerClazz == null)
			throw new DependencyInjectionException ((controllersPackage    == null ? "The given controllersPackage must not be null. "    : "")
                                                  + (parentControllerClazz == null ? "The given parentControllerClazz must not be null. " : ""));

		// Resolves dependencies of the "implementations" inside resolvers
		Set<DependencyInjectionResolver> resolvers = new HashSet<DependencyInjectionResolver>(resolversPool.values());
		for (DependencyInjectionResolver resolver : resolvers)
			resolver.resolveAllClassPropertiesOfImplementations();

		DependencyInjectionControllersResolver.instance().init (controllersPackage, parentControllerClazz);
	}


	/**
	 * Gets the {@link DependencyInjectionResolver} of a specific interface package.
	 * 
	 * @param interfacesPackage
	 *    Package name that stores the "injectable interfaces"
	 *    
	 * @return {@link DependencyInjectionResolver}
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionResolver getResolver (final String interfacesPackage) throws DependencyInjectionException {

		if (interfacesPackage == null)
			throw new DependencyInjectionException ("The given interfacesPackage must not be null");

		DependencyInjectionResolver resolver = instance.resolversPool.get (interfacesPackage);
		if (resolver == null)
			throw new DependencyInjectionException ("The given interface name: " + interfacesPackage
					                              + " has not a resolver that manages the dependency injection");
        return resolver;  
	}


	/**
	 *    Gets all {@link DependencyInjectionResolver}s less the resolver related to the given
	 * interface package. If the interface is null returns all resolvers in the pool.  
	 * 
	 * @param interfacesPackage
	 *    Package name that stores the "injectable interfaces"
	 * 
	 * @return {@link Set} of {@link DependencyInjectionResolver}
	 * 
	 * @throws DependencyInjectionException
	 */
	public Set<DependencyInjectionResolver> getResolversLessGivenInterfacePackage (final String interfacesPackage)
			                                                                          throws DependencyInjectionException {

		Set<DependencyInjectionResolver> result = new HashSet<DependencyInjectionResolver>();
		if (interfacesPackage == null)
			result.addAll (instance.resolversPool.values());
		else {
			for (Map.Entry<String, DependencyInjectionResolver> entry : instance.resolversPool.entrySet()) {

				if (!entry.getKey().equals (interfacesPackage))
					result.add (entry.getValue());
			}
		}
		return result;
	}

}