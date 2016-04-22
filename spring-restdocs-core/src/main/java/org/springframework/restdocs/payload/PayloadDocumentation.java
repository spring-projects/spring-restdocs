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

package org.springframework.restdocs.payload;

import java.util.Arrays;
import java.util.Map;

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
	 * When documenting an XML payload, the {@code path} uses XPath, i.e. '/' is used to
	 * descend to a child node.
	 * <p>
	 * When documenting a JSON payload, the {@code path} uses '.' to descend into a child
	 * object and ' {@code []}' to descend into an array. For example, with this JSON
	 * payload:
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
	 */
	public static FieldDescriptor fieldWithPath(String path) {
		return new FieldDescriptor(path);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operations's
	 * request payload. The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the request payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field is sufficient for all of its descendants to also be treated as having been
	 * documented.
	 * <p>
	 * If you do not want to document a field, a field descriptor can be marked as
	 * {@link FieldDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 *
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static RequestFieldsSnippet requestFields(FieldDescriptor... descriptors) {
		return new RequestFieldsSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operations's
	 * request payload. The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			FieldDescriptor... descriptors) {
		return new RequestFieldsSnippet(Arrays.asList(descriptors), true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * request payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the request payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field is sufficient for all of its descendants to also be treated as
	 * having been documented.
	 * <p>
	 * If you do not want to document a field, a field descriptor can be marked as
	 * {@link FieldDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static RequestFieldsSnippet requestFields(Map<String, Object> attributes,
			FieldDescriptor... descriptors) {
		return new RequestFieldsSnippet(Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * request payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return new RequestFieldsSnippet(Arrays.asList(descriptors), attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field is sufficient for all of its descendants to also be treated as
	 * having been documented.
	 * <p>
	 * If you do not want to document a field, a field descriptor can be marked as
	 * {@link FieldDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 *
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
		return new ResponseFieldsSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			FieldDescriptor... descriptors) {
		return new ResponseFieldsSnippet(Arrays.asList(descriptors), true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field is sufficient for all of its descendants to also be treated as
	 * having been documented.
	 * <p>
	 * If you do not want to document a field, a field descriptor can be marked as
	 * {@link FieldDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(Map<String, Object> attributes,
			FieldDescriptor... descriptors) {
		return new ResponseFieldsSnippet(Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return new ResponseFieldsSnippet(Arrays.asList(descriptors), attributes, true);
	}

}
