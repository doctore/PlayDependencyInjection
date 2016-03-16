package org.play.dependencyinjection.resolvers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
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
	 * In the operations related with the searching of information about interfaces and implementations, the number of threads 
	 */
	private static int numberOfParallelThreads = 2;
	
	/**
	 * Used to store the necessary information that the current resolver needs to manage 
	 */
	private InternalInformationOfResolver internalInformationOfResolver;


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

		this.internalInformationOfResolver = new InternalInformationOfResolver (interfacesPackage);
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

		this (interfacesPackage, implementationPackage, null, null);
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

		this (interfacesPackage, implementationPackage, interfaceToResolve, null);
	}
	
	
	/**
	 * Initializes the equivalence between "interfaces" and "implementations".
	 * 
	 * @param interfacesPackage
	 *    Package name that stores the "injectable interfaces"
	 * @param implementationPackage
	 *    Package name that stores the implementation of "injectable interfaces"
	 * @param preInitializedObjects
	 *    {@link List} of {@link Object}s preinitialized by the user (for example,
	 *    because he/she does not want to use the default constructor)
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionResolver (final String interfacesPackage, final String implementationPackage,
                                        @Nullable final List<Object> preInitializedObjects) throws DependencyInjectionException {

		this (interfacesPackage, implementationPackage, null, preInitializedObjects);
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
	 * @param preInitializedObjects
	 *    {@link List} of {@link Object}s preinitialized by the user (for example,
	 *    because he/she does not want to use the default constructor)
	 * 
	 * @throws DependencyInjectionException
	 */
	public DependencyInjectionResolver (final String interfacesPackage, final String implementationPackage,
                                        @Nullable final Class<?> interfaceToResolve,
                                        @Nullable final List<Object> preInitializedObjects) throws DependencyInjectionException {

		String errorMessage = (interfacesPackage == null || interfacesPackage.trim().isEmpty() 
                                  ? "The given interfacesPackage must not be null or empty. " : "");

		errorMessage += (implementationPackage == null || implementationPackage.trim().isEmpty() 
                             ? "The given implementationPackage must not be null or empty. " : "");

		if (!errorMessage.isEmpty())
			throw new DependencyInjectionException (errorMessage);	
		
		this.internalInformationOfResolver = new InternalInformationOfResolver (interfacesPackage);
		
		// Executes the first step of dependency injection process: creates the relation between interface - implementation  
		buildDependencyInjectionOfInterfacesAndImplementations (implementationPackage, interfaceToResolve, preInitializedObjects);	
	}


	@Override
	public int hashCode() {		
		return (internalInformationOfResolver == null ? 0 : internalInformationOfResolver.hashCode());
	}


	@Override
	public boolean equals (Object obj) {
		
		if (this == obj)  return true;
        if (obj == null || getClass() != obj.getClass())  return false;

        DependencyInjectionResolver other = (DependencyInjectionResolver) obj;
        return !(internalInformationOfResolver != null ? !internalInformationOfResolver.equals (other.internalInformationOfResolver) 
        		                                       : other.internalInformationOfResolver != null);
	}


	/**
	 * Binds one interface with its implementation manually.
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param implementationClazz
	 *    Class of implementation
	 * @param preinitializedObject
	 *    Object that the user has initialized for the given implementationClazz (for example, because he/she does not want
	 * to use the default constructor)  
	 * 
	 * @return instance of {@link DependencyInjectionResolver}
	 * 
	 * @throws DependencyInjectionException
	 */
	public <T, E> DependencyInjectionResolver bind (final Class<T> interfaceClazz, final Class<E> implementationClazz
			                                       ,@Nullable final Object preinitializedObject) throws DependencyInjectionException {

		if (interfaceClazz == null || implementationClazz == null)
			throw new DependencyInjectionException ((interfaceClazz       == null ? "The given interfaceClazz must not be null. " : "")
                                                  + (implementationClazz == null ? "The given implementationClazz must not be null. " : ""));

		internalBind (interfaceClazz, implementationClazz, true, preinitializedObject);
		return this;
	}


	/**
	 * Gets the implementation of the given interface (and qualifier value)
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
	public <T> T getImplementation (final Class<T> interfaceClazz, @Nullable String qualifierValue) throws DependencyInjectionException {     

		return this.internalInformationOfResolver.getImplementation (interfaceClazz, qualifierValue);
	}


	/**
	 *    Returns the valid value (implementation) of the given {@link Field} if the current {@link DependencyInjectionResolver}
	 * manages the relation interface-implementation of the field.
	 * 
	 * @param field
	 *    {@link Field} whose value needs to be manage using the dependency injection functionality
	 * 
	 * @return the value of the given field (or null if the current {@link DependencyInjectionResolver}
	 *         does not manage the given field)
	 * 
	 * @throws DependencyInjectionException 
	 */
	public Object getImplementationOfField (Field field) throws DependencyInjectionException {

		if (field == null)
			throw new DependencyInjectionException ("The given field must not be null");

		return this.internalInformationOfResolver.getImplementationOfField (field);
	}


	/**
	 * Returns the package name of the "injectable interfaces" managed by the current {@link DependencyInjectionResolver}
	 * 
	 * @return {@link String} with the package name of the "injectable interfaces"
	 */
	public String getInterfacesPackage() {
		return this.internalInformationOfResolver.getInterfacesPackage();
	}


	/**
	 * Deletes the information contained in the current object.
	 * 
	 * <h1><strong>IMPORTANT: Use only when we will stop the application</strong></h1></br>
	 */
	public void destroyResources() {

		this.internalInformationOfResolver.destroyResources();
	}


	/**
	 *    Resolves the dependencies for all interfaceImplementationEquivalence.values() (for every
	 * implementation loads the properties with the {@link WithDependencyInjection} annotation).
	 * 
	 * @throws DependencyInjectionException
	 */
	public void resolveAllClassPropertiesOfImplementations() throws DependencyInjectionException {

		if (this.internalInformationOfResolver != null) {

			for (Object implementation : this.internalInformationOfResolver.getImplementations())
				resolvePropertiesOfImplementation (implementation);
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

		resolvePropertiesOfImplementation (getImplementation (interfaceClazz, qualifierValue));
	}


	/**
	 *    Resolves the dependency injections between interfaces and its implementation, that is, searches all
	 * interfaces with the annotation {@link Injectable} and tries to find an implementation of those interfaces.     
	 * 
	 * @param implementationPackage
	 *    Package name that stores the implementation of "injectable interfaces"
	 * @param interfaceToResolve
	 *    Interface that manages this resolver
	 * @param preInitializedObjects
	 *    {@link List} of {@link Object}s preinitialized by the user (for example,
	 *    because he/she does not want to use the default constructor)
	 *    
	 * @throws DependencyInjectionException   
	 */
	private void buildDependencyInjectionOfInterfacesAndImplementations (final String implementationPackage, @Nullable final Class<?> interfaceToResolve
			                                                            ,@Nullable final List<Object> preInitializedObjects) throws DependencyInjectionException {
			
		// Gets classes of interfaces with Injectable annotation
		Set<Class<?>> interfaceClasses = getInterfaceClassesWithInjectableAnnotation (interfaceToResolve);
		if (interfaceClasses != null) {

			// Gets implementations
			Reflections implementationReflections = new Reflections (new ConfigurationBuilder()
                                                                        .filterInputsBy (new FilterBuilder().includePackage (implementationPackage))
                                                                        .setUrls (ClasspathHelper.forPackage (implementationPackage))
                                                                        .setScanners (new SubTypesScanner()
                                                                                     ,new TypeAnnotationsScanner())
                                                                        .setExecutorService (Executors.newFixedThreadPool (numberOfParallelThreads)));
			// Gets a map of preinitialized objects easy to use
			Map<String, Object> preInitializedObjectsMap = structurePreInitializeObjects (preInitializedObjects);

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

						// Stores the relation between interfaceClazz and implementationClazz
						internalBind (interfaceClazz, implementationClazz, false, preInitializedObjectsMap.get (implementationClazz.getCanonicalName()));

					} catch (Exception e) {
						throw new DependencyInjectionException (e);
					}
				}
			}
		}
	}


	/**
	 *   Searches in the additional resolvers the implementation of a given property annotated
	 * with {@link WithDependencyInjection}.
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
		String qualifierValue = this.internalInformationOfResolver.getQualifierValueInWithDependencyInjectionAnnotation (field);

		for (DependencyInjectionResolver additionalResolver : DependencyInjectionPool.instance()
				                                                                     .getResolversLessGivenInterfacePackage (getInterfacesPackage())) {
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
                                                               .filterInputsBy (new FilterBuilder().includePackage (this.internalInformationOfResolver.getInterfacesPackage()))
                                                               .setUrls (ClasspathHelper.forPackage (this.internalInformationOfResolver.getInterfacesPackage()))
                                                               .setScanners (new SubTypesScanner()
                                                                            ,new TypeAnnotationsScanner())
                                                               .setExecutorService (Executors.newFixedThreadPool (numberOfParallelThreads)));

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
	 * Binds one interface with its implementation.
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param implementationClazz
	 *    Class of implementation
	 * @param overwriteImplementation
	 *    If false and exists other implementation for the given interface a DependencyInjectionException will be throw.
	 *    If true, always overwrite the existing implementation of the given interface with the given implementation.
	 * @param preinitializedObject
	 *    Object that the user has initialized for the given implementationClazz (for example, because he/she does not want
	 * to use the default constructor)
	 * 
	 * @throws DependencyInjectionException
	 */
	private void internalBind (final Class<?> interfaceClazz, final Class<?> implementationClazz, boolean overwriteImplementation
			                  ,@Nullable final Object preinitializedObject) throws DependencyInjectionException {
		try {
			this.internalInformationOfResolver.addInformationOfElementToInject (interfaceClazz, implementationClazz, overwriteImplementation
					                                                           ,preinitializedObject);
		} catch (Exception e) {
			throw new DependencyInjectionException (e);
		}
	}


	/**
	 * Resolves the properties within the given object (the properties with the {@link WithDependencyInjection} annotation)
	 * 
	 * @param implementationObject
	 *    Object whose properties need to be resolved using dependency injection 
	 *    
	 * @throws DependencyInjectionException
	 */
	@SuppressWarnings({ "unchecked" })
	private void resolvePropertiesOfImplementation (Object implementationObject) throws DependencyInjectionException {

		// Get all clazz properties with WithDependencyInjection annotation
		Set<Field> clazzProperties = ReflectionUtils.getAllFields (implementationObject.getClass(),
				                                                   ReflectionUtils.withAnnotation (WithDependencyInjection.class));
		if (clazzProperties != null) {

			for (Field field : clazzProperties) {

				// Due to is a private property
				field.setAccessible (true);
				try {
					// Searching inside "current resolver"
					Object clazzPropertyImplementation = this.internalInformationOfResolver.getImplementationOfField (field);
					if (clazzPropertyImplementation != null)
						field.set (implementationObject, clazzPropertyImplementation);

					// Searching inside other dependency injection resolvers
					else {
						Object otherClazzPropertyImplementation = findInAdditionalResolversTheImplementation (field);
						if (otherClazzPropertyImplementation != null)
							field.set (implementationObject, otherClazzPropertyImplementation);
						else
							throw new DependencyInjectionException ("The property: " + field.getName() + " in the class: "
									                              + implementationObject.getClass().getCanonicalName() 
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


	/**
	 *    Uses the given {@link List} of preinitialized objects and generates a {@link Map} 
	 * with the following content:
	 * 
	 *  - Key: canonical name of class of the preinitialized object.
	 *  - Value: preinitialized object.
	 *  
	 * @param preInitializedObjects
	 *    {@link List} of {@link Object}s preinitialized by the user
	 *    
	 * @throws DependencyInjectionException
	 */
	private Map<String, Object> structurePreInitializeObjects (List<Object> preInitializedObjects) throws DependencyInjectionException {

		Map<String, Object> preInitializedObjectsMap = new HashMap<String, Object> (preInitializedObjects == null ? 0 : preInitializedObjects.size());
		if (preInitializedObjects != null) {

			for (Object preInitializedObject : preInitializedObjects) {

				String key = preInitializedObject.getClass().getCanonicalName();
				if (preInitializedObjectsMap.containsKey (key))
					throw new DependencyInjectionException ("In the preinitialized list of objects, the class: " + key 
							                              + " appears more than once");

				preInitializedObjectsMap.put (key, preInitializedObject);
			}
		}
		return preInitializedObjectsMap;
	}

}