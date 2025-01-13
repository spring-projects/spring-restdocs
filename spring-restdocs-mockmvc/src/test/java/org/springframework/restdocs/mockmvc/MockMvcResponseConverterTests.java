/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.mockmvc;

import java.util.Collections;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link MockMvcResponseConverter}.
 *
 * @author Tomasz Kopczynski
 */
public class MockMvcResponseConverterTests {

	private final MockMvcResponseConverter factory = new MockMvcResponseConverter();

	@Test
	public void basicResponse() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_OK);
		OperationResponse operationResponse = this.factory.convert(response);
		assertThat(operationResponse.getStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void responseWithCookie() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_OK);
		Cookie cookie = new Cookie("name", "value");
		cookie.setDomain("localhost");
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		OperationResponse operationResponse = this.factory.convert(response);
		assertThat(operationResponse.getHeaders().headerSet()).containsOnly(
				entry(HttpHeaders.SET_COOKIE, Collections.singletonList("name=value; Domain=localhost; HttpOnly")));
		assertThat(operationResponse.getCookies()).hasSize(1);
		assertThat(operationResponse.getCookies()).first().extracting(ResponseCookie::getName).isEqualTo("name");
		assertThat(operationResponse.getCookies()).first().extracting(ResponseCookie::getValue).isEqualTo("value");
	}

	@Test
	public void responseWithCustomStatus() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(600);
		OperationResponse operationResponse = this.factory.convert(response);
		assertThat(operationResponse.getStatus()).isEqualTo(HttpStatusCode.valueOf(600));
	}

}
