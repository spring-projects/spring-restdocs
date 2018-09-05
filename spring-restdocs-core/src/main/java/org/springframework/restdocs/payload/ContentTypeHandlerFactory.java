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
 * Factory providing access to {@link ContentHandler} implementation for the content type.
 *
 * @author Mathias Düsterhöft
 */
final class ContentTypeHandlerFactory {

	private ContentTypeHandlerFactory() {
	}

	/**
	 * Create a {@link ContentHandler} for the given content type and payload.
	 * @param content the payload
	 * @param contentType the content type
	 * @return the ContentHandler
	 */
	static ContentHandler create(byte[] content, MediaType contentType) {
		ContentHandler contentHandler = createJsonContentHandler(content);
		if (contentHandler == null) {
			contentHandler = createXmlContentHandler(content);
			if (contentHandler == null) {
				throw new PayloadHandlingException("Cannot handle " + contentType
						+ " content as it could not be parsed as JSON or XML");
			}
		}
		return contentHandler;
	}

	private static ContentHandler createJsonContentHandler(byte[] content) {
		try {
			return new JsonContentHandler(content);
		}
		catch (Exception ex) {
			return null;
		}
	}

	private static ContentHandler createXmlContentHandler(byte[] content) {
		try {
			return new XmlContentHandler(content);
		}
		catch (Exception ex) {
			return null;
		}
	}

}
