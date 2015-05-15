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
 * Resolves the type of a field in a request or response payload
 * 
 * @author Andy Wilkinson
 */
class FieldTypeResolver {

	private final FieldProcessor fieldProcessor = new FieldProcessor();

	FieldType resolveFieldType(String path, Object payload) {
		FieldPath fieldPath = FieldPath.compile(path);
		Object field = this.fieldProcessor.extract(fieldPath, payload);
		if (field instanceof Collection && !fieldPath.isPrecise()) {
			FieldType commonType = null;
			for (Object item : (Collection<?>) field) {
				FieldType fieldType = determineFieldType(item);
				if (commonType == null) {
					commonType = fieldType;
				}
				else if (fieldType != commonType) {
					return FieldType.VARIES;
				}
			}
			return commonType;
		}
		return determineFieldType(this.fieldProcessor.extract(fieldPath, payload));
	}

	private FieldType determineFieldType(Object fieldValue) {
		if (fieldValue == null) {
			return FieldType.NULL;
		}
		if (fieldValue instanceof String) {
			return FieldType.STRING;
		}
		if (fieldValue instanceof Map) {
			return FieldType.OBJECT;
		}
		if (fieldValue instanceof Collection) {
			return FieldType.ARRAY;
		}
		if (fieldValue instanceof Boolean) {
			return FieldType.BOOLEAN;
		}
		return FieldType.NUMBER;
	}
}
