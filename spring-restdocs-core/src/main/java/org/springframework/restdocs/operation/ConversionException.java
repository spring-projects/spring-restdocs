/*
 * Copyright 2012-2016 the original author or authors.
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

/**
 * An exception that can be thrown by {@link RequestConverter} and
 * {@link ResponseConverter} implementations to indicate that a failure has occurred
 * during conversion.
 *
 * @author Andy Wilkinson
 * @see RequestConverter#convert(Object)
 * @see ResponseConverter#convert(Object)
 */
public class ConversionException extends RuntimeException {

	/**
	 * Creates a new {@code ConversionException} with the given {@code cause}.
	 *
	 * @param cause the cause
	 */
	public ConversionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new {@code ConversionException} with the given {@code message} and
	 * {@code cause}.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
