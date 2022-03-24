/*
 * Copyright 2014-2022 the original author or authors.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;

/**
 * Abstract {@link TemplatedSnippet} subclass that provides a base for snippets that
 * document a RESTful resource's request or response cookies.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 * @since 3.0
 */
public abstract class AbstractCookiesSnippet extends TemplatedSnippet {

	private final Map<String, CookieDescriptor> descriptorsByName = new LinkedHashMap<>();

	private final boolean ignoreUndocumentedCookies;

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
			Assert.notNull(descriptor.getName(), "Cookie descriptors must have a name");
			if (!descriptor.isIgnored()) {
				Assert.notNull(descriptor.getDescription(), "The descriptor for cookie '" + descriptor.getName()
						+ "' must either have a description or be marked as ignored");
			}
			this.descriptorsByName.put(descriptor.getName(), descriptor);
		}
		this.ignoreUndocumentedCookies = ignoreUndocumentedCookies;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		verifyCookieDescriptors(operation);

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> cookies = new ArrayList<>();
		for (CookieDescriptor descriptor : this.descriptorsByName.values()) {
			if (!descriptor.isIgnored()) {
				cookies.add(createModelForDescriptor(descriptor));
			}
		}
		model.put("cookies", cookies);
		return model;
	}

	private void verifyCookieDescriptors(Operation operation) {
		Set<String> actualCookies = extractActualCookies(operation);
		Set<String> expectedCookies = new HashSet<>();
		for (Entry<String, CookieDescriptor> entry : this.descriptorsByName.entrySet()) {
			if (!entry.getValue().isOptional()) {
				expectedCookies.add(entry.getKey());
			}
		}
		Set<String> undocumentedCookies;
		if (this.ignoreUndocumentedCookies) {
			undocumentedCookies = Collections.emptySet();
		}
		else {
			undocumentedCookies = new HashSet<>(actualCookies);
			undocumentedCookies.removeAll(this.descriptorsByName.keySet());
		}
		Set<String> missingCookies = new HashSet<>(expectedCookies);
		missingCookies.removeAll(actualCookies);

		if (!undocumentedCookies.isEmpty() || !missingCookies.isEmpty()) {
			verificationFailed(undocumentedCookies, missingCookies);
		}
	}

	/**
	 * Extracts the names of the cookies from the request or response of the given
	 * {@code operation}.
	 * @param operation the operation
	 * @return the cookie names
	 */
	protected abstract Set<String> extractActualCookies(Operation operation);

	/**
	 * Called when the documented cookies do not match the actual cookies.
	 * @param undocumentedCookies the cookies that were found in the operation but were
	 * not documented
	 * @param missingCookies the cookies that were documented but were not found in the
	 * operation
	 */
	protected abstract void verificationFailed(Set<String> undocumentedCookies, Set<String> missingCookies);

	/**
	 * Returns the list of {@link CookieDescriptor CookieDescriptors} that will be used to
	 * generate the documentation.
	 * @return the cookie descriptors
	 */
	protected final Map<String, CookieDescriptor> getCookieDescriptors() {
		return this.descriptorsByName;
	}

	/**
	 * Returns whether or not this snippet ignores undocumented cookies.
	 * @return {@code true} if undocumented cookies are ignored, otherwise {@code false}
	 */
	protected final boolean isIgnoreUndocumentedCookies() {
		return this.ignoreUndocumentedCookies;
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
