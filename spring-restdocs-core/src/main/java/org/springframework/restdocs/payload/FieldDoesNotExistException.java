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

package org.springframework.restdocs.payload;

/**
 * A {@code FieldDoesNotExistException} is thrown when a requested field does not exist in
 * a payload.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("serial")
public class FieldDoesNotExistException extends RuntimeException {

	/**
	 * Creates a new {@code FieldDoesNotExistException} that indicates that the field with
	 * the given {@code fieldPath} does not exist.
	 *
	 * @param fieldPath the path of the field that does not exist
	 */
	public FieldDoesNotExistException(JsonFieldPath fieldPath) {
		super("The payload does not contain a field with the path '" + fieldPath + "'");
	}
}
