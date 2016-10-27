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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;

/**
 * A {@link Snippet} that documents the fields in a request part.
 *
 * @author Mathieu Pousse
 * @author Andy Wilkinson
 * @since 1.2.0
 * @see PayloadDocumentation#requestPartFields(String, FieldDescriptor...)
 * @see PayloadDocumentation#requestPartFields(String, List)
 */
public class RequestPartFieldsSnippet extends AbstractFieldsSnippet {

	private final String partName;

	/**
	 * Creates a new {@code RequestPartFieldsSnippet} that will document the fields in the
	 * request part using the given {@code descriptors}. Undocumented fields will trigger
	 * a failure.
	 *
	 * @param partName the part name
	 * @param descriptors the descriptors
	 */
	protected RequestPartFieldsSnippet(String partName,
			List<FieldDescriptor> descriptors) {
		this(partName, descriptors, null, false);
	}

	/**
	 * Creates a new {@code RequestPartFieldsSnippet} that will document the fields in the
	 * request part using the given {@code descriptors}. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param partName the part name
	 * @param descriptors the descriptors
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected RequestPartFieldsSnippet(String partName, List<FieldDescriptor> descriptors,
			boolean ignoreUndocumentedFields) {
		this(partName, descriptors, null, ignoreUndocumentedFields);
	}

	/**
	 * Creates a new {@code RequestFieldsSnippet} that will document the fields in the
	 * request using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering. Undocumented fields will trigger a
	 * failure.
	 *
	 * @param partName the part name
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 */
	protected RequestPartFieldsSnippet(String partName, List<FieldDescriptor> descriptors,
			Map<String, Object> attributes) {
		this(partName, descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code RequestFieldsSnippet} that will document the fields in the
	 * request using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param partName the part name
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected RequestPartFieldsSnippet(String partName, List<FieldDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedFields) {
		this(partName, null, descriptors, attributes, ignoreUndocumentedFields);
	}

	/**
	 * Creates a new {@code RequestPartFieldsSnippet} that will document the fields in a
	 * subsection of the request part using the given {@code descriptors}. The subsection
	 * will be extracted using the given {@code subsectionExtractor}. Undocumented fields
	 * will trigger a failure.
	 *
	 * @param partName the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptors
	 */
	protected RequestPartFieldsSnippet(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		this(partName, subsectionExtractor, descriptors, null, false);
	}

	/**
	 * Creates a new {@code RequestPartFieldsSnippet} that will document the fields in a
	 * subsection the request part using the given {@code descriptors}. The subsection
	 * will be extracted using the given {@code subsectionExtractor}. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param partName the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptors
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected RequestPartFieldsSnippet(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors, boolean ignoreUndocumentedFields) {
		this(partName, subsectionExtractor, descriptors, null, ignoreUndocumentedFields);
	}

	/**
	 * Creates a new {@code RequestPartFieldsSnippet} that will document the fields in a
	 * subsection of the request part using the given {@code descriptors}. The subsection
	 * will be extracted using the given {@code subsectionExtractor}. The given
	 * {@code attributes} will be included in the model during template rendering.
	 * Undocumented fields will trigger a failure.
	 *
	 * @param partName the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 */
	protected RequestPartFieldsSnippet(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors, Map<String, Object> attributes) {
		this(partName, subsectionExtractor, descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code RequestPartFieldsSnippet} that will document the fields in a
	 * subsection of the request part using the given {@code descriptors}. The subsection
	 * will be extracted using the given {@code subsectionExtractor}. The given
	 * {@code attributes} will be included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param partName the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected RequestPartFieldsSnippet(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors, Map<String, Object> attributes,
			boolean ignoreUndocumentedFields) {
		super("request-part-" + partName, "request-part", descriptors, attributes,
				ignoreUndocumentedFields, subsectionExtractor);
		this.partName = partName;
	}

	@Override
	protected MediaType getContentType(Operation operation) {
		return findPart(operation).getHeaders().getContentType();
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return findPart(operation).getContent();
	}

	private OperationRequestPart findPart(Operation operation) {
		for (OperationRequestPart candidate : operation.getRequest().getParts()) {
			if (candidate.getName().equals(this.partName)) {
				return candidate;
			}
		}
		throw new SnippetException("A request part named '" + this.partName
				+ "' was not found in the request");
	}

	/**
	 * Returns a new {@code RequestPartFieldsSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 *
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestPartFieldsSnippet and(FieldDescriptor... additionalDescriptors) {
		return andWithPrefix("", additionalDescriptors);
	}

	/**
	 * Returns a new {@code RequestPartFieldsSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 *
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestPartFieldsSnippet and(
			List<FieldDescriptor> additionalDescriptors) {
		return andWithPrefix("", additionalDescriptors);
	}

	/**
	 * Returns a new {@code RequestFieldsSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}. The given {@code pathPrefix} is applied to the path
	 * of each additional descriptor.
	 *
	 * @param pathPrefix the prefix to apply to the additional descriptors
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestPartFieldsSnippet andWithPrefix(String pathPrefix,
			FieldDescriptor... additionalDescriptors) {
		List<FieldDescriptor> combinedDescriptors = new ArrayList<>();
		combinedDescriptors.addAll(getFieldDescriptors());
		combinedDescriptors.addAll(PayloadDocumentation.applyPathPrefix(pathPrefix,
				Arrays.asList(additionalDescriptors)));
		return new RequestPartFieldsSnippet(this.partName, combinedDescriptors,
				this.getAttributes());
	}

	/**
	 * Returns a new {@code RequestFieldsSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}. The given {@code pathPrefix} is applied to the path
	 * of each additional descriptor.
	 *
	 * @param pathPrefix the prefix to apply to the additional descriptors
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestPartFieldsSnippet andWithPrefix(String pathPrefix,
			List<FieldDescriptor> additionalDescriptors) {
		List<FieldDescriptor> combinedDescriptors = new ArrayList<>(
				getFieldDescriptors());
		combinedDescriptors.addAll(
				PayloadDocumentation.applyPathPrefix(pathPrefix, additionalDescriptors));
		return new RequestPartFieldsSnippet(this.partName, combinedDescriptors,
				this.getAttributes());
	}

}
