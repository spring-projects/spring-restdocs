/*
 * Copyright 2014-2016 the original author or authors.
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

import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;

/**
 * A converter for creating an {@link OperationResponse} from a REST Assured
 * {@link Response}.
 *
 * @author Andy Wilkinson
 */
class RestAssuredResponseConverter implements ResponseConverter<Response> {

	@Override
	public OperationResponse convert(Response response) {
		return new OperationResponseFactory().create(
				HttpStatus.valueOf(response.getStatusCode()), extractHeaders(response),
				extractContent(response));
	}

	private HttpHeaders extractHeaders(Response response) {
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Header header : response.getHeaders()) {
			httpHeaders.add(header.getName(), header.getValue());
		}
		return httpHeaders;
	}

	private byte[] extractContent(Response response) {
		return response.getBody().asByteArray();
	}

}
