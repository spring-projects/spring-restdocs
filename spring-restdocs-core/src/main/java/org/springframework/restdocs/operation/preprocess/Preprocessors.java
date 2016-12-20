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

import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * Static factory methods for creating {@link OperationPreprocessor
 * OperationPreprocessors} that can be applied to an {@link Operation Operation's}
 * {@link OperationRequest request} or {@link OperationResponse response} before it is
 * documented.
 *
 * @author Andy Wilkinson
 * @author Roland Huss
 */
public final class Preprocessors {

	private Preprocessors() {

	}

	/**
	 * Returns an {@link OperationRequestPreprocessor} that will preprocess the request by
	 * applying the given {@code preprocessors} to it.
	 *
	 * @param preprocessors the preprocessors
	 * @return the request preprocessor
	 */
	public static OperationRequestPreprocessor preprocessRequest(
			OperationPreprocessor... preprocessors) {
		return new DelegatingOperationRequestPreprocessor(Arrays.asList(preprocessors));
	}

	/**
	 * Returns an {@link OperationResponsePreprocessor} that will preprocess the response
	 * by applying the given {@code preprocessors} to it.
	 *
	 * @param preprocessors the preprocessors
	 * @return the response preprocessor
	 */
	public static OperationResponsePreprocessor preprocessResponse(
			OperationPreprocessor... preprocessors) {
		return new DelegatingOperationResponsePreprocessor(Arrays.asList(preprocessors));
	}

	/**
	 * Returns an {@code OperationPreprocessor} that will pretty print the content of the
	 * request or response.
	 *
	 * @return the preprocessor
	 */
	public static OperationPreprocessor prettyPrint() {
		return new ContentModifyingOperationPreprocessor(
				new PrettyPrintingContentModifier());
	}

	/**
	 * Returns an {@code OperationPreprocessor} that will remove any header from the
	 * request or response with a name that is equal to one of the given
	 * {@code headersToRemove}.
	 *
	 * @param headerNames the header names
	 * @return the preprocessor
	 * @see String#equals(Object)
	 */
	public static OperationPreprocessor removeHeaders(String... headerNames) {
		return new HeaderRemovingOperationPreprocessor(
				new ExactMatchHeaderFilter(headerNames));
	}

	/**
	 * Returns an {@code OperationPreprocessor} that will remove any headers from the
	 * request or response with a name that matches one of the given
	 * {@code headerNamePatterns} regular expressions.
	 *
	 * @param headerNamePatterns the header name patterns
	 * @return the preprocessor
	 * @see java.util.regex.Matcher#matches()
	 */
	public static OperationPreprocessor removeMatchingHeaders(
			String... headerNamePatterns) {
		return new HeaderRemovingOperationPreprocessor(
				new PatternMatchHeaderFilter(headerNamePatterns));
	}

	/**
	 * Returns an {@code OperationPreprocessor} that will mask the href of hypermedia
	 * links in the request or response.
	 *
	 * @return the preprocessor
	 */
	public static OperationPreprocessor maskLinks() {
		return new ContentModifyingOperationPreprocessor(
				new LinkMaskingContentModifier());
	}

	/**
	 * Returns an {@code OperationPreprocessor} that will mask the href of hypermedia
	 * links in the request or response.
	 *
	 * @param mask the link mask
	 * @return the preprocessor
	 */
	public static OperationPreprocessor maskLinks(String mask) {
		return new ContentModifyingOperationPreprocessor(
				new LinkMaskingContentModifier(mask));
	}

	/**
	 * Returns an {@code OperationPreprocessor} that will modify the content of the
	 * request or response by replacing occurrences of the given {@code pattern} with the
	 * given {@code replacement}.
	 *
	 * @param pattern the pattern
	 * @param replacement the replacement
	 * @return the preprocessor
	 */
	public static OperationPreprocessor replacePattern(Pattern pattern,
			String replacement) {
		return new ContentModifyingOperationPreprocessor(
				new PatternReplacingContentModifier(pattern, replacement));
	}

	/**
	 * Returns a {@code ParametersModifyingOperationPreprocessor} that can then be
	 * configured to modify the parameters of the request.
	 *
	 * @return the preprocessor
	 * @since 1.1.0
	 */
	public static ParametersModifyingOperationPreprocessor modifyParameters() {
		return new ParametersModifyingOperationPreprocessor();
	}

}
