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

package org.springframework.restdocs;

import static org.springframework.restdocs.util.IterableEnumeration.iterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.MockMvcOperationRequestFactory;
import org.springframework.restdocs.operation.MockMvcOperationResponseFactory;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * A Spring MVC Test {@code ResultHandler} for documenting RESTful APIs.
 * 
 * @author Andy Wilkinson
 * @author Andreas Evers
 * @see RestDocumentation#document(String, Snippet...)
 */
public class RestDocumentationResultHandler implements ResultHandler {

	private final String identifier;

	private final List<Snippet> snippets;

	RestDocumentationResultHandler(String identifier, Snippet... snippets) {
		this.identifier = identifier;
		this.snippets = Arrays.asList(snippets);
	}

	@Override
	public void handle(MvcResult result) throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		for (String name : iterable(result.getRequest().getAttributeNames())) {
			attributes.put(name, result.getRequest().getAttribute(name));
		}
		StandardOperation operation = new StandardOperation(this.identifier,
				new MockMvcOperationRequestFactory().createOperationRequest(result
						.getRequest()),
				new MockMvcOperationResponseFactory().createOperationResponse(result
						.getResponse()), attributes);
		for (Snippet snippet : getSnippets(result)) {
			snippet.document(operation);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Snippet> getSnippets(MvcResult result) {
		List<Snippet> combinedSnippets = new ArrayList<>((List<Snippet>) result
				.getRequest()
				.getAttribute("org.springframework.restdocs.defaultSnippets"));
		combinedSnippets.addAll(this.snippets);
		return combinedSnippets;
	}

}
