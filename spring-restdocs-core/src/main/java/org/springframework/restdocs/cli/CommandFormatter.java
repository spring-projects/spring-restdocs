/*
 * Copyright 2014-present the original author or authors.
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
