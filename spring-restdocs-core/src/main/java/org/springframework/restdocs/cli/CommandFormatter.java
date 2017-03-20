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

/**
 * Formatter for {@link CurlRequestSnippet} and {@link HttpieRequestSnippet}.
 * Its purpose is to format a command snippet from a list of its parts represented
 * as {@code String}s.
 *
 * @author Tomasz Kopczynski
 */
public interface CommandFormatter {

	/**
	 * Formats a list of {@code String}s into a single {@code String}.
	 *
	 * @param elements A list of {@code String}s to be formatted
	 * @return A list of {@code String}s formatted as one {@code String}
	 */
	String format(List<String> elements);
}
