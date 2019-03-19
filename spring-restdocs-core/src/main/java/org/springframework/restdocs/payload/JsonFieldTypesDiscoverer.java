/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.payload;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.restdocs.payload.JsonFieldPath.PathType;
import org.springframework.restdocs.payload.JsonFieldProcessor.ExtractedField;

/**
 * Discovers the types of the fields found at a path in a JSON request or response
 * payload.
 *
 * @author Andy Wilkinson
 */
class JsonFieldTypesDiscoverer {

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	JsonFieldTypes discoverFieldTypes(String path, Object payload) {
		ExtractedField extractedField = this.fieldProcessor.extract(path, payload);
		Object value = extractedField.getValue();
		if (value instanceof Collection && extractedField.getType() == PathType.MULTI) {
			Collection<?> values = (Collection<?>) value;
			if (allAbsent(values)) {
				throw new FieldDoesNotExistException(path);
			}
			Set<JsonFieldType> fieldTypes = new HashSet<>();
			for (Object item : values) {
				fieldTypes.add(determineFieldType(item));
			}
			return new JsonFieldTypes(fieldTypes);
		}
		if (value == ExtractedField.ABSENT) {
			throw new FieldDoesNotExistException(path);
		}
		return new JsonFieldTypes(determineFieldType(value));
	}

	private JsonFieldType determineFieldType(Object fieldValue) {
		if (fieldValue == null || fieldValue == ExtractedField.ABSENT) {
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

	private boolean allAbsent(Collection<?> values) {
		for (Object value : values) {
			if (value != ExtractedField.ABSENT) {
				return false;
			}
		}
		return true;
	}

}
