package org.play.dependencyinjection.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies those interfaces to be implemented by a class (simulating dependency injection)
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DependencyInjectionQualifier {

	String value();
}