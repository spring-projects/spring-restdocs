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

package org.springframework.restdocs.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;

/**
 * A {@link Snippet} that documents the request parts supported by a RESTful resource.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 * @see RequestDocumentation#requestParts(RequestPartDescriptor...)
 * @see RequestDocumentation#requestParts(Map, RequestPartDescriptor...)
 * @see RequestDocumentation#relaxedRequestParts(RequestPartDescriptor...)
 * @see RequestDocumentation#relaxedRequestParts(Map, RequestPartDescriptor...)
 */
public class RequestPartsSnippet extends TemplatedSnippet {

	private final Map<String, RequestPartDescriptor> descriptorsByName = new LinkedHashMap<>();

	private final boolean ignoreUndocumentedParts;

	/**
	 * Creates a new {@code RequestPartsSnippet} that will document the request's parts
	 * using the given {@code descriptors}. Undocumented parts will trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 */
	protected RequestPartsSnippet(List<RequestPartDescriptor> descriptors) {
		this(descriptors, null, false);
	}

	/**
	 * Creates a new {@code RequestPartsSnippet} that will document the request's parts
	 * using the given {@code descriptors}. If {@code ignoreUndocumentedParts} is
	 * {@code true}, undocumented parts will be ignored and will not trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 * @param ignoreUndocumentedParts whether undocumented parts should be ignored
	 */
	protected RequestPartsSnippet(List<RequestPartDescriptor> descriptors,
			boolean ignoreUndocumentedParts) {
		this(descriptors, null, ignoreUndocumentedParts);
	}

	/**
	 * Creates a new {@code RequestPartsSnippet} that will document the request's parts
	 * using the given {@code descriptors}. The given {@code attributes} will be included
	 * in the model during template rendering. Undocumented parts will trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 * @param attributes the additional attributes
	 */
	protected RequestPartsSnippet(List<RequestPartDescriptor> descriptors,
			Map<String, Object> attributes) {
		this(descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code RequestPartsSnippet} that will document the request's parts
	 * using the given {@code descriptors}. The given {@code attributes} will be included
	 * in the model during template rendering. If {@code ignoreUndocumentedParts} is
	 * {@code true}, undocumented parts will be ignored and will not trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedParts whether undocumented parts should be ignored
	 */
	protected RequestPartsSnippet(List<RequestPartDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedParts) {
		super("request-parts", attributes);
		for (RequestPartDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getName(),
					"Request part descriptors must have a name");
			if (!descriptor.isIgnored()) {
				Assert.notNull(descriptor.getDescription(),
						"The descriptor for request part '" + descriptor.getName()
								+ "' must either have a description or be marked as "
								+ "ignored");
			}
			this.descriptorsByName.put(descriptor.getName(), descriptor);
		}
		this.ignoreUndocumentedParts = ignoreUndocumentedParts;
	}

	/**
	 * Returns a new {@code RequestPartsSnippet} configured with this snippet's attributes
	 * and its descriptors combined with the given {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestPartsSnippet and(RequestPartDescriptor... additionalDescriptors) {
		return and(Arrays.asList(additionalDescriptors));
	}

	/**
	 * Returns a new {@code RequestPartsSnippet} configured with this snippet's attributes
	 * and its descriptors combined with the given {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestPartsSnippet and(
			List<RequestPartDescriptor> additionalDescriptors) {
		List<RequestPartDescriptor> combinedDescriptors = new ArrayList<>(
				this.descriptorsByName.values());
		combinedDescriptors.addAll(additionalDescriptors);
		return new RequestPartsSnippet(combinedDescriptors, this.getAttributes());
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		verifyRequestPartDescriptors(operation);
		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> requestParts = new ArrayList<>();
		for (Entry<String, RequestPartDescriptor> entry : this.descriptorsByName
				.entrySet()) {
			RequestPartDescriptor descriptor = entry.getValue();
			if (!descriptor.isIgnored()) {
				requestParts.add(createModelForDescriptor(descriptor));
			}
		}
		model.put("requestParts", requestParts);
		return model;
	}

	private void verifyRequestPartDescriptors(Operation operation) {
		Set<String> actualRequestParts = extractActualRequestParts(operation);
		Set<String> expectedRequestParts = new HashSet<>();
		for (Entry<String, RequestPartDescriptor> entry : this.descriptorsByName
				.entrySet()) {
			if (!entry.getValue().isOptional()) {
				expectedRequestParts.add(entry.getKey());
			}
		}
		Set<String> undocumentedRequestParts;
		if (this.ignoreUndocumentedParts) {
			undocumentedRequestParts = Collections.emptySet();
		}
		else {
			undocumentedRequestParts = new HashSet<>(actualRequestParts);
			undocumentedRequestParts.removeAll(this.descriptorsByName.keySet());
		}

		Set<String> missingRequestParts = new HashSet<>(expectedRequestParts);
		missingRequestParts.removeAll(actualRequestParts);

		if (!undocumentedRequestParts.isEmpty() || !missingRequestParts.isEmpty()) {
			verificationFailed(undocumentedRequestParts, missingRequestParts);
		}
	}

	private Set<String> extractActualRequestParts(Operation operation) {
		Set<String> actualRequestParts = new HashSet<>();
		for (OperationRequestPart requestPart : operation.getRequest().getParts()) {
			actualRequestParts.add(requestPart.getName());
		}
		return actualRequestParts;
	}

	private void verificationFailed(Set<String> undocumentedRequestParts,
			Set<String> missingRequestParts) {
		String message = "";
		if (!undocumentedRequestParts.isEmpty()) {
			message += "Request parts with the following names were not documented: "
					+ undocumentedRequestParts;
		}
		if (!missingRequestParts.isEmpty()) {
			if (message.length() > 0) {
				message += ". ";
			}
			message += "Request parts with the following names were not found in "
					+ "the request: " + missingRequestParts;
		}
		throw new SnippetException(message);
	}

	private Map<String, Object> createModelForDescriptor(
			RequestPartDescriptor descriptor) {
		Map<String, Object> model = new HashMap<>();
		model.put("name", descriptor.getName());
		model.put("description", descriptor.getDescription());
		model.put("optional", descriptor.isOptional());
		model.putAll(descriptor.getAttributes());
		return model;
	}

}
