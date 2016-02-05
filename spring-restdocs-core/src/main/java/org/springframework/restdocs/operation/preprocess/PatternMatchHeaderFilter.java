/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
