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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;

/**
 * A {@link Snippet} that documents the fields in a request.
 * 
 * @author Andy Wilkinson
 */
class RequestFieldsSnippet extends AbstractFieldsSnippet {

	RequestFieldsSnippet(List<FieldDescriptor> descriptors) {
		this(null, descriptors);
	}

	RequestFieldsSnippet(Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		super("request", attributes, descriptors);
	}

	@Override
	protected MediaType getContentType(Operation operation) {
		return operation.getRequest().getHeaders().getContentType();
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return operation.getRequest().getContent();
	}

}
