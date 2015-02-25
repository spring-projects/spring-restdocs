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

package org.springframework.restdocs.config;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code RestDocumentationContext} encapsulates the context in which the documentation of
 * a RESTful API is being performed.
 * 
 * @author Andy Wilkinson
 */
public class RestDocumentationContext {

	private static final ThreadLocal<RestDocumentationContext> CONTEXTS = new InheritableThreadLocal<RestDocumentationContext>();

	private final AtomicInteger stepCount = new AtomicInteger(0);

	private final Method testMethod;

	private RestDocumentationContext() {
		this(null);
	}

	private RestDocumentationContext(Method testMethod) {
		this.testMethod = testMethod;
	}

	/**
	 * Returns the test {@link Method method} that is currently executing
	 * 
	 * @return The test method
	 */
	public Method getTestMethod() {
		return this.testMethod;
	}

	/**
	 * Gets and then increments the current step count
	 * 
	 * @return The step count prior to it being incremented
	 */
	int getAndIncrementStepCount() {
		return this.stepCount.getAndIncrement();
	}

	/**
	 * Gets the current step count
	 * 
	 * @return The current step count
	 */
	public int getStepCount() {
		return this.stepCount.get();
	}

	static void establishContext(Method testMethod) {
		CONTEXTS.set(new RestDocumentationContext(testMethod));
	}

	static void clearContext() {
		CONTEXTS.set(null);
	}

	/**
	 * Returns the current context, never {@code null}.
	 * 
	 * @return The current context
	 */
	public static RestDocumentationContext currentContext() {
		return CONTEXTS.get();
	}

}
