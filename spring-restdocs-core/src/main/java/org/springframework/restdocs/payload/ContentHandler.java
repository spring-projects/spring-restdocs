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

import java.util.List;

/**
 * A handler for the content of a request or response.
 *
 * @author Andy Wilkinson
 */
interface ContentHandler {

	/**
	 * Finds the fields that are missing from the handler's payload. A field is missing if
	 * it is described by one of the {@code fieldDescriptors} but is not present in the
	 * payload.
	 *
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
	 *
	 * @param fieldDescriptors the descriptors
	 * @return the undocumented content, or {@code null} if all of the content is
	 * documented
	 * @throws PayloadHandlingException if a failure occurs
	 */
	String getUndocumentedContent(List<FieldDescriptor> fieldDescriptors);

	/**
	 * Returns the type of the field with the given {@code path} based on the content of
	 * the payload.
	 *
	 * @param path the field path
	 * @return the type of the field
	 */
	Object determineFieldType(String path);

}
