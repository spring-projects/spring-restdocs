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

package org.springframework.restdocs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.response.ResponsePostProcessor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ReflectionUtils;

/**
 * Modifies the response in an {@link MvcResult} by applying {@link ResponsePostProcessor
 * ResponsePostProcessors} to it.
 * 
 * @see RestDocumentation#modifyResponseTo(ResponsePostProcessor...)
 * @author Andy Wilkinson
 */
public final class ResponseModifier {

	private final List<ResponsePostProcessor> postProcessors;

	ResponseModifier(ResponsePostProcessor... postProcessors) {
		this.postProcessors = Arrays.asList(postProcessors);
	}

	/**
	 * Provides a {@link RestDocumentationResultHandler} that can be used to document the
	 * request and modified response.
	 * @param identifier an identifier for the API call that is being documented
	 * @param snippets the snippets to use to document the call
	 * @return the result handler that will produce the documentation
	 */
	public RestDocumentationResultHandler andDocument(String identifier,
			Snippet... snippets) {
		return new ResponseModifyingRestDocumentationResultHandler(identifier, snippets);
	}

	class ResponseModifyingRestDocumentationResultHandler extends
			RestDocumentationResultHandler {

		private ResponseModifyingRestDocumentationResultHandler(String identifier,
				Snippet... snippets) {
			super(identifier, snippets);
		}

		@Override
		public void handle(MvcResult result) throws Exception {
			super.handle(postProcessResponse(result));
		}

		MvcResult postProcessResponse(MvcResult result) throws Exception {
			MockHttpServletResponse response = result.getResponse();
			for (ResponsePostProcessor postProcessor : ResponseModifier.this.postProcessors) {
				response = postProcessor.postProcess(response);
			}
			return decorateResult(result, response);
		}

		private MvcResult decorateResult(MvcResult result,
				MockHttpServletResponse response) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(MvcResult.class);
			enhancer.setCallback(new GetResponseMethodInterceptor(response, result));
			return (MvcResult) enhancer.create();
		}

		private class GetResponseMethodInterceptor implements MethodInterceptor {

			private final MvcResult delegate;

			private final MockHttpServletResponse response;

			private final Method getResponseMethod = findMethod("getResponse");

			private GetResponseMethodInterceptor(MockHttpServletResponse response,
					MvcResult delegate) {
				this.delegate = delegate;
				this.response = response;
			}

			@Override
			public Object intercept(Object proxy, Method method, Object[] args,
					MethodProxy methodProxy) throws IllegalAccessException,
					InvocationTargetException {
				if (this.getResponseMethod.equals(method)) {
					return this.response;
				}
				return method.invoke(this.delegate, args);
			}

			private Method findMethod(String methodName) {
				return BridgeMethodResolver.findBridgedMethod(ReflectionUtils.findMethod(
						MvcResult.class, methodName));
			}

		}

	}

}
