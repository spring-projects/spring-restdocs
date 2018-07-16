/*
 * Copyright 2014-2018 the original author or authors.
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
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Attributes.Attribute;
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

	private final String type;

	private final PayloadSubsectionExtractor<?> subsectionExtractor;

	/**
	 * Creates a new {@code AbstractFieldsSnippet} that will produce a snippet named
	 * {@code <type>-fields} using a template named {@code <type>-fields}. The fields will
	 * be documented using the given {@code  descriptors} and the given {@code attributes}
	 * will be included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param type the type of the fields
	 * @param descriptors the field descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected AbstractFieldsSnippet(String type, List<FieldDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedFields) {
		this(type, type, descriptors, attributes, ignoreUndocumentedFields);
	}

	/**
	 * Creates a new {@code AbstractFieldsSnippet} that will produce a snippet named
	 * {@code <type>-fields} using a template named {@code <type>-fields}. The fields in
	 * the subsection of the payload extracted by the given {@code subsectionExtractor}
	 * will be documented using the given {@code  descriptors} and the given
	 * {@code attributes} will be included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param type the type of the fields
	 * @param descriptors the field descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 * @param subsectionExtractor the subsection extractor
	 * @since 1.2.0
	 */
	protected AbstractFieldsSnippet(String type, List<FieldDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedFields,
			PayloadSubsectionExtractor<?> subsectionExtractor) {
		this(type, type, descriptors, attributes, ignoreUndocumentedFields,
				subsectionExtractor);
	}

	/**
	 * Creates a new {@code AbstractFieldsSnippet} that will produce a snippet named
	 * {@code <name>-fields} using a template named {@code <type>-fields}. The fields will
	 * be documented using the given {@code  descriptors} and the given {@code attributes}
	 * will be included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param name the name of the snippet
	 * @param type the type of the fields
	 * @param descriptors the field descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 */
	protected AbstractFieldsSnippet(String name, String type,
			List<FieldDescriptor> descriptors, Map<String, Object> attributes,
			boolean ignoreUndocumentedFields) {
		this(name, type, descriptors, attributes, ignoreUndocumentedFields, null);
	}

	/**
	 * Creates a new {@code AbstractFieldsSnippet} that will produce a snippet named
	 * {@code <name>-fields} using a template named {@code <type>-fields}. The fields in
	 * the subsection of the payload identified by {@code subsectionPath} will be
	 * documented using the given {@code  descriptors} and the given {@code attributes}
	 * will be included in the model during template rendering. If
	 * {@code ignoreUndocumentedFields} is {@code true}, undocumented fields will be
	 * ignored and will not trigger a failure.
	 *
	 * @param name the name of the snippet
	 * @param type the type of the fields
	 * @param descriptors the field descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedFields whether undocumented fields should be ignored
	 * @param subsectionExtractor the subsection extractor documented. {@code null} or an
	 * empty string can be used to indicate that the entire payload should be documented.
	 * @since 1.2.0
	 */
	protected AbstractFieldsSnippet(String name, String type,
			List<FieldDescriptor> descriptors, Map<String, Object> attributes,
			boolean ignoreUndocumentedFields,
			PayloadSubsectionExtractor<?> subsectionExtractor) {
		super(name + "-fields"
				+ (subsectionExtractor != null
						? "-" + subsectionExtractor.getSubsectionId() : ""),
				type + "-fields", attributes);
		for (FieldDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getPath(), "Field descriptors must have a path");
			if (!descriptor.isIgnored()) {
				Assert.notNull(descriptor.getDescription() != null,
						"The descriptor for '" + descriptor.getPath() + "' must have a"
								+ " description or it must be marked as ignored");
			}
		}
		this.fieldDescriptors = descriptors;
		this.ignoreUndocumentedFields = ignoreUndocumentedFields;
		this.type = type;
		this.subsectionExtractor = subsectionExtractor;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		byte[] content;
		try {
			content = verifyContent(getContent(operation));
		}
		catch (IOException ex) {
			throw new ModelCreationException(ex);
		}
		MediaType contentType = getContentType(operation);
		if (this.subsectionExtractor != null) {
			content = verifyContent(
					this.subsectionExtractor.extractSubsection(content, contentType));
		}
		ContentHandler contentHandler = getContentHandler(content, contentType);

		validateFieldDocumentation(contentHandler);

		List<FieldDescriptor> descriptorsToDocument = new ArrayList<>();
		for (FieldDescriptor descriptor : this.fieldDescriptors) {
			if (!descriptor.isIgnored()) {
				try {
					Object type = contentHandler.determineFieldType(descriptor);
					descriptorsToDocument.add(copyWithType(descriptor, type));
				}
				catch (FieldDoesNotExistException ex) {
					String message = "Cannot determine the type of the field '"
							+ descriptor.getPath() + "' as it is not present in the "
							+ "payload. Please provide a type using "
							+ "FieldDescriptor.type(Object type).";
					throw new FieldTypeRequiredException(message);
				}
			}
		}

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> fields = new ArrayList<>();
		model.put("fields", fields);
		for (FieldDescriptor descriptor : descriptorsToDocument) {
			if (!descriptor.isIgnored()) {
				fields.add(createModelForDescriptor(descriptor));
			}
		}
		return model;
	}

	private byte[] verifyContent(byte[] content) {
		if (content.length == 0) {
			throw new SnippetException("Cannot document " + this.type + " fields as the "
					+ this.type + " body is empty");
		}
		return content;
	}

	private ContentHandler getContentHandler(byte[] content, MediaType contentType) {
		ContentHandler contentHandler = createJsonContentHandler(content);
		if (contentHandler == null) {
			contentHandler = createXmlContentHandler(content);
			if (contentHandler == null) {
				throw new PayloadHandlingException("Cannot handle " + contentType
						+ " content as it could not be parsed as JSON or XML");
			}
		}
		return contentHandler;
	}

	private ContentHandler createJsonContentHandler(byte[] content) {
		try {
			return new JsonContentHandler(content);
		}
		catch (Exception ex) {
			return null;
		}
	}

	private ContentHandler createXmlContentHandler(byte[] content) {
		try {
			return new XmlContentHandler(content);
		}
		catch (Exception ex) {
			return null;
		}
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
	 * Returns whether or not this snippet ignores undocumented fields.
	 *
	 * @return {@code true} if undocumented fields are ignored, otherwise {@code false}
	 */
	protected final boolean isIgnoredUndocumentedFields() {
		return this.ignoreUndocumentedFields;
	}

	/**
	 * Returns the {@link PayloadSubsectionExtractor}, if any, used by this snippet.
	 *
	 * @return the subsection extractor or {@code null}
	 * @since 1.2.4
	 */
	protected final PayloadSubsectionExtractor<?> getSubsectionExtractor() {
		return this.subsectionExtractor;
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

	private FieldDescriptor copyWithType(FieldDescriptor source, Object type) {
		FieldDescriptor result = source instanceof SubsectionDescriptor
				? new SubsectionDescriptor(source.getPath())
				: new FieldDescriptor(source.getPath());
		result.description(source.getDescription()).type(type)
				.attributes(asArray(source.getAttributes()));
		if (source.isIgnored()) {
			result.ignored();
		}
		if (source.isOptional()) {
			result.optional();
		}
		return result;
	}

	private static Attribute[] asArray(Map<String, Object> attributeMap) {
		List<Attributes.Attribute> attributes = new ArrayList<>();
		for (Map.Entry<String, Object> attribute : attributeMap.entrySet()) {
			attributes
					.add(Attributes.key(attribute.getKey()).value(attribute.getValue()));
		}
		return attributes.toArray(new Attribute[attributes.size()]);
	}

}
