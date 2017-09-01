/*
 * Copyright 2014-2017 the original author or authors.
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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link ContentHandler} for JSON content.
 *
 * @author Andy Wilkinson
 */
class JsonContentHandler implements ContentHandler {

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	private final JsonFieldTypeResolver fieldTypeResolver = new JsonFieldTypeResolver();

	private final ObjectMapper objectMapper = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT);

	private final byte[] rawContent;

	JsonContentHandler(byte[] content) {
		this.rawContent = content;
		readContent();
	}

	@Override
	public List<FieldDescriptor> findMissingFields(
			List<FieldDescriptor> fieldDescriptors) {
		List<FieldDescriptor> missingFields = new ArrayList<>();
		Object payload = readContent();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			if (!fieldDescriptor.isOptional() && !this.fieldProcessor
					.hasField(fieldDescriptor.getPath(), payload)) {
				missingFields.add(fieldDescriptor);
			}
		}

		return missingFields;
	}

	@Override
	public String getUndocumentedContent(List<FieldDescriptor> fieldDescriptors) {
		Object content = readContent();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			if (describesSubsection(fieldDescriptor)) {
				this.fieldProcessor.removeSubsection(fieldDescriptor.getPath(), content);
			}
			else {
				this.fieldProcessor.remove(fieldDescriptor.getPath(), content);
			}
		}
		if (!isEmpty(content)) {
			try {
				return this.objectMapper.writeValueAsString(content);
			}
			catch (JsonProcessingException ex) {
				throw new PayloadHandlingException(ex);
			}
		}
		return null;
	}

	private boolean describesSubsection(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor instanceof SubsectionDescriptor;
	}

	private Object readContent() {
		try {
			return new ObjectMapper().readValue(this.rawContent, Object.class);
		}
		catch (IOException ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private boolean isEmpty(Object object) {
		if (object instanceof Map) {
			return ((Map<?, ?>) object).isEmpty();
		}
		return ((List<?>) object).isEmpty();
	}

	@Override
	public Object determineFieldType(FieldDescriptor fieldDescriptor) {
		if (fieldDescriptor.getType() == null) {
			return this.fieldTypeResolver.resolveFieldType(fieldDescriptor,
					readContent());
		}
		if (!(fieldDescriptor.getType() instanceof JsonFieldType)) {
			return fieldDescriptor.getType();
		}
		JsonFieldType descriptorFieldType = (JsonFieldType) fieldDescriptor.getType();
		try {
			JsonFieldType actualFieldType = this.fieldTypeResolver
					.resolveFieldType(fieldDescriptor, readContent());
			if (descriptorFieldType == JsonFieldType.VARIES
					|| descriptorFieldType == actualFieldType
					|| (fieldDescriptor.isOptional()
							&& actualFieldType == JsonFieldType.NULL)) {
				return descriptorFieldType;
			}
			throw new FieldTypesDoNotMatchException(fieldDescriptor, actualFieldType);
		}
		catch (FieldDoesNotExistException ex) {
			return fieldDescriptor.getType();
		}
	}

}
