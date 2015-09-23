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
 * A {@code FieldTypeRequiredException} is thrown when a field's type cannot be determined
 * automatically and, therefore, must be explicitly provided.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("serial")
public class FieldTypeRequiredException extends RuntimeException {

	/**
	 * Creates a new {@code FieldTypeRequiredException} indicating that a type is required
	 * for the reason described in the given {@code message}.
	 *
	 * @param message the message
	 */
	public FieldTypeRequiredException(String message) {
		super(message);
	}
}
