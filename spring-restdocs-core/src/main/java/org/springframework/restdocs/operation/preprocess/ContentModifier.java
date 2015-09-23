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

package org.springframework.restdocs.operation.preprocess;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * A {@code ContentModifier} modifies the content of an {@link OperationRequest} or
 * {@link OperationResponse} during the preprocessing that is performed prior to
 * documentation generation.
 *
 * @author Andy Wilkinson
 * @see ContentModifyingOperationPreprocessor
 */
public interface ContentModifier {

	/**
	 * Returns modified content based on the given {@code originalContent}.
	 *
	 * @param originalContent the original content
	 * @param contentType the type of the original content, may be {@code null}
	 * @return the modified content
	 */
	byte[] modifyContent(byte[] originalContent, MediaType contentType);

}
