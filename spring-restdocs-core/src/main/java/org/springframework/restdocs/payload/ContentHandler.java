/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.List;

import org.jspecify.annotations.Nullable;

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
	 * it is described but is not present in the payload.
	 * @return descriptors for the fields that are missing from the payload
	 * @throws PayloadHandlingException if a failure occurs
	 */
	List<FieldDescriptor> findMissingFields();

	/**
	 * Returns modified content, formatted as a String, that only contains the fields that
	 * are undocumented. A field is undocumented if it is present in the handler's content
	 * but is not described. If the content is completely documented, {@code null} is
	 * returned
	 * @return the undocumented content, or {@code null} if all of the content is
	 * documented
	 * @throws PayloadHandlingException if a failure occurs
	 */
	@Nullable String getUndocumentedContent();

	/**
	 * Create a {@link ContentHandler} for the given content type and payload, described
	 * by the given descriptors.
	 * @param content the payload
	 * @param contentType the content type
	 * @param descriptors descriptors of the content
	 * @return the ContentHandler
	 * @throws PayloadHandlingException if no known ContentHandler can handle the content
	 */
	static ContentHandler forContentWithDescriptors(byte[] content, @Nullable MediaType contentType,
			List<FieldDescriptor> descriptors) {
		try {
			return new JsonContentHandler(content, descriptors);
		}
		catch (Exception je) {
			try {
				return new XmlContentHandler(content, descriptors);
			}
			catch (Exception xe) {
				throw new PayloadHandlingException("Cannot handle content "
						+ ((contentType != null) ? "with type " + contentType : "of unknown type")
						+ " as it could not be parsed as JSON or XML");
			}
		}
	}

}
