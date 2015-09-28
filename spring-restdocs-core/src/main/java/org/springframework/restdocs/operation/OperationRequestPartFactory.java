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

package org.springframework.restdocs.operation;

import org.springframework.http.HttpHeaders;

/**
 * A factory for creating {@link OperationRequestPart OperationRequestParts}.
 *
 * @author Andy Wilkinson
 */
public class OperationRequestPartFactory {

	/**
	 * Creates a new {@link OperationRequestPart}. The given {@code headers} will be
	 * augmented to ensure that they always include a {@code Content-Length} header if the
	 * part has any content.
	 *
	 * @param name the name of the part
	 * @param submittedFileName the name of the file being submitted by the part
	 * @param content the content of the part
	 * @param headers the headers of the part
	 * @return the {@code OperationRequestPart}
	 */
	public OperationRequestPart create(String name, String submittedFileName,
			byte[] content, HttpHeaders headers) {
		return new StandardOperationRequestPart(name, submittedFileName, content,
				augmentHeaders(headers, content));
	}

	private HttpHeaders augmentHeaders(HttpHeaders input, byte[] content) {
		return new HttpHeadersHelper(input).setContentLengthHeader(content).getHeaders();
	}

}
