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

package org.springframework.restdocs.mockmvc;

import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.config.AbstractNestedConfigurer;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

/**
 * A configurer that can be used to configure the documented URIs.
 *
 * @author Andy Wilkinson
 * @author Eddú Meléndez
 */
public class UriConfigurer
		extends AbstractNestedConfigurer<MockMvcRestDocumentationConfigurer>
		implements MockMvcConfigurer {

	/**
	 * The default scheme for documented URIs.
	 *
	 * @see #withScheme(String)
	 */
	public static final String DEFAULT_SCHEME = "http";

	/**
	 * The HTTPS scheme for documented URIs.
	 *
	 * @see #withHttps()
	 */
	public static final String HTTPS_SCHEME = "https";

	/**
	 * The default host for documented URIs.
	 *
	 * @see #withHost(String)
	 */
	public static final String DEFAULT_HOST = "localhost";

	/**
	 * The default port for documented URIs.
	 *
	 * @see #withHttp(int)
	 */
	public static final int DEFAULT_PORT = 8080;

	/**
	 * The HTTP port for documented URIs.
	 *
	 * @see #withHttp(int)
	 */
	public static final int HTTP_PORT = 80;

	/**
	 * The HTTPS port for documented URIs.
	 *
	 * @see #withHttps(int)
	 */
	public static final int HTTPS_PORT = 443;

	private String scheme = DEFAULT_SCHEME;

	private String host = DEFAULT_HOST;

	private int port = DEFAULT_PORT;

	UriConfigurer(MockMvcRestDocumentationConfigurer parent) {
		super(parent);
	}

	/**
	 * Configures any documented URIs to use the given {@code scheme}. The default is
	 * {@code http}.
	 *
	 * @param scheme The URI scheme
	 * @return {@code this}
	 * @deprecated Since 1.2.0 in favor of {@link #withHttp()} or {@link #withHttps()}
	 */
	@Deprecated
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
	 * @deprecated Since 1.2.0 in favor of {@link #withHttp(int)} or {@link #withHttps(int)}
	 */
	@Deprecated
	public UriConfigurer withPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Configures any documented URIs to use HTTP scheme.  The default port is 8080.
	 *
	 * @return {@code this}
	 */
	public UriConfigurer withHttp() {
		this.scheme = DEFAULT_SCHEME;
		this.port = DEFAULT_PORT;
		return this;
	}

	/**
	 * Configures any documented URIs to use HTTP scheme and the given {@link #port}.
	 *
	 * @param port The URI port
	 * @return {@code this}
	 */
	public UriConfigurer withHttp(int port) {
		this.scheme = DEFAULT_SCHEME;
		this.port = port;
		return this;
	}

	/**
	 * Configures any documented URIs to use HTTPS scheme. The default port is 443.
	 *
	 * @return {@code this}
	 */
	public UriConfigurer withHttps() {
		this.scheme = HTTPS_SCHEME;
		this.port = HTTPS_PORT;
		return this;
	}

	/**
	 * Configures any documented URIs to use HTTPS scheme and the given {@link #port}.
	 *
	 * @param port The URI port
	 * @return {@code this}
	 */
	public UriConfigurer withHttps(int port) {
		this.scheme = HTTPS_SCHEME;
		this.port = port;
		return this;
	}

	@Override
	public void apply(Map<String, Object> configuration,
			RestDocumentationContext context) {
		MockHttpServletRequest request = (MockHttpServletRequest) configuration
				.get(MockHttpServletRequest.class.getName());
		request.setScheme(this.scheme);
		request.setServerPort(this.port);
		request.setServerName(this.host);
	}

	@Override
	public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
		and().afterConfigurerAdded(builder);
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return and().beforeMockMvcCreated(builder, context);
	}

}
