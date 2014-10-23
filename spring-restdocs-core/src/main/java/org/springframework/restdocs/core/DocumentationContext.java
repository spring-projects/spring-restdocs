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
import java.util.Stack;

class DocumentationContext {

	private static final InheritableThreadLocal<Stack<DocumentationContext>> CONTEXTS = new InheritableThreadLocal<Stack<DocumentationContext>>() {

		@Override
		protected Stack<DocumentationContext> initialValue() {
			return new Stack<DocumentationContext>();
		}

	};

	private final Class<?> documentationClass;

	private final Method documentationMethod;

	public DocumentationContext(Class<?> documentationClass, Method documentationMethod) {
		this.documentationClass = documentationClass;
		this.documentationMethod = documentationMethod;
	}

	public static DocumentationContext current() {
		return CONTEXTS.get().peek();
	}

	public Class<?> getDocumentationClass() {
		return documentationClass;
	}

	public Method getDocumentationMethod() {
		return documentationMethod;
	}

	static void push(DocumentationContext context) {
		CONTEXTS.get().push(context);
	}

	static void pop() {
		CONTEXTS.get().pop();
	}

}
