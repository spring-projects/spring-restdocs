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

package org.springframework.restdocs.operation.preprocess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link ContentModifier} that modifies the content by replacing occurrences of a
 * regular expression {@link Pattern}.
 *
 * @author Andy Wilkinson
 * @author Dewet Diener
 */
class PatternReplacingContentModifier implements ContentModifier {

	private final Pattern pattern;

	private final String replacement;

	/**
	 * Creates a new {@link PatternReplacingContentModifier} that will replace occurences
	 * the given {@code pattern} with the given {@code replacement}.
	 *
	 * @param pattern the pattern
	 * @param replacement the replacement
	 */
	PatternReplacingContentModifier(Pattern pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}

	@Override
	public byte[] modifyContent(byte[] content) {
		String original = new String(content);
		Matcher matcher = this.pattern.matcher(original);
		StringBuilder buffer = new StringBuilder();
		int previous = 0;
		while (matcher.find()) {
			buffer.append(original.substring(previous, matcher.start(1)));
			buffer.append(this.replacement);
			previous = matcher.end(1);
		}
		if (previous < original.length()) {
			buffer.append(original.substring(previous));
		}
		return buffer.toString().getBytes();
	}

}
