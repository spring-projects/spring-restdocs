/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A {@link HeaderFilter} that excludes a header if its name matches a {@link Pattern}.
 *
 * @author Andy Wilkinson
 * @author Roland Huss
 */
class PatternMatchHeaderFilter implements HeaderFilter {

	private Set<Pattern> exclusionPatterns;

	PatternMatchHeaderFilter(String... exclusionPatterns) {
		this.exclusionPatterns = new HashSet<>();
		for (String exclusionPattern : exclusionPatterns) {
			this.exclusionPatterns.add(Pattern.compile(exclusionPattern));
		}
	}

	@Override
	public boolean excludeHeader(String name) {
		for (Pattern pattern : this.exclusionPatterns) {
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

}
