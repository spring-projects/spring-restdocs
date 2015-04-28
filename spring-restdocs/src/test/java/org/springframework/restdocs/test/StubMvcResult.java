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

package org.springframework.restdocs.test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * A minimal stub implementation of {@link MvcResult}
 * 
 * @author Andy Wilkinson
 *
 */
public class StubMvcResult implements MvcResult {

	private final MockHttpServletRequest request;

	private final MockHttpServletResponse response;

	public static StubMvcResult result() {
		return new StubMvcResult();
	}

	public static StubMvcResult result(RequestBuilder requestBuilder) {
		return new StubMvcResult(requestBuilder);
	}

	public static StubMvcResult result(MockHttpServletRequest request) {
		return new StubMvcResult(request);
	}

	public static StubMvcResult result(MockHttpServletResponse response) {
		return new StubMvcResult(response);
	}

	private StubMvcResult() {
		this(new MockHttpServletRequest(), new MockHttpServletResponse());
	}

	private StubMvcResult(MockHttpServletRequest request) {
		this(request, new MockHttpServletResponse());
	}

	private StubMvcResult(MockHttpServletResponse response) {
		this(new MockHttpServletRequest(), response);
	}

	private StubMvcResult(MockHttpServletRequest request, MockHttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	private StubMvcResult(RequestBuilder requestBuilder) {
		this(requestBuilder.buildRequest(new MockServletContext()));
	}

	@Override
	public MockHttpServletRequest getRequest() {
		return this.request;
	}

	@Override
	public MockHttpServletResponse getResponse() {
		return this.response;
	}

	@Override
	public Object getHandler() {
		return null;
	}

	@Override
	public HandlerInterceptor[] getInterceptors() {
		return null;
	}

	@Override
	public ModelAndView getModelAndView() {
		return null;
	}

	@Override
	public Exception getResolvedException() {
		return null;
	}

	@Override
	public FlashMap getFlashMap() {
		return null;
	}

	@Override
	public Object getAsyncResult() {
		return null;
	}

	@Override
	public Object getAsyncResult(long timeToWait) {
		return null;
	}

}
