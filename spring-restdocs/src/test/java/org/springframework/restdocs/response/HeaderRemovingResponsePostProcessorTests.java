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

package org.springframework.restdocs.response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for {@link HeaderRemovingResponsePostProcessor}.
 * 
 * @author Andy Wilkinson
 *
 */
public class HeaderRemovingResponsePostProcessorTests {

	private final MockHttpServletResponse response = new MockHttpServletResponse();

	@Before
	public void configureResponse() {
		this.response.addHeader("a", "alpha");
		this.response.addHeader("b", "bravo");
	}

	@Test
	public void containsHeaderHonoursRemovedHeaders() {
		MockHttpServletResponse response = removeHeaders("a");
		assertThat(response.containsHeader("a"), is(false));
		assertThat(response.containsHeader("b"), is(true));
	}

	@Test
	public void getHeaderNamesHonoursRemovedHeaders() {
		MockHttpServletResponse response = removeHeaders("a");
		assertThat(response.getHeaderNames(), contains("b"));
	}

	@Test
	public void getHeaderHonoursRemovedHeaders() {
		MockHttpServletResponse response = removeHeaders("a");
		assertThat(response.getHeader("a"), is(nullValue()));
		assertThat(response.getHeader("b"), is("bravo"));
	}

	@Test
	public void getHeadersHonoursRemovedHeaders() {
		MockHttpServletResponse response = removeHeaders("a");
		assertThat(response.getHeaders("a"), is(empty()));
		assertThat(response.getHeaders("b"), contains("bravo"));
	}

	@Test
	public void getHeaderValueHonoursRemovedHeaders() {
		MockHttpServletResponse response = removeHeaders("a");
		assertThat(response.getHeaderValue("a"), is(nullValue()));
		assertThat(response.getHeaderValue("b"), is((Object) "bravo"));
	}

	@Test
	public void getHeaderValuesHonoursRemovedHeaders() {
		MockHttpServletResponse response = removeHeaders("a");
		assertThat(response.getHeaderValues("a"), is(empty()));
		assertThat(response.getHeaderValues("b"), contains((Object) "bravo"));
	}

	private MockHttpServletResponse removeHeaders(String... headerNames) {
		return new HeaderRemovingResponsePostProcessor(headerNames)
				.postProcess(this.response);
	}

}
