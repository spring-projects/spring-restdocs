/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.resttestclient;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.restdocs.config.OperationPreprocessorsConfigurer;

/**
 * A configurer that can be used to configure the operation preprocessors.
 *
 * @author Andy Wilkinson
 * @since 4.1.0
 */
public final class RestTestClientOperationPreprocessorsConfigurer extends
		OperationPreprocessorsConfigurer<RestTestClientRestDocumentationConfigurer, RestTestClientOperationPreprocessorsConfigurer>
		implements ClientHttpRequestInterceptor {

	RestTestClientOperationPreprocessorsConfigurer(RestTestClientRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		return and().intercept(request, body, execution);
	}

}
