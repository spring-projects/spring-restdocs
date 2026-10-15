/*
 * Copyright 2014-present the original author or authors.
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
