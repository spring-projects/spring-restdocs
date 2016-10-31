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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Attributes.Attribute;

/**
 * Static factory methods for documenting a RESTful API's request and response payloads.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 * @author Marcel Overdijk
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
	 * Creates a {@code FieldDescriptor} that describes a subsection, i.e. a field and all
	 * of its descendants, with the given {@code path}.
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
	 * <p>
	 * A subsection descriptor for the array with the path {@code a.b[]} will also
	 * describe its descendants {@code a.b[].c} and {@code a.b[].d}.
	 *
	 * @param path The path of the subsection
	 * @return a {@code SubsectionDescriptor} ready for further configuration
	 */
	public static SubsectionDescriptor subsectionWithPath(String path) {
		return new SubsectionDescriptor(path);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operations's
	 * request payload. The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the request payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see FieldDescriptor#description(Object)
	 */
	public static RequestFieldsSnippet requestFields(FieldDescriptor... descriptors) {
		return requestFields(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operations's
	 * request payload. The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the request payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet requestFields(List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(descriptors);
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
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			FieldDescriptor... descriptors) {
		return relaxedRequestFields(Arrays.asList(descriptors));
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
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * request payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the request payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet requestFields(Map<String, Object> attributes,
			FieldDescriptor... descriptors) {
		return requestFields(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * request payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the request payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet requestFields(Map<String, Object> attributes,
			List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(descriptors, attributes);
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
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return relaxedRequestFields(attributes, Arrays.asList(descriptors));
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
	 * @see #subsectionWithPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(descriptors, attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of API
	 * operations's request payload extracted by the given {@code subsectionExtractor}.
	 * The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the subsection of the request payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur.For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet requestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			FieldDescriptor... descriptors) {
		return requestFields(subsectionExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields in the subsection of the
	 * API operations's request payload extracted by the given {@code subsectionExtractor}
	 * . The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the subsection of the request payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet requestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(subsectionExtractor, descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of the
	 * API operations's request payload extracted by the given {@code subsectionExtractor}
	 * . The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			FieldDescriptor... descriptors) {
		return relaxedRequestFields(subsectionExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of the
	 * API operations's request payload extracted by the given {@code subsectionExtractor}
	 * . The fields will be documented using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(subsectionExtractor, descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of the
	 * API operation's request payload extracted by the given {@code subsectionExtractor}.
	 * The fields will be documented using the given {@code descriptors} and the given
	 * {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the subsection of the request payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet requestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return requestFields(subsectionExtractor, attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of the
	 * API operation's request payload extracted by the given {@code subsectionExtractor}.
	 * The fields will be documented using the given {@code descriptors} and the given
	 * {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the subsection of the request payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet requestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(subsectionExtractor, descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of the
	 * API operation's request payload extracted by the given {@code subsectionExtractor}.
	 * The fields will be documented using the given {@code descriptors} and the given
	 * {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return relaxedRequestFields(subsectionExtractor, attributes,
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the subsection of the
	 * API operation's request payload extracted by the given {@code subsectionExtractor}.
	 * The fields will be documented using the given {@code descriptors} and the given
	 * {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestFieldsSnippet relaxedRequestFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestFieldsSnippet(subsectionExtractor, descriptors, attributes,
				true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the payload of the request part, but is not documented by
	 * one of the descriptors, a failure will occur when the snippet is invoked.
	 * Similarly, if a field is documented, is not marked as optional, and is not present
	 * in the request part's payload, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			FieldDescriptor... descriptors) {
		return requestPartFields(part, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the payload of the request part, but is not documented by
	 * one of the descriptors, a failure will occur when the snippet is invoked.
	 * Similarly, if a field is documented, is not marked as optional, and is not present
	 * in the request part's payload, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			FieldDescriptor... descriptors) {
		return relaxedRequestPartFields(part, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors} and the given {@code attributes} will be
	 * available during snippet generation.
	 * <p>
	 * If a field is present in the payload of the request part, but is not documented by
	 * one of the descriptors, a failure will occur when the snippet is invoked.
	 * Similarly, if a field is documented, is not marked as optional, and is not present
	 * in the request part's payload, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return requestPartFields(part, attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors} and the given {@code attributes} will be
	 * available during snippet generation.
	 * <p>
	 * If a field is present in the payload of the request part, but is not documented by
	 * one of the descriptors, a failure will occur when the snippet is invoked.
	 * Similarly, if a field is documented, is not marked as optional, and is not present
	 * in the request part's payload, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors} and the given {@code attributes} will be
	 * available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return relaxedRequestPartFields(part, attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the specified
	 * {@code part} of the API operations's request payload. The fields will be documented
	 * using the given {@code descriptors} and the given {@code attributes} will be
	 * available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, descriptors, attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the subsection of the request part payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the subsection's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			FieldDescriptor... descriptors) {
		return requestPartFields(part, subsectionExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the subsection of the request part payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the subsection's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, subsectionExtractor, descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			FieldDescriptor... descriptors) {
		return relaxedRequestPartFields(part, subsectionExtractor,
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors}.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, subsectionExtractor, descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the givne {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors} and the given {@code attributes}
	 * will be available during snippet generation.
	 * <p>
	 * If a field is present in the subsection of the request part payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return requestPartFields(part, subsectionExtractor, attributes,
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors} and the given {@code attributes}
	 * will be available during snippet generation.
	 * <p>
	 * If a field is present in the subsection of the request part payload, but is not
	 * documented by one of the descriptors, a failure will occur when the snippet is
	 * invoked. Similarly, if a field is documented, is not marked as optional, and is not
	 * present in the subsection, a failure will also occur. For payloads with a
	 * hierarchical structure, documenting a field with a
	 * {@link #subsectionWithPath(String) subsection descriptor} will mean that all of its
	 * descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet requestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, subsectionExtractor, descriptors,
				attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors} and the given {@code attributes}
	 * will be available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return relaxedRequestPartFields(part, subsectionExtractor, attributes,
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the
	 * specified {@code part} of the API operations's request payload. The subsection will
	 * be extracted by the given {@code subsectionExtractor}. The fields will be
	 * documented using the given {@code descriptors} and the given {@code attributes}
	 * will be available during snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param part the part name
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request part's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static RequestPartFieldsSnippet relaxedRequestPartFields(String part,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new RequestPartFieldsSnippet(part, subsectionExtractor, descriptors,
				attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
		return responseFields(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(
			List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(descriptors);
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
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			FieldDescriptor... descriptors) {
		return relaxedResponseFields(Arrays.asList(descriptors));
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
	 * @see #subsectionWithPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(Map<String, Object> attributes,
			FieldDescriptor... descriptors) {
		return responseFields(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of the API operation's
	 * response payload. The fields will be documented using the given {@code descriptors}
	 * and the given {@code attributes} will be available during snippet generation.
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response,
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field with a {@link #subsectionWithPath(String) subsection descriptor} will mean
	 * that all of its descendants are also treated as having been documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(Map<String, Object> attributes,
			List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(descriptors, attributes);
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
	 * @see #subsectionWithPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return relaxedResponseFields(attributes, Arrays.asList(descriptors));
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
	 * @see #subsectionWithPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(descriptors, attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field with a {@link #subsectionWithPath(String) subsection
	 * descriptor} will mean that all of its descendants are also treated as having been
	 * documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			FieldDescriptor... descriptors) {
		return responseFields(subsectionExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field with a {@link #subsectionWithPath(String) subsection
	 * descriptor} will mean that all of its descendants are also treated as having been
	 * documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(subsectionExtractor, descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			FieldDescriptor... descriptors) {
		return relaxedResponseFields(subsectionExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(subsectionExtractor, descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} and the given {@code attributes} will be available during
	 * snippet generation.
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field with a {@link #subsectionWithPath(String) subsection
	 * descriptor} will mean that all of its descendants are also treated as having been
	 * documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return responseFields(subsectionExtractor, attributes,
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} and the given {@code attributes} will be available during
	 * snippet generation.
	 * <p>
	 * If a field is present in the response payload, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * payload, a failure will also occur. For payloads with a hierarchical structure,
	 * documenting a field with a {@link #subsectionWithPath(String) subsection
	 * descriptor} will mean that all of its descendants are also treated as having been
	 * documented.
	 * <p>
	 * If you do not want to document a field or subsection, a descriptor can be
	 * {@link FieldDescriptor#ignored configured to ignore it}. The ignored field or
	 * subsection will not appear in the generated snippet and the failure described above
	 * will not occur.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet responseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(subsectionExtractor, descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} and the given {@code attributes} will be available during
	 * snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return relaxedResponseFields(subsectionExtractor, attributes,
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the fields of a subsection of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The fields will be documented using the given
	 * {@code descriptors} and the given {@code attributes} will be available during
	 * snippet generation.
	 * <p>
	 * If a field is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented fields will be ignored.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response payload's fields
	 * @return the snippet that will document the fields
	 * @since 1.2.0
	 * @see #fieldWithPath(String)
	 * @see #subsectionWithPath(String)
	 * @see #beneathPath(String)
	 */
	public static ResponseFieldsSnippet relaxedResponseFields(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes, List<FieldDescriptor> descriptors) {
		return new ResponseFieldsSnippet(subsectionExtractor, descriptors, attributes,
				true);
	}

	/**
	 * Returns a {@code Snippet} that will document the body of the API operation's
	 * request payload.
	 *
	 * @return the snippet that will document the request body
	 */
	public static RequestBodySnippet requestBody() {
		return new RequestBodySnippet();
	}

	/**
	 * Returns a {@code Snippet} that will document the body of the API operation's
	 * request payload. The given attributes will be made available during snippet
	 * generation.
	 *
	 * @param attributes the attributes
	 * @return the snippet that will document the request body
	 */
	public static RequestBodySnippet requestBody(Map<String, Object> attributes) {
		return new RequestBodySnippet(attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document a subsection of the body of the API
	 * operation's request payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @return the snippet that will document the request body subsection
	 */
	public static RequestBodySnippet requestBody(
			PayloadSubsectionExtractor<?> subsectionExtractor) {
		return new RequestBodySnippet(subsectionExtractor);
	}

	/**
	 * Returns a {@code Snippet} that will document a subsection of the body of the API
	 * operation's request payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The given attributes will be made available during
	 * snippet generation.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @return the snippet that will document the request body subsection
	 */
	public static RequestBodySnippet requestBody(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		return new RequestBodySnippet(subsectionExtractor, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the body of the API operation's
	 * response payload.
	 *
	 * @return the snippet that will document the response body
	 */
	public static ResponseBodySnippet responseBody() {
		return new ResponseBodySnippet();
	}

	/**
	 * Returns a {@code Snippet} that will document the body of the API operation's
	 * response payload. The given attributes will be made available during snippet
	 * generation.
	 *
	 * @param attributes the attributes
	 * @return the snippet that will document the response body
	 */
	public static ResponseBodySnippet responseBody(Map<String, Object> attributes) {
		return new ResponseBodySnippet(attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document a subsection of the body of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @return the snippet that will document the response body subsection
	 */
	public static ResponseBodySnippet responseBody(
			PayloadSubsectionExtractor<?> subsectionExtractor) {
		return new ResponseBodySnippet(subsectionExtractor);
	}

	/**
	 * Returns a {@code Snippet} that will document a subsection of the body of the API
	 * operation's response payload. The subsection will be extracted using the given
	 * {@code subsectionExtractor}. The given attributes will be made available during
	 * snippet generation.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @return the snippet that will document the response body subsection
	 */
	public static ResponseBodySnippet responseBody(
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		return new ResponseBodySnippet(subsectionExtractor, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the body of specified part of the API
	 * operation's request payload.
	 *
	 * @param partName the name of the request part
	 * @return the snippet that will document the response body
	 */
	public static RequestPartBodySnippet requestPartBody(String partName) {
		return new RequestPartBodySnippet(partName);
	}

	/**
	 * Returns a {@code Snippet} that will document the body of specified part of the API
	 * operation's request payload. The given attributes will be made available during
	 * snippet generation.
	 *
	 * @param partName the name of the request part
	 * @param attributes the attributes
	 * @return the snippet that will document the response body
	 */
	public static RequestPartBodySnippet requestPartBody(String partName,
			Map<String, Object> attributes) {
		return new RequestPartBodySnippet(partName, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document a subsection of the body of specified
	 * part of the API operation's request payload. The subsection will be extracted using
	 * the given {@code subsectionExtractor}.
	 *
	 * @param partName the name of the request part
	 * @param subsectionExtractor the subsection extractor
	 * @return the snippet that will document the response body
	 */
	public static RequestPartBodySnippet requestPartBody(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor) {
		return new RequestPartBodySnippet(partName, subsectionExtractor);
	}

	/**
	 * Returns a {@code Snippet} that will document a subsection of the body of specified
	 * part of the API operation's request payload. The subsection will be extracted using
	 * the given {@code subsectionExtractor}. The given attributes will be made available
	 * during snippet generation.
	 *
	 * @param partName the name of the request part
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes the attributes
	 * @return the snippet that will document the response body
	 */
	public static RequestPartBodySnippet requestPartBody(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		return new RequestPartBodySnippet(partName, subsectionExtractor, attributes);
	}

	/**
	 * Creates a copy of the given {@code descriptors} with the given {@code pathPrefix}
	 * applied to their paths.
	 *
	 * @param pathPrefix the path prefix
	 * @param descriptors the descriptors to copy
	 * @return the copied descriptors with the prefix applied
	 */
	public static List<FieldDescriptor> applyPathPrefix(String pathPrefix,
			List<FieldDescriptor> descriptors) {
		List<FieldDescriptor> prefixedDescriptors = new ArrayList<>();
		for (FieldDescriptor descriptor : descriptors) {
			String prefixedPath = pathPrefix + descriptor.getPath();
			FieldDescriptor prefixedDescriptor = descriptor instanceof SubsectionDescriptor
					? new SubsectionDescriptor(prefixedPath)
					: new FieldDescriptor(prefixedPath);
			prefixedDescriptor.description(descriptor.getDescription())
					.type(descriptor.getType())
					.attributes(asArray(descriptor.getAttributes()));
			if (descriptor.isIgnored()) {
				prefixedDescriptor.ignored();
			}
			if (descriptor.isOptional()) {
				prefixedDescriptor.optional();
			}
			prefixedDescriptors.add(prefixedDescriptor);
		}
		return prefixedDescriptors;
	}

	/**
	 * Returns a {@link PayloadSubsectionExtractor} that will extract the subsection of
	 * the JSON payload found beneath the given {@code path}.
	 *
	 * @param path the path
	 * @return the subsection extractor
	 * @since 1.2.0
	 */
	public static PayloadSubsectionExtractor<?> beneathPath(String path) {
		return new FieldPathPayloadSubsectionExtractor(path);
	}

	private static Attribute[] asArray(Map<String, Object> attributeMap) {
		List<Attributes.Attribute> attributes = new ArrayList<>();
		for (Map.Entry<String, Object> attribute : attributeMap.entrySet()) {
			attributes
					.add(Attributes.key(attribute.getKey()).value(attribute.getValue()));
		}
		return attributes.toArray(new Attribute[attributes.size()]);
	}

}
