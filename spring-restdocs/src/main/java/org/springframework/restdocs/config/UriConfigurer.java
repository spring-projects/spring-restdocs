/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.config;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * A configurer that can be used to configure the documented URIs
 * 
 * @author Andy Wilkinson
 */
public class UriConfigurer extends AbstractNestedConfigurer<RestDocumentationConfigurer> {

	/**
	 * The default scheme for documented URIs
	 * @see #withScheme(String)
	 */
	public static final String DEFAULT_SCHEME = "http";

	/**
	 * The defalt host for documented URIs
	 * @see #withHost(String)
	 */
	public static final String DEFAULT_HOST = "localhost";

	/**
	 * The default port for documented URIs
	 * @see #withPort(int)
	 */
	public static final int DEFAULT_PORT = 8080;

	private String scheme = DEFAULT_SCHEME;

	private String host = DEFAULT_HOST;

	private int port = DEFAULT_PORT;

	protected UriConfigurer(RestDocumentationConfigurer parent) {
		super(parent);
	}

	/**
	 * Configures any documented URIs to use the given {@code scheme}. The default is
	 * {@code http}.
	 * 
	 * @param scheme The URI scheme
	 * @return {@code this}
	 */
	public UriConfigurer withScheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	/**
	 * Configures any documented URIs to use the given {@code host}. The default is
	 * {@code localhost}.
	 * 
	 * @param host The URI host
	 * @return {@code this}
	 */
	public UriConfigurer withHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Configures any documented URIs to use the given {@code port}. The default is
	 * {@code 8080}.
	 * 
	 * @param port The URI port
	 * @return {@code this}
	 */
	public UriConfigurer withPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	void apply(MockHttpServletRequest request) {
		request.setScheme(this.scheme);
		request.setServerPort(this.port);
		request.setServerName(this.host);
	}

}
