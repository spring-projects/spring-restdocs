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

package org.springframework.restdocs.webtestclient;

import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.test.web.reactive.server.ExchangeResult;

/**
 * A {@link ResponseConverter} for creating an {@link OperationResponse} derived from an
 * {@link ExchangeResult}.
 *
 * @author Andy Wilkinson
 */
class WebTestClientResponseConverter implements ResponseConverter<ExchangeResult> {

	@Override
	public OperationResponse convert(ExchangeResult result) {
		return new OperationResponseFactory().create(result.getStatus(),
				result.getResponseHeaders(), result.getResponseBodyContent());
	}

}
