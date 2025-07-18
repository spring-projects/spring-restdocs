/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.operation;

/**
 * A {@code RequestConverter} is used to convert an implementation-specific request into
 * an {@link OperationRequest}.
 *
 * @param <R> the implementation-specific request type
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface RequestConverter<R> {

	/**
	 * Converts the given {@code request} into an {@code OperationRequest}.
	 * @param request the request
	 * @return the operation request
	 * @throws ConversionException if the conversion fails
	 */
	OperationRequest convert(R request);

}
