/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import org.springframework.http.HttpHeaders;

/**
 * Base contract for operation requests, request parts, and responses.
 *
 * @author Andy Wilkinson
 */
interface OperationMessage {

	byte[] getContent();

	String getContentAsString();

	HttpHeaders getHeaders();

}
