/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link HeaderFilter} that excludes a header if its name is an exact match.
 *
 * @author Andy Wilkinson
 */
class ExactMatchHeaderFilter implements HeaderFilter {

	private final Set<String> headersToExclude;

	ExactMatchHeaderFilter(String... headersToExclude) {
		this.headersToExclude = new HashSet<>(Arrays.asList(headersToExclude));
	}

	@Override
	public boolean excludeHeader(String name) {
		return this.headersToExclude.contains(name);
	}

}
