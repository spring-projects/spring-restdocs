/*
 * Copyright 2014-2015 the original author or authors.
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

/**
 * Resolves the type of a field in a JSON request or response payload.
 *
 * @author Andy Wilkinson
 */
class JsonFieldTypeResolver {

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	JsonFieldType resolveFieldType(String path, Object payload) {
		JsonFieldPath fieldPath = JsonFieldPath.compile(path);
		Object field = this.fieldProcessor.extract(fieldPath, payload);
		if (field instanceof Collection && !fieldPath.isPrecise()) {
			JsonFieldType commonType = null;
			for (Object item : (Collection<?>) field) {
				JsonFieldType fieldType = determineFieldType(item);
				if (commonType == null) {
					commonType = fieldType;
				}
				else if (fieldType != commonType) {
					return JsonFieldType.VARIES;
				}
			}
			return commonType;
		}
		return determineFieldType(this.fieldProcessor.extract(fieldPath, payload));
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
