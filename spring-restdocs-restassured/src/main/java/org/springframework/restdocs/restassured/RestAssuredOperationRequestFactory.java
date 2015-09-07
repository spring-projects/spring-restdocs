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

package org.springframework.restdocs.restassured;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.StandardOperationRequest;

import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.specification.FilterableRequestSpecification;

class RestAssuredOperationRequestFactory {

	OperationRequest createOperationRequest(FilterableRequestSpecification requestSpec,
			FilterContext context) {
		return new StandardOperationRequest(URI.create(context.getCompleteRequestPath()),
				HttpMethod.valueOf(context.getRequestMethod().name()),
				extractContent(requestSpec), extractHeaders(requestSpec),
				extractParameters(requestSpec), extractParts(requestSpec));
	}

	private byte[] extractContent(FilterableRequestSpecification requestSpec) {
		return requestSpec.getBody() == null ? new byte[0] : (byte[]) requestSpec
				.getBody();
	}

	private HttpHeaders extractHeaders(FilterableRequestSpecification requestSpec) {
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Header header : requestSpec.getHeaders()) {
			httpHeaders.add(header.getName(), header.getValue());
		}
		return httpHeaders;
	}

	private Parameters extractParameters(FilterableRequestSpecification requestSpec) {
		Parameters parameters = new Parameters();
		Map<String, ?> requestParams = requestSpec.getRequestParams();
		for (Entry<String, ?> entry : requestParams.entrySet()) {
			parameters.add(entry.getKey(), entry.getValue().toString());
		}
		return parameters;
	}

	private Collection<OperationRequestPart> extractParts(
			FilterableRequestSpecification requestSpec) {
		return Collections.emptyList();
	}
}
