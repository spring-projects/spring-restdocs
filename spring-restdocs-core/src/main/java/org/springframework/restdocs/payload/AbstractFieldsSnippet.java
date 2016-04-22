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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.ModelCreationException;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract {@link TemplatedSnippet} subclass that provides a base for snippets that
 * document a RESTful resource's request or response fields.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public abstract class AbstractFieldsSnippet extends TemplatedSnippet {

	private final List<FieldDescriptor> fieldDescriptors;

	private final boolean ignoreUndocumentedFields;

	/**
	 * Creates a new {@code AbstractFieldsSnippet} that will produce a snippet named
	 * {@code <type>-fields}. The fields will be documented using the given
	 * {@code  descriptors} and the given {@code attributes} will be included in the model
	 * during template rendering. Undocumented fields will trigger a failure.
	 *
	 * @param type the type of the fields
	 * @param descriptors the field descriptors
	 * @param attributes the additional attributes
	 * @deprecated since 1.1 in favor of
	 * {@link #AbstractFieldsSnippet(String, List, Map, boolean)}
	 */
	@Deprecated
	protected AbstractFieldsSnippet(String type, List<FieldDescriptor> descriptors,
			Map<String, Object> attributes) {
		this(type, descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code AbstractFieldsSnippet} that will produce a snippet named
	 * {@code <type>-fields}. The fields will be documented using the given
	 * {@code  descriptors} and the given {@code attributes} will be included in the model
	 * during template rendering. If {@code ignoreUndocumentedFields} is {@code true},
	 * undocumented fields will be ignored and will not trigger a failure.
	 *
	 * @param type the type of the fields
	 * @param descriptors the field descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected AbstractFieldsSnippet(String type, List<FieldDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedFields) {
		super(type + "-fields", attributes);
		for (FieldDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getPath(), "Field descriptors must have a path");
			if (!descriptor.isIgnored()) {
				Assert.notNull(descriptor.getDescription(),
						"The descriptor for field '" + descriptor.getPath()
								+ "' must either have a description or" + " be marked as "
								+ "ignored");
			}

		}
		this.fieldDescriptors = descriptors;
		this.ignoreUndocumentedFields = ignoreUndocumentedFields;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		ContentHandler contentHandler = getContentHandler(operation);

		validateFieldDocumentation(contentHandler);

		for (FieldDescriptor descriptor : this.fieldDescriptors) {
			if (descriptor.getType() == null) {
				descriptor.type(contentHandler.determineFieldType(descriptor.getPath()));
			}
		}

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> fields = new ArrayList<>();
		model.put("fields", fields);
		for (FieldDescriptor descriptor : this.fieldDescriptors) {
			if (!descriptor.isIgnored()) {
				fields.add(createModelForDescriptor(descriptor));
			}
		}
		return model;
	}

	private ContentHandler getContentHandler(Operation operation) {
		MediaType contentType = getContentType(operation);
		ContentHandler contentHandler;
		try {
			if (contentType != null
					&& MediaType.APPLICATION_XML.isCompatibleWith(contentType)) {
				contentHandler = new XmlContentHandler(getContent(operation));
			}
			else {
				contentHandler = new JsonContentHandler(getContent(operation));
			}
		}
		catch (IOException ex) {
			throw new ModelCreationException(ex);
		}
		return contentHandler;
	}

	private void validateFieldDocumentation(ContentHandler payloadHandler) {
		List<FieldDescriptor> missingFields = payloadHandler
				.findMissingFields(this.fieldDescriptors);

		String undocumentedPayload = this.ignoreUndocumentedFields ? null
				: payloadHandler.getUndocumentedContent(this.fieldDescriptors);

		if (!missingFields.isEmpty() || StringUtils.hasText(undocumentedPayload)) {
			String message = "";
			if (StringUtils.hasText(undocumentedPayload)) {
				message += String.format("The following parts of the payload were"
						+ " not documented:%n%s", undocumentedPayload);
			}
			if (!missingFields.isEmpty()) {
				if (message.length() > 0) {
					message += String.format("%n");
				}
				List<String> paths = new ArrayList<>();
				for (FieldDescriptor fieldDescriptor : missingFields) {
					paths.add(fieldDescriptor.getPath());
				}
				message += "Fields with the following paths were not found in the"
						+ " payload: " + paths;
			}
			throw new SnippetException(message);
		}
	}

	/**
	 * Returns the content type of the request or response extracted from the given
	 * {@code operation}.
	 *
	 * @param operation The operation
	 * @return The content type
	 */
	protected abstract MediaType getContentType(Operation operation);

	/**
	 * Returns the content of the request or response extracted form the given
	 * {@code operation}.
	 *
	 * @param operation The operation
	 * @return The content
	 * @throws IOException if the content cannot be extracted
	 */
	protected abstract byte[] getContent(Operation operation) throws IOException;

	/**
	 * Returns the list of {@link FieldDescriptor FieldDescriptors} that will be used to
	 * generate the documentation.
	 *
	 * @return the field descriptors
	 */
	protected final List<FieldDescriptor> getFieldDescriptors() {
		return this.fieldDescriptors;
	}

	/**
	 * Returns a model for the given {@code descriptor}.
	 *
	 * @param descriptor the descriptor
	 * @return the model
	 */
	protected Map<String, Object> createModelForDescriptor(FieldDescriptor descriptor) {
		Map<String, Object> model = new HashMap<>();
		model.put("path", descriptor.getPath());
		model.put("type", descriptor.getType().toString());
		model.put("description", descriptor.getDescription());
		model.put("optional", descriptor.isOptional());
		model.putAll(descriptor.getAttributes());
		return model;
	}

	/**
	 * Creates a copy of the given {@code descriptors} with the given {@code pathPrefix}
	 * applied to their paths.
	 *
	 * @param pathPrefix the path prefix
	 * @param descriptors the descriptors to copy
	 * @return the copied descriptors with the prefix applied
	 */
	protected final List<FieldDescriptor> applyPathPrefix(String pathPrefix,
			List<FieldDescriptor> descriptors) {
		List<FieldDescriptor> prefixedDescriptors = new ArrayList<>();
		for (FieldDescriptor descriptor : descriptors) {
			FieldDescriptor prefixedDescriptor = new FieldDescriptor(
					pathPrefix + descriptor.getPath())
							.description(descriptor.getDescription())
							.type(descriptor.getType());
			if (descriptor.isIgnored()) {
				prefixedDescriptor.ignored();
			}
			if (descriptor.isOptional()) {
				prefixedDescriptor.optional();
			}
			prefixedDescriptors.add(prefixedDescriptor);
		}
		return prefixedDescriptors;
	}
}
