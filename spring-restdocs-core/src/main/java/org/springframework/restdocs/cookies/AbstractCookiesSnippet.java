/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.cookies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;

/**
 * Abstract {@link TemplatedSnippet} subclass that provides a base for snippets that
 * document a RESTful resource's request or response cookies.
 *
 * @author Andreas Evers
 * @author Clyde Stubbs
 * @since 2.1
 */
public abstract class AbstractCookiesSnippet extends TemplatedSnippet {

	private List<CookieDescriptor> cookieDescriptors;

	protected final boolean ignoreUndocumentedCookies;

	private String type;

	/**
	 * Creates a new {@code AbstractCookiesSnippet} that will produce a snippet named
	 * {@code <type>-cookies}. The cookies will be documented using the given
	 * {@code  descriptors} and the given {@code attributes} will be included in the model
	 * during template rendering.
	 * @param type the type of the cookies
	 * @param descriptors the cookie descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedCookies whether undocumented cookies should be ignored
	 */
	protected AbstractCookiesSnippet(String type, List<CookieDescriptor> descriptors, Map<String, Object> attributes,
			boolean ignoreUndocumentedCookies) {
		super(type + "-cookies", attributes);
		for (CookieDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getName(), "The name of the cookie must not be null");
			if (!descriptor.isIgnored()) {
				Assert.notNull(descriptor.getDescription(), "The description of the cookie must not be null");
			}
		}
		this.cookieDescriptors = descriptors;
		this.type = type;
		this.ignoreUndocumentedCookies = ignoreUndocumentedCookies;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		validateCookieDocumentation(operation);

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> cookies = new ArrayList<>();
		model.put("cookies", cookies);
		for (CookieDescriptor descriptor : this.cookieDescriptors) {
			cookies.add(createModelForDescriptor(descriptor));
		}
		return model;
	}

	private void validateCookieDocumentation(Operation operation) {
		List<CookieDescriptor> missingCookies = findMissingCookies(operation);
		if (!missingCookies.isEmpty()) {
			List<String> names = new ArrayList<>();
			for (CookieDescriptor cookieDescriptor : missingCookies) {
				names.add(cookieDescriptor.getName());
			}
			throw new SnippetException(
					"Cookies with the following names were not found" + " in the " + this.type + ": " + names);
		}
	}

	/**
	 * Finds the cookies that are missing from the operation. A cookie is missing if it is
	 * described by one of the {@code cookieDescriptors} but is not present in the
	 * operation.
	 * @param operation the operation
	 * @return descriptors for the cookies that are missing from the operation
	 */
	protected List<CookieDescriptor> findMissingCookies(Operation operation) {
		List<CookieDescriptor> missingCookies = new ArrayList<>();
		Set<String> actualCookies = extractActualCookies(operation);
		for (CookieDescriptor cookieDescriptor : this.cookieDescriptors) {
			if (!cookieDescriptor.isOptional() && !actualCookies.contains(cookieDescriptor.getName())) {
				missingCookies.add(cookieDescriptor);
			}
		}

		return missingCookies;
	}

	/**
	 * Extracts the names of the cookies from the request or response of the given
	 * {@code operation}.
	 * @param operation the operation
	 * @return the cookie names
	 */
	protected abstract Set<String> extractActualCookies(Operation operation);

	/**
	 * Returns the list of {@link CookieDescriptor CookieDescriptors} that will be used to
	 * generate the documentation.
	 * @return the cookie descriptors
	 */
	protected final List<CookieDescriptor> getCookieDescriptors() {
		return this.cookieDescriptors;
	}

	/**
	 * Returns a model for the given {@code descriptor}.
	 * @param descriptor the descriptor
	 * @return the model
	 */
	protected Map<String, Object> createModelForDescriptor(CookieDescriptor descriptor) {
		Map<String, Object> model = new HashMap<>();
		model.put("name", descriptor.getName());
		model.put("description", descriptor.getDescription());
		model.put("optional", descriptor.isOptional());
		model.putAll(descriptor.getAttributes());
		return model;
	}

}
