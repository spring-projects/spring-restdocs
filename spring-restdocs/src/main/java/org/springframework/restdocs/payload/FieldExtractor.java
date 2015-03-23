package org.springframework.restdocs.payload;

import java.util.Map;

/**
 * A {@link FieldExtractor} extracts a field from a payload
 * 
 * @author Andy Wilkinson
 *
 */
class FieldExtractor {

	boolean hasField(String path, Map<String, Object> payload) {
		String[] segments = path.indexOf('.') > -1 ? path.split("\\.")
				: new String[] { path };

		Object current = payload;

		for (String segment : segments) {
			if (current instanceof Map && ((Map<?, ?>) current).containsKey(segment)) {
				current = ((Map<?, ?>) current).get(segment);
			}
			else {
				return false;
			}
		}

		return true;
	}

	Object extractField(String path, Map<String, Object> payload) {
		String[] segments = path.indexOf('.') > -1 ? path.split("\\.")
				: new String[] { path };

		Object current = payload;

		for (String segment : segments) {
			if (current instanceof Map && ((Map<?, ?>) current).containsKey(segment)) {
				current = ((Map<?, ?>) current).get(segment);
			}
			else {
				throw new IllegalArgumentException(
						"The payload does not contain a field with the path '" + path
								+ "'");
			}
		}

		return current;

	}

}
