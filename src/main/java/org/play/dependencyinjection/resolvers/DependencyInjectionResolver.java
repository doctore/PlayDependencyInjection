package org.play.dependencyinjection.resolvers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import org.play.dependencyinjection.DependencyInjectionPool;
import org.play.dependencyinjection.annotations.DependencyInjectionQualifier;
import org.play.dependencyinjection.annotations.Injectable;
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
 * Main class that manages the dependency injection.
 */
public class DependencyInjectionResolver {

	/**
	 * Use to separate the parts of a "composed string"
	 */
	private static String separator = "-";

	/**
	 * Stores the equivalence between interfaces and implementations
	 */
	private Map<String, Object> interfaceImplementationEquivalence = new HashMap<String, Object>();

	/**
	 * Package name that stores the "injectable interfaces"
	 */
	private String interfacesPackage;


	/**
	 * Initializes the equivalence between "interfaces" and "implementations".
	 * 
	 * <h1><strong>IMPORTANT: Use only for testing purpose.</strong></h1></br>
	 * 
	 * @param interfacesPackage
	 *    Package name that stores the "injectable interfaces"
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionResolver (final String interfacesPackage) throws DependencyInjectionException {

		if (interfacesPackage == null)
			throw new DependencyInjectionException ("The given interfacesPackage must not be null");

		this.interfacesPackage = interfacesPackage;
	}


	/**
	 * Initializes the equivalence between "interfaces" and "implementations".
	 * 
	 * @param interfacesPackage
	 *    Package name that stores the "injectable interfaces"
	 * @param implementationPackage
	 *    Package name that stores the implementation of "injectable interfaces"
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionResolver (final String interfacesPackage, final String implementationPackage) throws DependencyInjectionException {

		this (interfacesPackage, implementationPackage, null);
	}


	/**
	 * Initializes the equivalence between "interfaces" and "implementations".
	 * 
	 * @param interfacesPackage
	 *    Package name that stores the "injectable interfaces"
	 * @param implementationPackage
	 *    Package name that stores the implementation of "injectable interfaces"
	 * @param interfaceToResolve
	 *    Interface that manages this resolver
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionResolver (final String interfacesPackage, final String implementationPackage,
                                        @Nullable final Class<?> interfaceToResolve) throws DependencyInjectionException {

		String errorMessage = (interfacesPackage == null || interfacesPackage.trim().isEmpty() 
                                  ? "The given resolverIdentifier must not be null or empty. " : "");

		errorMessage       += (implementationPackage == null || implementationPackage.trim().isEmpty() 
                                  ? "The given implementationPackage must not be null or empty. " : "");

		if (!errorMessage.isEmpty())
			throw new DependencyInjectionException (errorMessage);	

		this.interfacesPackage = interfacesPackage;

		// Gets classes of interfaces with Injectable annotation
		Set<Class<?>> interfaceClasses = getInterfaceClassesWithInjectableAnnotation (interfaceToResolve);
		if (interfaceClasses != null) {

			// Gets implementations
			Reflections implementationReflections = new Reflections (new ConfigurationBuilder()
                                                                        .filterInputsBy (new FilterBuilder().includePackage (implementationPackage))
                                                                        .setUrls (ClasspathHelper.forPackage (implementationPackage))
                                                                        .setScanners (new SubTypesScanner()
                                                                                     ,new TypeAnnotationsScanner())
                                                                        .setExecutorService (Executors.newFixedThreadPool (2)));
			// Links the interface with its implementation
			for (Class<?> interfaceClazz : interfaceClasses) {

				Set<?> implementationClasses = implementationReflections.getSubTypesOf (interfaceClazz);
				if (implementationClasses == null || implementationClasses.isEmpty())
					throw new DependencyInjectionException ("The interface " + interfaceClazz.getCanonicalName()
                                                          + " has not an implementation");

				// Insert in the "equivalence Map": interface -> implementation
				for (Object implementationClass : implementationClasses) {

					try {
						Class<?> implementationClazz = Class.forName (implementationClass.toString().replace("class ", ""));

						String keyValue = buildKeyInInterfaceImplementationEquivalence (interfaceClazz
								                                                       ,getQualifierValueInDependencyInjectionQualifierAnnotation (
								                                                           implementationClazz));
						if (interfaceImplementationEquivalence.get (keyValue) != null)
							throw new DependencyInjectionException ("The interface " + interfaceClazz.getCanonicalName() 
                                                                  + " and 'key value' = " + keyValue + " has more than one "
                                                                  + "implementation");

						interfaceImplementationEquivalence.put (keyValue, implementationClazz.newInstance());

					} catch (Exception e) {
						throw new DependencyInjectionException (e);
					}
				}
			}
		}
	}


	/**
	 * Binds one interface with its implementation manually.
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param implementationClazz
	 *    Class of implementation
	 * 
	 * @return instance of {@link DependencyInjectionResolver}
	 * 
	 * @throws DependencyInjectionException
	 */
	public <T, E> DependencyInjectionResolver bind (final Class<T> interfaceClazz, final Class<E> implementationClazz)
			                                           throws DependencyInjectionException {

		if (interfaceClazz == null || implementationClazz == null)
			throw new DependencyInjectionException ((interfaceClazz       == null ? "The given interfaceClazz must not be null. "       : "")
                                                  + (implementationClazz == null ? "The given implementationClazz must not be null. " : ""));
		try {
			interfaceImplementationEquivalence.put (buildKeyInInterfaceImplementationEquivalence (interfaceClazz
					                                                                             ,getQualifierValueInDependencyInjectionQualifierAnnotation (
                                                                                                     implementationClazz))
					                               ,implementationClazz.newInstance());
		} catch (Exception e) {
			throw new DependencyInjectionException (e);
		}
		return this;
	}


	@Override
	public boolean equals (final Object obj) {

		if (this == obj)  return true;
		if (obj == null)  return false;
		if (getClass() != obj.getClass())  return false;

		DependencyInjectionResolver other = (DependencyInjectionResolver) obj;
		if (interfacesPackage == null) {
			if (other.interfacesPackage != null)
				return false;

		} else if (!interfacesPackage.equals (other.interfacesPackage))
			return false;

		return true;
	}


	/**
	 * Get a implementation from a interface.
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param qualifierValue
	 *    Value of {@link DependencyInjectionQualifier} in an implementation class
	 * 
	 * @return the implementation of the given interface
	 * 
	 * @throws DependencyInjectionException
	 */
	@SuppressWarnings({ "unchecked" })
	public <T> T getImplementation (final Class<T> interfaceClazz, @Nullable String qualifierValue) throws DependencyInjectionException {     

		if (interfaceClazz == null)
			throw new DependencyInjectionException ("The given interfaceClazz must not be null");

		T implementation = (T) interfaceImplementationEquivalence.get (
				                    buildKeyInInterfaceImplementationEquivalence (interfaceClazz, qualifierValue));
		if (implementation == null)
			throw new DependencyInjectionException ("The given interface name: " + interfaceClazz.getCanonicalName() 
					                              + " has not an implementation");
        return implementation;  
	}


	/**
	 * Gets package name that stores the "injectable interfaces"
	 * 
	 * @return package name of "injectable interfaces"
	 */
	public String getInterfacesPackage() {
		return interfacesPackage;
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((interfacesPackage == null) ? 0 : interfacesPackage.hashCode());
		return result;
	}


	/**
	 *    Resolves the dependencies for all interfaceImplementationEquivalence.values() (for every
	 * implementation loads the properties with the {@link WithDependencyInjection} annotation).
	 * 
	 * @throws DependencyInjectionException
	 */
	public void resolveAllClassPropertiesOfImplementations() throws DependencyInjectionException {

		if (interfaceImplementationEquivalence != null) {

			for (Object implementation : interfaceImplementationEquivalence.values())
				resolveDependenciesOfClass (implementation);
		}
	}


	/**
	 *    Resolves the dependencies within the class thats implements interfaceClazz
	 * (the properties with the {@link WithDependencyInjection} annotation).
	 * 
	 * <h1><strong>IMPORTANT: Use only for testing purpose.</strong></h1></br>
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param qualifierValue
	 *    Value of {@link DependencyInjectionQualifier} in an implementation class
	 *    
	 * @throws DependencyInjectionException
	 */
	public <T> void resolveDependenciesOfInterface (final Class<T> interfaceClazz, @Nullable String qualifierValue)
			                                           throws DependencyInjectionException {

		resolveDependenciesOfClass (getImplementation (interfaceClazz, qualifierValue));
	}


	/**
	 *    With a given name of interface and quality value (of an implementation class), returns
	 * the key value in {@link DependencyInjectionResolver#interfaceImplementationEquivalence} map.
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param qualifierValue
	 *    Value of {@link DependencyInjectionQualifier} in an implementation class
	 *
	 * @return {@link String} with the key value
	 */
	private String buildKeyInInterfaceImplementationEquivalence (Class<?> interfaceClazz, String qualifierValue) {

		String key = interfaceClazz.getCanonicalName();

		if (qualifierValue != null && !qualifierValue.trim().isEmpty())
			key += separator + qualifierValue;

		return key;
	}


	/**
	 *   Searches in the additional resolvers the implementation of a given property
	 * annotated with {@link WithDependencyInjection} interface.
	 * 
	 * @param field
	 *    Property for which we need to find its implementation
	 * 
	 * @return implementation of the given property
	 * 
	 * @throws DependencyInjectionException
	 */
	private Object findInAdditionalResolversTheImplementation (Field field) throws DependencyInjectionException {

		// Gets qualifier value (in WithDependencyInjection annotation) of current property
		String qualifierValue = null;
		WithDependencyInjection annotation = (WithDependencyInjection)field.getAnnotation (WithDependencyInjection.class);
		if (annotation != null)
			qualifierValue = annotation.qualifier();

		for (DependencyInjectionResolver additionalResolver : DependencyInjectionPool.instance()
				                                                                     .getResolversLessGivenInterfacePackage (interfacesPackage)) {
			try {
				Object implementation = additionalResolver.getImplementation ((Class<?>) field.getType(), qualifierValue);
				if (implementation != null)
					return implementation;

			} catch (DependencyInjectionException e) {}
		}
		return null;
	}


	/**
	 * Returns classes of interfaces with {@link Injectable} annotation
	 * 
	 * @param interfaceToResolve
	 *    Interface that manages this resolver
	 * 
	 * @return {@link Set} of classes with {@link Injectable} annotation
	 */
	private Set<Class<?>> getInterfaceClassesWithInjectableAnnotation (@Nullable final Class<?> interfaceToResolve) {

		// Get "injectable interfaces"
		Reflections interfaceReflections = new Reflections (new ConfigurationBuilder()
                                                               .filterInputsBy (new FilterBuilder().includePackage (interfacesPackage))
                                                               .setUrls (ClasspathHelper.forPackage (interfacesPackage))
                                                               .setScanners (new SubTypesScanner()
                                                                            ,new TypeAnnotationsScanner())
                                                               .setExecutorService (Executors.newFixedThreadPool (2)));

		Set<Class<?>> interfaceClasses = interfaceReflections.getTypesAnnotatedWith (Injectable.class);

		// Filter by the interface indicated as parameter 
		if (interfaceToResolve != null) {

			boolean flagContainsInterfaceToResolve = false;
			if (interfaceClasses.contains (interfaceToResolve))
				flagContainsInterfaceToResolve = true;

			interfaceClasses.retainAll (interfaceReflections.getSubTypesOf (interfaceToResolve));
			if (flagContainsInterfaceToResolve)
				interfaceClasses.add (interfaceToResolve);
		}
		return interfaceClasses;
	}


	/**
	 *    Returns the value of qualifier of {@link DependencyInjectionQualifier} annotation
	 * of a class.
	 * 
	 * @param implementationClazz
	 *    Class that implements an {@link Injectable} interface
	 * 
	 * @return value of qualifier function
	 */
	private String getQualifierValueInDependencyInjectionQualifierAnnotation (Class<?> implementationClazz) {

		String qualifierValue = null;
		DependencyInjectionQualifier annotation = (DependencyInjectionQualifier)implementationClazz.getAnnotation (DependencyInjectionQualifier.class);
		if (annotation != null)
			qualifierValue = annotation.value();

		return qualifierValue;
	}


	/**
	 *    Returns the value of qualifier of {@link WithDependencyInjection} annotation
	 * of a property.
	 * 
	 * @param field
	 *    Property of a class that implements an {@link Injectable} interface
	 * 
	 * @return value of qualifier function
	 */
	private String getQualifierValueInWithDependencyInjectionAnnotation (Field field) {

		String qualifierValue = null;
		WithDependencyInjection annotation = (WithDependencyInjection)field.getAnnotation (WithDependencyInjection.class);
		if (annotation != null)
			qualifierValue = annotation.qualifier();

		return qualifierValue;
	}


	/**
	 *    Resolves the dependencies within the class clazz (the properties
	 * with the {@link WithDependencyInjection} annotation).
	 * 
	 * @param clazz
	 *    Class of "implementation"
	 *    
	 * @throws DependencyInjectionException
	 */
	@SuppressWarnings({ "unchecked" })
	private void resolveDependenciesOfClass (Object clazz) throws DependencyInjectionException {

		// Get all clazz properties with WithDependencyInjection annotation
		Set<Field> clazzProperties = ReflectionUtils.getAllFields (clazz.getClass(),
				                                                   ReflectionUtils.withAnnotation (WithDependencyInjection.class));
		if (clazzProperties != null) {

			for (Field field : clazzProperties) {

				// Due to is a private property
				field.setAccessible (true);
				try {
					// Searching inside "current resolver"
					Object clazzPropertyImplementation = interfaceImplementationEquivalence.get (
							                                buildKeyInInterfaceImplementationEquivalence (field.getType()
								                                                                         ,getQualifierValueInWithDependencyInjectionAnnotation (
								                                                                             field)));
					if (clazzPropertyImplementation != null)
						field.set (clazz, clazzPropertyImplementation);

					// Searching inside other dependency injection resolvers
					else {
						Object otherClazzPropertyImplementation = findInAdditionalResolversTheImplementation (field);
						if (otherClazzPropertyImplementation != null)
							field.set (clazz, otherClazzPropertyImplementation);
						else
							throw new DependencyInjectionException ("The property: " + field.getName() + " in the class: "
									                              + clazz.getClass().getCanonicalName() 
									                              + "  has not an implementation");
					}
				} catch (Exception e) {
					throw new DependencyInjectionException (e);
				}
				finally {
					field.setAccessible (false);
				}
			}
		}
	}

}