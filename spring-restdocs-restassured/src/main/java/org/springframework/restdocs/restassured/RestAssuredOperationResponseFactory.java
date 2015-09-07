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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.StandardOperationResponse;

import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

class RestAssuredOperationResponseFactory {

	OperationResponse createOperationResponse(Response response) {
		return new StandardOperationResponse(
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
