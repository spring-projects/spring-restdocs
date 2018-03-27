/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.webtestclient;

import reactor.core.publisher.Mono;

import org.springframework.restdocs.config.OperationPreprocessorsConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

/**
 * A configurer that can be used to configure the operation preprocessors.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public final class WebTestClientOperationPreprocessorsConfigurer extends
		OperationPreprocessorsConfigurer<WebTestClientRestDocumentationConfigurer, WebTestClientOperationPreprocessorsConfigurer>
		implements ExchangeFilterFunction {

	WebTestClientOperationPreprocessorsConfigurer(
			WebTestClientRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		return and().filter(request, next);
	}

}
