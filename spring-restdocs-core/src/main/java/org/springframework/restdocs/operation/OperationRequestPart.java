/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;

/**
 * A part of a multipart request.
 *
 * @author Andy Wilkinson
 * @see OperationRequest#getParts()
 */
public interface OperationRequestPart {

	/**
	 * Returns the name of the part.
	 * @return the name
	 */
	String getName();

	/**
	 * Returns the name of the file that is being uploaded in this part.
	 * @return the name of the file, or {@code null} if the part has no file name
	 */
	@Nullable String getSubmittedFileName();

	/**
	 * Returns the contents of the part.
	 * @return the contents
	 */
	byte[] getContent();

	/**
	 * Returns the content of the part as a {@link String}. If the part has no content an
	 * empty string is returned. If the part has a {@code Content-Type} header that
	 * specifies a charset then that charset will be used when converting the contents to
	 * a {@code String}.
	 * @return the contents as string, never {@code null}
	 */
	String getContentAsString();

	/**
	 * Returns the part's headers.
	 * @return the headers
	 */
	HttpHeaders getHeaders();

}
