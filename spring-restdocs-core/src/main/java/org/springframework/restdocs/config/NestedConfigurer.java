/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.config;

/**
 * A configurer that is nested and, therefore, has a parent.
 *
 * @param <PARENT> the parent's type
 * @author Andy Wilkinson
 * @since 1.1.0
 */
interface NestedConfigurer<PARENT> {

	/**
	 * Returns the configurer's parent.
	 * @return the parent
	 */
	PARENT and();

}
