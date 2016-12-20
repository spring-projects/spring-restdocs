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

import org.springframework.http.MediaType;

/**
 * Strategy interface for extracting a subsection of a payload.
 *
 * @param <T> The subsection extractor subclass
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public interface PayloadSubsectionExtractor<T extends PayloadSubsectionExtractor<T>> {

	/**
	 * Extracts a subsection of the given {@code payload} that has the given
	 * {@code contentType}.
	 *
	 * @param payload the payload
	 * @param contentType the content type of the payload
	 * @return the subsection of the payload
	 */
	byte[] extractSubsection(byte[] payload, MediaType contentType);

	/**
	 * Returns an identifier for the subsection that this extractor will extract.
	 *
	 * @return the identifier
	 */
	String getSubsectionId();

	/**
	 * Returns an extractor with the given {@code subsectionId}.
	 *
	 * @param subsectionId the subsection ID
	 * @return the customized extractor
	 */
	T withSubsectionId(String subsectionId);

}
