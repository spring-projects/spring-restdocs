/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

/**
 * A strategy for determining whether or not a header should be excluded.
 *
 * @author Andy Wilkinson
 */
interface HeaderFilter {

	/**
	 * Called to determine whether a header should be excluded. Return {@code true} to
	 * exclude a header, otherwise {@code false}.
	 * @param name the name of the header
	 * @return {@code true} to exclude the header, otherwise {@code false}
	 */
	boolean excludeHeader(String name);

}
