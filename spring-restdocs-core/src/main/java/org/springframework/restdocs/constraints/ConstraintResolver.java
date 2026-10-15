/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.constraints;

import java.util.List;

/**
 * An abstraction for resolving a class's constraints.
 *
 * @author Andy Wilkinson
 */
public interface ConstraintResolver {

	/**
	 * Resolves and returns the constraints for the given {@code property} on the given
	 * {@code clazz}. If there are no constraints, an empty list is returned.
	 * @param property the property
	 * @param clazz the class
	 * @return the list of constraints, never {@code null}
	 */
	List<Constraint> resolveForProperty(String property, Class<?> clazz);

}
