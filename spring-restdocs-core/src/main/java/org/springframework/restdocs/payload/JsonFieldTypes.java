/*
 * Copyright 2014-present the original author or authors.
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
