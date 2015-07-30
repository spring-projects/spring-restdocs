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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ReflectionUtils;

/**
 * A {@link ResponsePostProcessor} that removes headers from the response
 * 
 * @author Andy Wilkinson
 */
class HeaderRemovingResponsePostProcessor implements ResponsePostProcessor {

	private final Set<String> headersToRemove;

	HeaderRemovingResponsePostProcessor(String... headersToRemove) {
		this.headersToRemove = new HashSet<>(Arrays.asList(headersToRemove));
	}

	@Override
	public MockHttpServletResponse postProcess(final MockHttpServletResponse response) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(MockHttpServletResponse.class);
		enhancer.setCallback(new HeaderHidingMethodInterceptor(this.headersToRemove,
				response));

		return (MockHttpServletResponse) enhancer.create();
	}

	private static final class HeaderHidingMethodInterceptor implements MethodInterceptor {

		private final MockHttpServletResponse response;

		private final List<Method> interceptedMethods = Arrays.asList(
				findHeaderMethod("containsHeader", String.class),
				findHeaderMethod("getHeader", String.class),
				findHeaderMethod("getHeaderValue", String.class),
				findHeaderMethod("getHeaders", String.class),
				findHeaderMethod("getHeaderValues", String.class));

		private final Method getHeaderNamesMethod = findHeaderMethod("getHeaderNames");

		private final Set<String> hiddenHeaders;

		private HeaderHidingMethodInterceptor(Set<String> hiddenHeaders,
				MockHttpServletResponse response) {
			this.hiddenHeaders = hiddenHeaders;
			this.response = response;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args,
				MethodProxy methodProxy) throws IllegalAccessException,
				InvocationTargetException {
			if (this.getHeaderNamesMethod.equals(method)) {
				List<String> headerNames = new ArrayList<>();
				for (String candidate : this.response.getHeaderNames()) {
					if (!isHiddenHeader(candidate)) {
						headerNames.add(candidate);
					}
				}
				return headerNames;
			}
			if (this.interceptedMethods.contains(method) && isHiddenHeader(args)) {
				if (method.getReturnType().equals(boolean.class)) {
					return false;
				}
				else if (Collection.class.isAssignableFrom(method.getReturnType())) {
					return Collections.emptyList();
				}
				else {
					return null;
				}
			}

			return method.invoke(this.response, args);
		}

		private boolean isHiddenHeader(Object[] args) {
			if (args.length == 1 && args[0] instanceof String) {
				return isHiddenHeader((String) args[0]);
			}
			return false;
		}

		private boolean isHiddenHeader(String headerName) {
			for (String hiddenHeader : this.hiddenHeaders) {
				if (hiddenHeader.equalsIgnoreCase(headerName)) {
					return true;
				}
			}
			return false;
		}

		private static Method findHeaderMethod(String methodName, Class<?>... args) {
			Method candidate = ReflectionUtils.findMethod(MockHttpServletResponse.class,
					methodName, args);
			if (candidate.isBridge()) {
				return BridgeMethodResolver.findBridgedMethod(candidate);
			}
			return candidate;
		}
	}

}
