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

import java.util.Collection;
import java.util.Map;

import org.springframework.restdocs.payload.JsonFieldPath.PathType;
import org.springframework.restdocs.payload.JsonFieldProcessor.ExtractedField;

/**
 * Resolves the type of a field in a JSON request or response payload.
 *
 * @author Andy Wilkinson
 * @author Mathias Düsterhöft
 */
class JsonFieldTypeResolver implements FieldTypeResolver {

	private final Object content;

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	JsonFieldTypeResolver(Object content) {
		this.content = content;
	}

	@Override
	public Object determineFieldType(FieldDescriptor fieldDescriptor) {
		if (fieldDescriptor.getType() == null) {
			return resolveFieldType(fieldDescriptor);
		}
		if (!(fieldDescriptor.getType() instanceof JsonFieldType)) {
			return fieldDescriptor.getType();
		}
		JsonFieldType descriptorFieldType = (JsonFieldType) fieldDescriptor.getType();
		try {
			JsonFieldType actualFieldType = resolveFieldType(fieldDescriptor);
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

	JsonFieldType resolveFieldType(FieldDescriptor fieldDescriptor) {
		ExtractedField extractedField = this.fieldProcessor
				.extract(fieldDescriptor.getPath(), this.content);
		Object value = extractedField.getValue();
		if (value instanceof Collection && extractedField.getType() == PathType.MULTI) {
			JsonFieldType commonType = null;
			for (Object item : (Collection<?>) value) {
				JsonFieldType fieldType = determineFieldType(item);
				if (commonType == null) {
					commonType = fieldType;
				}
				else if (fieldType != commonType) {
					if (!fieldDescriptor.isOptional()) {
						return JsonFieldType.VARIES;
					}
					if (commonType == JsonFieldType.NULL) {
						commonType = fieldType;
					}
					else if (fieldType != JsonFieldType.NULL) {
						return JsonFieldType.VARIES;
					}
				}
			}
			return commonType;
		}
		return determineFieldType(value);
	}

	private JsonFieldType determineFieldType(Object fieldValue) {
		if (fieldValue == null) {
			return JsonFieldType.NULL;
		}
		if (fieldValue instanceof String) {
			return JsonFieldType.STRING;
		}
		if (fieldValue instanceof Map) {
			return JsonFieldType.OBJECT;
		}
		if (fieldValue instanceof Collection) {
			return JsonFieldType.ARRAY;
		}
		if (fieldValue instanceof Boolean) {
			return JsonFieldType.BOOLEAN;
		}
		return JsonFieldType.NUMBER;
	}

}
