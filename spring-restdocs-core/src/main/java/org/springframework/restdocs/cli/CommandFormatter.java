/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
 * Formatter for CLI commands such as those included in {@link CurlRequestSnippet} and
 * {@link HttpieRequestSnippet}.
 *
 * @author Tomasz Kopczynski
 * @since 1.2.0
 */
public interface CommandFormatter {

	/**
	 * Formats a list of {@code elements} into a single {@code String}.
	 * @param elements the {@code String} elements to be formatted
	 * @return a single formatted {@code String}
	 */
	String format(List<String> elements);

}
