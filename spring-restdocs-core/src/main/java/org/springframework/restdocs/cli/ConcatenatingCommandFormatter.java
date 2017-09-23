/*
 * Copyright 2014-2017 the original author or authors.
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

package org.springframework.restdocs.cli;

import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * {@link CommandFormatter} which concatenates commands with a given {@code separator}.
 *
 * @author Tomasz Kopczynski
 */
final class ConcatenatingCommandFormatter implements CommandFormatter {

	private String separator;

	ConcatenatingCommandFormatter(String separator) {
		this.separator = separator;
	}

	/**
	 * Concatenates a list of {@code String}s with a specified separator.
	 *
	 * @param elements A list of {@code String}s to be concatenated
	 * @return Concatenated list of {@code String}s as one {@code String}
	 */
	@Override
	public String format(List<String> elements) {
		if (CollectionUtils.isEmpty(elements)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (String element : elements) {
			result.append(String.format(this.separator));
			result.append(element);
		}
		return result.toString();
	}

}
