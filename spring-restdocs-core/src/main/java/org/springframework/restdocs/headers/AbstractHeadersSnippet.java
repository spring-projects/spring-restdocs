/*
 * Copyright 2014-2016 the original author or authors.
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

package org.springframework.restdocs.headers;

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
 * document a RESTful resource's request or response headers.
 *
 * @author Andreas Evers
 */
public abstract class AbstractHeadersSnippet extends TemplatedSnippet {

	private List<HeaderDescriptor> headerDescriptors;

	private String type;

	/**
	 * Creates a new {@code AbstractHeadersSnippet} that will produce a snippet named
	 * {@code <type>-headers}. The headers will be documented using the given
	 * {@code  descriptors} and the given {@code attributes} will be included in the model
	 * during template rendering.
	 *
	 * @param type the type of the headers
	 * @param descriptors the header descriptors
	 * @param attributes the additional attributes
	 */
	protected AbstractHeadersSnippet(String type, List<HeaderDescriptor> descriptors,
			Map<String, Object> attributes) {
		super(type + "-headers", attributes);
		for (HeaderDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getName());
			Assert.notNull(descriptor.getDescription());
		}
		this.headerDescriptors = descriptors;
		this.type = type;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		validateHeaderDocumentation(operation);

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> headers = new ArrayList<>();
		model.put("headers", headers);
		for (HeaderDescriptor descriptor : this.headerDescriptors) {
			headers.add(createModelForDescriptor(descriptor));
		}
		return model;
	}

	private void validateHeaderDocumentation(Operation operation) {
		List<HeaderDescriptor> missingHeaders = findMissingHeaders(operation);
		if (!missingHeaders.isEmpty()) {
			List<String> names = new ArrayList<>();
			for (HeaderDescriptor headerDescriptor : missingHeaders) {
				names.add(headerDescriptor.getName());
			}
			throw new SnippetException("Headers with the following names were not found"
					+ " in the " + this.type + ": " + names);
		}
	}

	/**
	 * Finds the headers that are missing from the operation. A header is missing if it is
	 * described by one of the {@code headerDescriptors} but is not present in the
	 * operation.
	 *
	 * @param operation the operation
	 * @return descriptors for the headers that are missing from the operation
	 */
	protected List<HeaderDescriptor> findMissingHeaders(Operation operation) {
		List<HeaderDescriptor> missingHeaders = new ArrayList<>();
		Set<String> actualHeaders = extractActualHeaders(operation);
		for (HeaderDescriptor headerDescriptor : this.headerDescriptors) {
			if (!headerDescriptor.isOptional()
					&& !actualHeaders.contains(headerDescriptor.getName())) {
				missingHeaders.add(headerDescriptor);
			}
		}

		return missingHeaders;
	}

	/**
	 * Extracts the names of the headers from the request or response of the given
	 * {@code operation}.
	 *
	 * @param operation the operation
	 * @return the header names
	 */
	protected abstract Set<String> extractActualHeaders(Operation operation);

	/**
	 * Returns the list of {@link HeaderDescriptor HeaderDescriptors} that will be used to
	 * generate the documentation.
	 *
	 * @return the header descriptors
	 */
	protected final List<HeaderDescriptor> getHeaderDescriptors() {
		return this.headerDescriptors;
	}

	/**
	 * Returns a model for the given {@code descriptor}.
	 *
	 * @param descriptor the descriptor
	 * @return the model
	 */
	protected Map<String, Object> createModelForDescriptor(HeaderDescriptor descriptor) {
		Map<String, Object> model = new HashMap<>();
		model.put("name", descriptor.getName());
		model.put("description", descriptor.getDescription());
		model.put("optional", descriptor.isOptional());
		model.putAll(descriptor.getAttributes());
		return model;
	}

}
