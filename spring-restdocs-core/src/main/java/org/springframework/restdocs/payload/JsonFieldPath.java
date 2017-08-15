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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A path that identifies a field in a JSON payload.
 *
 * @author Andy Wilkinson
 * @author Jeremy Rickard
 * @author Minhyeok Jeong
 */
final class JsonFieldPath {

	private static final Pattern BRACKETS_AND_ARRAY_PATTERN = Pattern
			.compile("\\[\'(.+?)\'\\]|\\[([0-9]+|\\*){0,1}\\]");

	private static final Pattern ARRAY_INDEX_PATTERN = Pattern
			.compile("\\[([0-9]+|\\*){0,1}\\]");

	private final String rawPath;

	private final List<String> segments;

	/**
	 * Whether this path indicates a specific value which is neither an array nor an
	 * object.
	 */
	private final boolean precise;

	/** Whether this path indicates a value which is an array. */
	private final boolean array;

	private JsonFieldPath(String rawPath, List<String> segments, boolean precise,
			boolean array) {
		this.rawPath = rawPath;
		this.segments = segments;
		this.precise = precise;
		this.array = array;
	}

	boolean isPrecise() {
		return this.precise;
	}

	boolean isArray() {
		return this.array;
	}

	List<String> getSegments() {
		return this.segments;
	}

	@Override
	public String toString() {
		return this.rawPath;
	}

	static JsonFieldPath compile(String path) {
		List<String> segments = extractSegments(path);
		String leafSegment = segments.get(segments.size() - 1);
		return new JsonFieldPath(path, segments, matchesSingleValue(segments),
				isArraySegment(leafSegment) || isWildcardSegment(leafSegment));
	}

	static boolean isArraySegment(String segment) {
		return ARRAY_INDEX_PATTERN.matcher(segment).matches();
	}

	private static boolean matchesSingleValue(List<String> segments) {
		for (String segment : segments) {
			if (isArraySegment(segment) || isWildcardSegment(segment)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isWildcardSegment(String segment) {
		return "*".equals(segment);
	}

	private static List<String> extractSegments(String path) {
		Matcher matcher = BRACKETS_AND_ARRAY_PATTERN.matcher(path);

		int previous = 0;

		List<String> segments = new ArrayList<>();
		while (matcher.find()) {
			if (previous != matcher.start()) {
				segments.addAll(extractDotSeparatedSegments(
						path.substring(previous, matcher.start())));
			}
			if (matcher.group(1) != null) {
				segments.add(matcher.group(1));
			}
			else {
				segments.add(matcher.group());
			}
			previous = matcher.end(0);
		}

		if (previous < path.length()) {
			segments.addAll(extractDotSeparatedSegments(path.substring(previous)));
		}

		return segments;
	}

	private static List<String> extractDotSeparatedSegments(String path) {
		List<String> segments = new ArrayList<>();
		for (String segment : path.split("\\.")) {
			if (segment.length() > 0) {
				segments.add(segment);
			}
		}
		return segments;
	}
}
