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

package org.springframework.restdocs.state;

import static org.springframework.restdocs.state.FieldSnippetResultHandler.Type.REQUEST;
import static org.springframework.restdocs.state.FieldSnippetResultHandler.Type.RESPONSE;
import static org.springframework.restdocs.state.Path.path;

import java.util.Arrays;

import org.springframework.restdocs.RestDocumentationResultHandler;

/**
 * Static factory methods for documenting a RESTful API's state.
 * 
 * @author Andreas Evers
 */
public abstract class StateDocumentation {

	private StateDocumentation() {

	}

	/**
	 * Creates a {@code FieldDescriptor} that describes a field with the given
	 * {@code path}.
	 * 
	 * @param path The path of the field
	 * @return a {@code FieldDescriptor} ready for further configuration
	 * @see RestDocumentationResultHandler#withRequestFields(FieldDescriptor...)
	 * @see RestDocumentationResultHandler#withResponseFields(FieldDescriptor...)
	 */
	public static FieldDescriptor fieldWithPath(Path path) {
		return new FieldDescriptor(path);
	}

	/**
	 * Creates a {@code FieldDescriptor} that describes a field with the given
	 * {@code path}, in case the field is at the root of the request or response body
	 * 
	 * @param name The name of the field being at the root of the request or response body
	 * @return a {@code FieldDescriptor} ready for further configuration
	 * @see RestDocumentationResultHandler#withRequestFields(FieldDescriptor...)
	 * @see RestDocumentationResultHandler#withResponseFields(FieldDescriptor...)
	 */
	public static FieldDescriptor fieldWithPath(String name) {
		return new FieldDescriptor(path(name));
	}

	/**
	 * Creates a {@code RequestFieldsSnippetResultHandler} that will produce a
	 * documentation snippet for a request's fields.
	 * 
	 * @param outputDir The directory to which the snippet should be written
	 * @param descriptors The descriptions of the request's fields
	 * @return the handler
	 * @see RestDocumentationResultHandler#withRequestFields(FieldDescriptor...)
	 */
	public static FieldSnippetResultHandler documentRequestFields(String outputDir,
			FieldDescriptor... descriptors) {
		return new FieldSnippetResultHandler(outputDir, REQUEST,
				Arrays.asList(descriptors));
	}

	/**
	 * Creates a {@code ResponseFieldsSnippetResultHandler} that will produce a
	 * documentation snippet for a response's fields.
	 * 
	 * @param outputDir The directory to which the snippet should be written
	 * @param descriptors The descriptions of the response's fields
	 * @return the handler
	 * @see RestDocumentationResultHandler#withResponseFields(FieldDescriptor...)
	 */
	public static FieldSnippetResultHandler documentResponseFields(String outputDir,
			FieldDescriptor... descriptors) {
		return new FieldSnippetResultHandler(outputDir, RESPONSE,
				Arrays.asList(descriptors));
	}

}
