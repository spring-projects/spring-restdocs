/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.payload;

import java.util.List;

import org.springframework.http.MediaType;

/**
 * A handler for the content of a request or response.
 *
 * @author Andy Wilkinson
 * @author Mathias Düsterhöft
 */
interface ContentHandler extends FieldTypeResolver {

	/**
	 * Finds the fields that are missing from the handler's payload. A field is missing if
	 * it is described by one of the {@code fieldDescriptors} but is not present in the
	 * payload.
	 * @param fieldDescriptors the descriptors
	 * @return descriptors for the fields that are missing from the payload
	 * @throws PayloadHandlingException if a failure occurs
	 */
	List<FieldDescriptor> findMissingFields(List<FieldDescriptor> fieldDescriptors);

	/**
	 * Returns modified content, formatted as a String, that only contains the fields that
	 * are undocumented. A field is undocumented if it is present in the handler's content
	 * but is not described by the given {@code fieldDescriptors}. If the content is
	 * completely documented, {@code null} is returned
	 * @param fieldDescriptors the descriptors
	 * @return the undocumented content, or {@code null} if all of the content is
	 * documented
	 * @throws PayloadHandlingException if a failure occurs
	 */
	String getUndocumentedContent(List<FieldDescriptor> fieldDescriptors);

	/**
	 * Create a {@link ContentHandler} for the given content type and payload.
	 * @param content the payload
	 * @param contentType the content type
	 * @return the ContentHandler
	 * @throws PayloadHandlingException if no known ContentHandler can handle the content
	 */
	static ContentHandler forContent(byte[] content, MediaType contentType) {

		try {
			return new JsonContentHandler(content);
		}
		catch (Exception je) {
			try {
				return new XmlContentHandler(content);
			}
			catch (Exception xe) {
				throw new PayloadHandlingException("Cannot handle " + contentType
						+ " content as it could not be parsed as JSON or XML");
			}
		}
	}

}
