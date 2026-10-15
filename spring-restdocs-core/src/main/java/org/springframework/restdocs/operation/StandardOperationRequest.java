/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Standard implementation of {@link OperationRequest}.
 *
 * @author Andy Wilkinson
 */
class StandardOperationRequest extends AbstractOperationMessage implements OperationRequest {

	private HttpMethod method;

	private Collection<OperationRequestPart> parts;

	private URI uri;

	private Collection<RequestCookie> cookies;

	/**
	 * Creates a new request with the given {@code uri} and {@code method}. The request
	 * will have the given {@code headers}, {@code parameters}, {@code parts}, and
	 * {@code cookies}.
	 * @param uri the uri
	 * @param method the method
	 * @param content the content
	 * @param headers the headers
	 * @param parts the parts
	 * @param cookies the cookies
	 */
	StandardOperationRequest(URI uri, HttpMethod method, byte @Nullable [] content, HttpHeaders headers,
			Collection<OperationRequestPart> parts, Collection<RequestCookie> cookies) {
		super(content, headers);
		this.uri = uri;
		this.method = method;
		this.parts = parts;
		this.cookies = cookies;
	}

	@Override
	public HttpMethod getMethod() {
		return this.method;
	}

	@Override
	public Collection<OperationRequestPart> getParts() {
		return Collections.unmodifiableCollection(this.parts);
	}

	@Override
	public URI getUri() {
		return this.uri;
	}

	@Override
	public Collection<RequestCookie> getCookies() {
		return this.cookies;
	}

}
