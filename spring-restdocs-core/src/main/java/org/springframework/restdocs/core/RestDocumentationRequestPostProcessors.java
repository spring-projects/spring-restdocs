/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.core;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public abstract class RestDocumentationRequestPostProcessors {

	public static RequestPostProcessor port(final int port) {
		return new RequestPostProcessor() {

			@Override
			public MockHttpServletRequest postProcessRequest(
					MockHttpServletRequest request) {
				request.setRemotePort(port);
				request.setServerPort(port);
				return request;
			}
		};

	}

	public static RequestPostProcessor host(final String host) {
		return new RequestPostProcessor() {

			@Override
			public MockHttpServletRequest postProcessRequest(
					MockHttpServletRequest request) {
				request.setRemoteHost(host);
				return request;
			}
		};

	}

}
