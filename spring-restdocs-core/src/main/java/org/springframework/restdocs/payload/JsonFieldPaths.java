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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@code JsonFieldPaths} provides support for extracting fields paths from JSON
 * structures and identifying uncommon paths.
 *
 * @author Andy Wilkinson
 */
final class JsonFieldPaths {

	private final Set<String> uncommonFieldPaths;

	private JsonFieldPaths(Set<String> uncommonFieldPaths) {
		this.uncommonFieldPaths = uncommonFieldPaths;
	}

	Set<String> getUncommon() {
		return this.uncommonFieldPaths;
	}

	static JsonFieldPaths from(Collection<?> items) {
		Set<Set<String>> itemsFieldPaths = new HashSet<>();
		Set<String> allFieldPaths = new HashSet<>();
		for (Object item : items) {
			Set<String> paths = new LinkedHashSet<>();
			from(paths, "", item);
			itemsFieldPaths.add(paths);
			allFieldPaths.addAll(paths);
		}
		Set<String> uncommonFieldPaths = new HashSet<>();
		for (Set<String> itemFieldPaths : itemsFieldPaths) {
			Set<String> uncommonForItem = new HashSet<>(allFieldPaths);
			uncommonForItem.removeAll(itemFieldPaths);
			uncommonFieldPaths.addAll(uncommonForItem);
		}
		return new JsonFieldPaths(uncommonFieldPaths);
	}

	private static void from(Set<String> paths, String parent, Object object) {
		if (object instanceof List) {
			String path = append(parent, "[]");
			paths.add(path);
			from(paths, path, (List<?>) object);
		}
		else if (object instanceof Map) {
			from(paths, parent, (Map<?, ?>) object);
		}
	}

	private static void from(Set<String> paths, String parent, List<?> items) {
		for (Object item : items) {
			from(paths, parent, item);
		}
	}

	private static void from(Set<String> paths, String parent, Map<?, ?> map) {
		for (Entry<?, ?> entry : map.entrySet()) {
			String path = append(parent, entry.getKey());
			paths.add(path);
			from(paths, path, entry.getValue());
		}
	}

	private static String append(String path, Object suffix) {
		return (path.length() == 0) ? ("" + suffix) : (path + "." + suffix);
	}

}
