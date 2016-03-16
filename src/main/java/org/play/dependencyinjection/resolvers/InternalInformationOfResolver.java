package org.play.dependencyinjection.resolvers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.play.dependencyinjection.annotations.DependencyInjectionQualifier;
import org.play.dependencyinjection.annotations.Injectable;
import org.play.dependencyinjection.annotations.WithDependencyInjection;
import org.play.dependencyinjection.exceptions.DependencyInjectionException;

/**
 * Stores the necessary information that {@link DependencyInjectionResolver} needs to manage
 */
public class InternalInformationOfResolver {

	/**
	 * Package name of the "injectable interfaces" manages with the current {@link DependencyInjectionResolver}
	 */
	private String interfacesPackage;

	/**
	 *    Stores the necessary information about the interfaces-implementations
	 * manages by the "current" {@link DependencyInjectionResolver}   
	 */
	private Map<String, Object> interfaceImplementationEquivalence;

	/**
	 * Use to separate the parts of a "composed string"
	 */
	private static String separator = "-";


	public InternalInformationOfResolver (String interfacesPackage) {

		this.interfacesPackage                  = interfacesPackage;
		this.interfaceImplementationEquivalence = new HashMap<String, Object>(64); 
	}


	/**
	 * Returns the package name of the "injectable interfaces" managed by the current {@link DependencyInjectionResolver}
	 * 
	 * @return {@link String} with the package name of the "injectable interfaces"
	 */
	public String getInterfacesPackage() {
		return interfacesPackage;
	}


	@Override
	public int hashCode() {
		return (interfacesPackage == null ? 0 : interfacesPackage.hashCode());
	}


	@Override
	public boolean equals (Object obj) {

		if (this == obj)  return true;
        if (obj == null || getClass() != obj.getClass())  return false;

        InternalInformationOfResolver other = (InternalInformationOfResolver) obj;
        return !(interfacesPackage != null ? !interfacesPackage.equals (other.interfacesPackage) : other.interfacesPackage != null);
	}	


	/**
	 *    Stores the necessary information for the given relation between the interfaceClazz and implementationClazz
	 * when the last one was defined with a {@link Scope} = {@link ScopeValuesEnum#PROTOTYPE}.
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param implementationClazz
	 *    Class of implementation of the given interface
	 * @param overwriteImplementation
	 *    If false and exists other implementation for the given interface a DependencyInjectionException will be throw.
	 *    If true, always overwrite the existing implementation of the given interface with the given implementation.
	 * @param preinitializedObject
	 *    Object that the user has initialized for the given implementationClazz (for example, because he/she does not want
	 * to use the default constructor)
	 * 
	 * @throws DependencyInjectionException
	 */
	public void addInformationOfElementToInject (Class<?> interfaceClazz, Class<?> implementationClazz, boolean overwriteImplementation
			                                    ,@Nullable final Object preinitializedObject) throws DependencyInjectionException {

		String errorMessage = (interfaceClazz == null ? "The given interfaceClazz must not be null. " : "");
		errorMessage += (implementationClazz == null ? "The given implementationClazz must not be null. " : "");

		if (!errorMessage.isEmpty())
			throw new DependencyInjectionException (errorMessage);

		// Generates "the key" related with the given interface (canonical name + qualifier value)  
		String interfaceKey = buildKeyInInterfaceImplementationEquivalence (interfaceClazz
				                                                           ,getQualifierValueInDependencyInjectionQualifierAnnotation (
				                                                               implementationClazz));

		if (this.interfaceImplementationEquivalence.containsKey (interfaceKey) && !overwriteImplementation)
			throw new DependencyInjectionException ("The interface " + interfaceClazz.getCanonicalName() + " and 'key value' = " + interfaceKey
                                                  + " has more than one implementation");

		// Creates an unique instance of the given class
		Object singletonObject = null;
		try {
			singletonObject = (preinitializedObject != null ? preinitializedObject : implementationClazz.newInstance());
		} catch (Exception e) {
			throw new DependencyInjectionException (e);
		}
		this.interfaceImplementationEquivalence.put (interfaceKey, singletonObject);
	}


	/**
	 * Gets the implementation of the given interface (and qualifier value)
	 * 
	 * @param interfaceClazz
	 *    Class of interface
	 * @param qualifierValue
	 *    Value of {@link DependencyInjectionQualifier} in an implementation class
	 * 
	 * @return the implementation of the given interface (or null if the current {@link DependencyInjectionResolver}
	 *         does not manage the given class of an interface)
	 * 
	 * @throws DependencyInjectionException
	 */
	@SuppressWarnings("unchecked")
	public <T> T getImplementation (final Class<T> interfaceClazz, @Nullable String qualifierValue) throws DependencyInjectionException {

		if (interfaceClazz == null)
			throw new DependencyInjectionException ("The given interfaceClazz must not be null");

		// Generates "the key" related with the given field (canonical name + qualifier value)
		String interfaceKey = buildKeyInInterfaceImplementationEquivalence (interfaceClazz, qualifierValue);

		return (T) this.interfaceImplementationEquivalence.get (interfaceKey);
	}


	/**
	 * Returns all implementations stored in {@link InternalInformationOfResolver#interfaceImplementationEquivalence}
	 * 
	 * @return {@link Collection} of {@link Object}
	 */
	public Collection<Object> getImplementations() {

		return this.interfaceImplementationEquivalence.values();
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

		return getImplementation (field.getType(), getQualifierValueInWithDependencyInjectionAnnotation (field));
	}


	/**
	 *    Returns the value of qualifier of {@link WithDependencyInjection} annotation
	 * of a property.
	 * 
	 * @param field
	 *    Property of a class that implements an {@link Injectable} interface
	 * 
	 * @return value of qualifier function
	 * 
	 * @throws DependencyInjectionException 
	 */
	public String getQualifierValueInWithDependencyInjectionAnnotation (Field field) throws DependencyInjectionException {

		WithDependencyInjection annotation = (WithDependencyInjection)field.getAnnotation (WithDependencyInjection.class);
		if (annotation == null)
			throw new DependencyInjectionException ("The field: " + field.getName() + " belonging to the class: " + field.getType().getCanonicalName() 
					                              + " does not contain the annotation WithDependencyInjection");
		return annotation.value();
	}


	/**
	 * Deletes the information contained in the current object.
	 * 
	 * <h1><strong>IMPORTANT: Use only when we will stop the application</strong></h1></br>
	 */
	public void destroyResources() {
		
		this.interfaceImplementationEquivalence.clear();
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
	 *    Returns the value of qualifier of {@link DependencyInjectionQualifier} annotation
	 * of a class.
	 * 
	 * @param implementationClazz
	 *    Class that implements an {@link Injectable} interface
	 * 
	 * @return value of qualifier function
	 * 
	 * @throws DependencyInjectionException 
	 */
	private String getQualifierValueInDependencyInjectionQualifierAnnotation (Class<?> implementationClazz) throws DependencyInjectionException {

		DependencyInjectionQualifier annotation = (DependencyInjectionQualifier)implementationClazz.getAnnotation (DependencyInjectionQualifier.class);
		return (annotation == null ? null : annotation.value());
	}	

}