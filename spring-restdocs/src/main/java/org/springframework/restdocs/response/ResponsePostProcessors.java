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

package org.springframework.restdocs.response;

import java.util.regex.Pattern;

/**
 * Static factory methods for accessing various {@link ResponsePostProcessor
 * ResponsePostProcessors}.
 * 
 * @author Andy Wilkinson
 * @author Dewet Diener
 */
public abstract class ResponsePostProcessors {

	private ResponsePostProcessors() {

	}

	/**
	 * Returns a {@link ResponsePostProcessor} that will pretty print the content of the
	 * response.
	 * 
	 * @return the response post-processor
	 */
	public static ResponsePostProcessor prettyPrintContent() {
		return new PrettyPrintingResponsePostProcessor();
	}

	/**
	 * Returns a {@link ResponsePostProcessor} that will remove the headers with the given
	 * {@code headerNames} from the response.
	 * 
	 * @param headerNames the name of the headers to remove
	 * @return the response post-processor
	 */
	public static ResponsePostProcessor removeHeaders(String... headerNames) {
		return new HeaderRemovingResponsePostProcessor(headerNames);
	}

	/**
	 * Returns a {@link ResponsePostProcessor} that will update the content of the
	 * response to mask any links that it contains. Each link is masked my replacing its
	 * {@code href} with {@code ...}.
	 * 
	 * @return the response post-processor
	 */
	public static ResponsePostProcessor maskLinks() {
		return new LinkMaskingResponsePostProcessor();
	}

	/**
	 * Returns a {@link ResponsePostProcessor} that will update the content of the
	 * response to mask any links that it contains. Each link is masked my replacing its
	 * {@code href} with the given {@code mask}.
	 * 
	 * @param mask the mask to apply
	 * @return the response post-processor
	 */
	public static ResponsePostProcessor maskLinksWith(String mask) {
		return new LinkMaskingResponsePostProcessor(mask);
	}

	/**
	 * Returns a {@link ResponsePostProcessor} that will update the content of the
	 * response by replacing any occurrences of the given {@code pattern} with the given
	 * {@code replacement}.
	 *
	 * @param pattern the pattern to match
	 * @param replacement the replacement to apply
	 * @return the response post-processor
	 */
	public static ResponsePostProcessor replacePattern(Pattern pattern, String replacement) {
		return new PatternReplacingResponsePostProcessor(pattern, replacement);
	}

}
