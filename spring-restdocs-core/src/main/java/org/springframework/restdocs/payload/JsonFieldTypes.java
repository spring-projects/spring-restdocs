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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * {@link JsonFieldType Types} for a field discovered in a JSON payload.
 *
 * @author Andy Wilkinson
 */
class JsonFieldTypes implements Iterable<JsonFieldType> {

	private final Set<JsonFieldType> fieldTypes;

	JsonFieldTypes(JsonFieldType fieldType) {
		this(Collections.singleton(fieldType));
	}

	JsonFieldTypes(Set<JsonFieldType> fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	JsonFieldType coalesce(boolean optional) {
		Set<JsonFieldType> types = new HashSet<>(this.fieldTypes);
		if (optional && types.size() > 1) {
			types.remove(JsonFieldType.NULL);
		}
		if (types.size() == 1) {
			return types.iterator().next();
		}
		return JsonFieldType.VARIES;
	}

	@Override
	public Iterator<JsonFieldType> iterator() {
		return this.fieldTypes.iterator();
	}

}
