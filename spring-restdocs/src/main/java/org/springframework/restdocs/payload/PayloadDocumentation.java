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

import java.util.Arrays;

import org.springframework.restdocs.RestDocumentationResultHandler;

/**
 * Static factory methods for documenting a RESTful API's request and response payloads.
 * 
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public abstract class PayloadDocumentation {

	private PayloadDocumentation() {

	}

	/**
	 * Creates a {@code FieldDescriptor} that describes a field with the given
	 * {@code path}.
	 * <p>
	 * The {@code path} uses '.' to descend into a child object and ' {@code []}' to
	 * descend into an array. For example, with this JSON payload:
	 * 
	 * <pre>
	 * {
     *    "a":{
     *        "b":[
     *            {
     *                "c":"one"
     *            },
     *            {
     *                "c":"two"
     *            },
     *            {
     *                "d":"three"
     *            }
     *        ]
     *    }
     * }
	 * </pre>
	 * 
	 * The following paths are all present:
	 * 
	 * <table summary="Paths and their values">
	 * <tr>
	 * <th>Path</th>
	 * <th>Value</th>
	 * </tr>
	 * <tr>
	 * <td>{@code a}</td>
	 * <td>An object containing "b"</td>
	 * </tr>
	 * <tr>
	 * <td>{@code a.b}</td>
	 * <td>An array containing three objects</td>
	 * </tr>
	 * <tr>
	 * <td>{@code a.b[]}</td>
	 * <td>An array containing three objects</td>
	 * </tr>
	 * <tr>
	 * <td>{@code a.b[].c}</td>
	 * <td>An array containing the strings "one" and "two"</td>
	 * </tr>
	 * <tr>
	 * <td>{@code a.b[].d}</td>
	 * <td>The string "three"</td>
	 * </tr>
	 * </table>
	 * 
	 * @param path The path of the field
	 * @return a {@code FieldDescriptor} ready for further configuration
	 * @see RestDocumentationResultHandler#withRequestFields(FieldDescriptor...)
	 * @see RestDocumentationResultHandler#withResponseFields(FieldDescriptor...)
	 */
	public static FieldDescriptor fieldWithPath(String path) {
		return new FieldDescriptor(path);
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
		return new RequestFieldSnippetResultHandler(outputDir, Arrays.asList(descriptors));
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
		return new ResponseFieldSnippetResultHandler(outputDir,
				Arrays.asList(descriptors));
	}

}
