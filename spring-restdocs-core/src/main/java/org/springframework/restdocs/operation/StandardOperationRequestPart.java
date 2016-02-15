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
 * Standard implementation of {@code OperationRequestPart}.
 *
 * @author Andy Wilkinson
 */
class StandardOperationRequestPart extends AbstractOperationMessage
		implements OperationRequestPart {

	private final String name;

	private final String submittedFileName;

	/**
	 * Creates a new {@code StandardOperationRequestPart} with the given {@code name}.
	 *
	 * @param name the name of the part
	 * @param submittedFileName the name of the file being uploaded by this part
	 * @param content the contents of the part
	 * @param headers the headers of the part
	 */
	StandardOperationRequestPart(String name, String submittedFileName, byte[] content,
			HttpHeaders headers) {
		super(content, headers);
		this.name = name;
		this.submittedFileName = submittedFileName;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getSubmittedFileName() {
		return this.submittedFileName;
	}

}
