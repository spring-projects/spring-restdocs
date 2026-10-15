/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.constraints;

/**
 * Resolves a description for a {@link Constraint}.
 *
 * @author Andy Wilkinson
 */
public interface ConstraintDescriptionResolver {

	/**
	 * Resolves the description for the given {@code constraint}.
	 * @param constraint the constraint
	 * @return the description
	 */
	String resolveDescription(Constraint constraint);

}
