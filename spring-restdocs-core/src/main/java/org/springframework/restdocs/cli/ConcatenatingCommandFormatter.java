/*
 * Copyright 2014-present the original author or authors.
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
	 * @param elements a list of {@code String}s to be concatenated
	 * @return concatenated list of {@code String}s as one {@code String}
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
