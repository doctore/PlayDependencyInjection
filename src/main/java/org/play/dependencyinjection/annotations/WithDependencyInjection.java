package org.play.dependencyinjection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies which properties they should inject dependency
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface WithDependencyInjection {}