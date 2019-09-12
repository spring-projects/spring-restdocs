/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.restassured3;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Test;

import org.springframework.restdocs.operation.OperationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link RestAssuredResponseConverter}.
 *
 * @author Andy Wilkinson
 */
public class RestAssuredResponseConverterTests {

	private final RestAssuredResponseConverter converter = new RestAssuredResponseConverter();

	@Test
	public void responseWithCustomStatus() {
		Response response = mock(Response.class);
		given(response.getStatusCode()).willReturn(600);
		given(response.getHeaders()).willReturn(new Headers());
		ResponseBody<?> body = mock(ResponseBody.class);
		given(response.getBody()).willReturn(body);
		given(body.asByteArray()).willReturn(new byte[0]);
		OperationResponse operationResponse = this.converter.convert(response);
		assertThat(operationResponse.getStatus()).isNull();
		assertThat(operationResponse.getStatusCode()).isEqualTo(600);
	}

}
