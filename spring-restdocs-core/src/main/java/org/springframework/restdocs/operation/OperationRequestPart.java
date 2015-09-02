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
 * A part of a multipart request
 * 
 * @author awilkinson
 * @see OperationRequest#getParts()
 */
public interface OperationRequestPart {

	/**
	 * Returns the name of the part.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Returns the name of the file that is being uploaded in this part.
	 * 
	 * @return the name of the file
	 */
	String getSubmittedFileName();

	/**
	 * Returns the contents of the part.
	 * 
	 * @return the contents
	 */
	byte[] getContent();

	/**
	 * Returns the part's headers.
	 * 
	 * @return the headers
	 */
	HttpHeaders getHeaders();

}
