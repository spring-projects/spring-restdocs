/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.List;

import org.jspecify.annotations.Nullable;

import org.springframework.http.MediaType;

/**
 * Strategy interface for extracting a subsection of a payload.
 *
 * @param <T> the subsection extractor subclass
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public interface PayloadSubsectionExtractor<T extends PayloadSubsectionExtractor<T>> {

	/**
	 * Extracts a subsection of the given {@code payload} that has the given
	 * {@code contentType}.
	 * @param payload the payload
	 * @param contentType the content type of the payload
	 * @return the subsection of the payload
	 */
	byte[] extractSubsection(byte[] payload, @Nullable MediaType contentType);

	/**
	 * Extracts a subsection of the given {@code payload} that has the given
	 * {@code contentType} and that is described by the given {@code descriptors}.
	 * @param payload the payload
	 * @param contentType the content type of the payload
	 * @param descriptors descriptors that describe the payload
	 * @return the subsection of the payload
	 * @since 2.0.4
	 */
	default byte[] extractSubsection(byte[] payload, @Nullable MediaType contentType,
			List<FieldDescriptor> descriptors) {
		return extractSubsection(payload, contentType);
	}

	/**
	 * Returns an identifier for the subsection that this extractor will extract.
	 * @return the identifier
	 */
	String getSubsectionId();

	/**
	 * Returns an extractor with the given {@code subsectionId}.
	 * @param subsectionId the subsection ID
	 * @return the customized extractor
	 */
	T withSubsectionId(String subsectionId);

}
