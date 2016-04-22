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
import org.springframework.restdocs.snippet.Snippet;

/**
 * A {@link Snippet} that documents the fields in a response.
 *
 * @author Andy Wilkinson
 * @see PayloadDocumentation#responseFields(FieldDescriptor...)
 * @see PayloadDocumentation#responseFields(Map, FieldDescriptor...)
 */
public class ResponseFieldsSnippet extends AbstractFieldsSnippet {

	/**
	 * Creates a new {@code ResponseFieldsSnippet} that will document the fields in the
	 * response using the given {@code descriptors}. Undocumented fields will trigger a
	 * failure.
	 *
	 * @param descriptors the descriptors
	 */
	protected ResponseFieldsSnippet(List<FieldDescriptor> descriptors) {
		this(descriptors, null, false);
	}

	/**
	 * Creates a new {@code ResponseFieldsSnippet} that will document the fields in the
	 * response using the given {@code descriptors}. If {@code ignoreUndocumentedFields}
	 * is {@code true}, undocumented fields will be ignored and will not trigger a
	 * failure.
	 *
	 * @param descriptors the descriptors
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected ResponseFieldsSnippet(List<FieldDescriptor> descriptors,
			boolean ignoreUndocumentedFields) {
		this(descriptors, null, ignoreUndocumentedFields);
	}

	/**
	 * Creates a new {@code ResponseFieldsSnippet} that will document the fields in the
	 * response using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering. Undocumented fields will trigger a
	 * failure.
	 *
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 */
	protected ResponseFieldsSnippet(List<FieldDescriptor> descriptors,
			Map<String, Object> attributes) {
		this(descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code ResponseFieldsSnippet} that will document the fields in the
	 * response using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected ResponseFieldsSnippet(List<FieldDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedFields) {
		super("response", descriptors, attributes, ignoreUndocumentedFields);
	}

	@Override
	protected MediaType getContentType(Operation operation) {
		return operation.getResponse().getHeaders().getContentType();
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return operation.getResponse().getContent();
	}

	/**
	 * Returns a new {@code ResponseFieldsSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 *
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public ResponseFieldsSnippet and(FieldDescriptor... additionalDescriptors) {
		return andWithPrefix("", additionalDescriptors);
	}

	/**
	 * Returns a new {@code ResponseFieldsSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}. The given {@code pathPrefix} is applied to the path
	 * of each additional descriptor.
	 *
	 * @param pathPrefix the prefix to apply to the additional descriptors
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public ResponseFieldsSnippet andWithPrefix(String pathPrefix,
			FieldDescriptor... additionalDescriptors) {
		List<FieldDescriptor> combinedDescriptors = new ArrayList<>();
		combinedDescriptors.addAll(getFieldDescriptors());
		combinedDescriptors.addAll(
				applyPathPrefix(pathPrefix, Arrays.asList(additionalDescriptors)));
		return new ResponseFieldsSnippet(combinedDescriptors, this.getAttributes());
	}

}
