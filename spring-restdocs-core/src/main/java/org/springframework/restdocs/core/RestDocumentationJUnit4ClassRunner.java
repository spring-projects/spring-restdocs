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

import java.lang.reflect.Method;

import org.junit.runners.model.InitializationError;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class RestDocumentationJUnit4ClassRunner extends SpringJUnit4ClassRunner {

	public RestDocumentationJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	protected Object createTest() throws Exception {
		Object testInstance = createProxiedTestInstance();
		getTestContextManager().prepareTestInstance(testInstance);
		return testInstance;
	}

	private Object createProxiedTestInstance() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(getTestClass().getJavaClass());
		enhancer.setClassLoader(getTestClass().getJavaClass().getClassLoader());
		enhancer.setCallback(new DocumentationContextManagingMethodInterceptor(
				getTestClass().getJavaClass()));
		return enhancer.create();
	}

	private static class DocumentationContextManagingMethodInterceptor implements
			MethodInterceptor {

		private final Class<?> testClass;

		private DocumentationContextManagingMethodInterceptor(Class<?> testClass) {
			this.testClass = testClass;
		}

		@Override
		public Object intercept(Object target, Method method, Object[] args,
				MethodProxy methodProxy) throws Throwable {
			DocumentationContext.push(new DocumentationContext(this.testClass, method));
			try {
				return methodProxy.invokeSuper(target, args);
			}
			finally {
				DocumentationContext.pop();
			}
		}

	}
}