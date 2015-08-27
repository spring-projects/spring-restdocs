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

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ReflectionUtils;

/**
 * A base class for {@link ResponsePostProcessor ResponsePostProcessors} that modify the
 * content of the response.
 * 
 * @author Andy Wilkinson
 */
public abstract class ContentModifyingReponsePostProcessor implements
		ResponsePostProcessor {

	@Override
	public MockHttpServletResponse postProcess(MockHttpServletResponse response)
			throws Exception {
		String modifiedContent = modifyContent(response.getContentAsString());

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(MockHttpServletResponse.class);
		enhancer.setCallback(new ContentModifyingMethodInterceptor(modifiedContent,
				response));

		return (MockHttpServletResponse) enhancer.create();
	}

	/**
	 * Returns a modified version of the given {@code originalContent}
	 * 
	 * @param originalContent the content to modify
	 * @return the modified content
	 * @throws Exception if a failure occurs while modifying the content
	 */
	protected abstract String modifyContent(String originalContent) throws Exception;

	private static class ContentModifyingMethodInterceptor implements MethodInterceptor {

		private final Method getContentAsStringMethod = findMethod("getContentAsString");

		private final Method getContentAsByteArray = findMethod("getContentAsByteArray");

		private final String modifiedContent;

		private final MockHttpServletResponse delegate;

		private ContentModifyingMethodInterceptor(String modifiedContent,
				MockHttpServletResponse delegate) {
			this.modifiedContent = modifiedContent;
			this.delegate = delegate;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args,
				MethodProxy methodProxy) throws IllegalAccessException,
				InvocationTargetException {
			if (this.getContentAsStringMethod.equals(method)) {
				return this.modifiedContent;
			}
			if (this.getContentAsByteArray.equals(method)) {
				return this.modifiedContent.getBytes();
			}
			return method.invoke(this.delegate, args);
		}

		private static Method findMethod(String methodName) {
			return BridgeMethodResolver.findBridgedMethod(ReflectionUtils.findMethod(
					MockHttpServletResponse.class, methodName));
		}

	}

}
