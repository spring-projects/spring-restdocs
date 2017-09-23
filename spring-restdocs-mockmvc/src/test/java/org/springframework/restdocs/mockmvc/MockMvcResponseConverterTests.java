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

import java.util.Collections;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.operation.OperationResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

		assertThat(operationResponse.getStatus(), is(HttpStatus.OK));
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

		assertThat(operationResponse.getHeaders().size(), is(1));
		assertTrue(operationResponse.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
		assertThat(operationResponse.getHeaders().get(HttpHeaders.SET_COOKIE), equalTo(
				Collections.singletonList("name=value; Domain=localhost; HttpOnly")));
	}

}
