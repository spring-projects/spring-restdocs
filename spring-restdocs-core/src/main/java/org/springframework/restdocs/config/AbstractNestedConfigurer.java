/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.config;

/**
 * Base class for {@link NestedConfigurer} implementations.
 *
 * @param <PARENT> the type of the configurer's parent
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public abstract class AbstractNestedConfigurer<PARENT> extends AbstractConfigurer implements NestedConfigurer<PARENT> {

	private final PARENT parent;

	/**
	 * Creates a new {@code AbstractNestedConfigurer} with the given {@code parent}.
	 * @param parent the parent
	 */
	protected AbstractNestedConfigurer(PARENT parent) {
		this.parent = parent;
	}

	@Override
	public final PARENT and() {
		return this.parent;
	}

}
