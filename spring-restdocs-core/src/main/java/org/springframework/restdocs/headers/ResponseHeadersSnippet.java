/*
 * Copyright 2014-2016 the original author or authors.
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

package org.springframework.restdocs.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;

/**
 * A {@link Snippet} that documents the headers in a response.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 * @see HeaderDocumentation#responseHeaders(HeaderDescriptor...)
 * @see HeaderDocumentation#responseHeaders(Map, HeaderDescriptor...)
 */
public class ResponseHeadersSnippet extends AbstractHeadersSnippet {

	/**
	 * Creates a new {@code ResponseHeadersSnippet} that will document the headers in the
	 * response using the given {@code descriptors}.
	 *
	 * @param descriptors the descriptors
	 */
	protected ResponseHeadersSnippet(List<HeaderDescriptor> descriptors) {
		this(descriptors, null);
	}

	/**
	 * Creates a new {@code ResponseHeadersSnippet} that will document the headers in the
	 * response using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering.
	 *
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 */
	protected ResponseHeadersSnippet(List<HeaderDescriptor> descriptors,
			Map<String, Object> attributes) {
		super("response", descriptors, attributes);
	}

	@Override
	protected Set<String> extractActualHeaders(Operation operation) {
		return operation.getResponse().getHeaders().keySet();
	}

	/**
	 * Returns a new {@code ResponseHeadersSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final ResponseHeadersSnippet and(HeaderDescriptor... additionalDescriptors) {
		List<HeaderDescriptor> combinedDescriptors = new ArrayList<>();
		combinedDescriptors.addAll(this.getHeaderDescriptors());
		combinedDescriptors.addAll(Arrays.asList(additionalDescriptors));
		return new ResponseHeadersSnippet(combinedDescriptors, getAttributes());
	}

}
