/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

/**
 * A {@link RuntimeException} thrown to indicate a problem with the generation of a
 * documentation snippet.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("serial")
public class SnippetException extends RuntimeException {

	/**
	 * Creates a new {@code SnippetException} described by the given {@code message}.
	 * @param message the message that describes the problem
	 */
	public SnippetException(String message) {
		super(message);
	}

}
