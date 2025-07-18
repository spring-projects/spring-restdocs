/*
 * Copyright 2014-present the original author or authors.
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
 * Resolves the type of a field in a request or response payload.
 *
 * @author Mathias Düsterhöft
 * @author Andy Wilkinson
 * @since 2.0.3
 */
public interface FieldTypeResolver {

	/**
	 * Create a {@code FieldTypeResolver} for the given {@code content} and
	 * {@code contentType}, described by the given {@code descriptors}.
	 * @param content the payload that the {@code FieldTypeResolver} should handle
	 * @param contentType the content type of the payload
	 * @param descriptors the descriptors of the content
	 * @return the {@code FieldTypeResolver}
	 */
	static FieldTypeResolver forContentWithDescriptors(byte[] content, MediaType contentType,
			List<FieldDescriptor> descriptors) {
		return ContentHandler.forContentWithDescriptors(content, contentType, descriptors);
	}

	/**
	 * Resolves the type of the field that is described by the given
	 * {@code fieldDescriptor} based on the content of the payload.
	 * @param fieldDescriptor the field descriptor
	 * @return the type of the field
	 */
	Object resolveFieldType(FieldDescriptor fieldDescriptor);

}
