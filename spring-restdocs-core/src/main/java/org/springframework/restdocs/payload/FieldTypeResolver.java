/*
 * Copyright 2014-2018 the original author or authors.
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

import org.springframework.http.MediaType;

/**
 * Public abstraction for external access to field type determination for xml and json
 * payloads.
 *
 * @author Mathias Düsterhöft
 * @since 2.0.3
 */
public interface FieldTypeResolver {

	/**
	 * Create a FieldTypeResolver for the given content and contentType.
	 * @param content the payload that the {@link FieldTypeResolver} should handle
	 * @param contentType the content type of the payload
	 * @return the {@link FieldTypeResolver}
	 */
	static FieldTypeResolver forContent(byte[] content, MediaType contentType) {
		return ContentTypeHandlerFactory.create(content, contentType)
				.getFieldTypeResolver();
	}

	/**
	 * Returns the type of the field that is described by the given
	 * {@code fieldDescriptor} based on the content of the payload.
	 * @param fieldDescriptor the field descriptor
	 * @return the type of the field
	 */
	Object determineFieldType(FieldDescriptor fieldDescriptor);

}
