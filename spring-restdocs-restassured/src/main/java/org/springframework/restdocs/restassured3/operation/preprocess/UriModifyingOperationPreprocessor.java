/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.restassured3.operation.preprocess;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;

/**
 * An {@link OperationPreprocessor} that modifies URIs in the request and in the response
 * by changing one or more of their host, scheme, and port. URIs in the following
 * locations are modified:
 * <ul>
 * <li>{@link OperationRequest#getUri() Request URI}
 * <li>{@link OperationRequest#getHeaders() Request headers}
 * <li>{@link OperationRequest#getContent() Request content}
 * <li>{@link OperationRequestPart#getHeaders() Request part headers}
 * <li>{@link OperationRequestPart#getContent() Request part content}
 * <li>{@link OperationResponse#getHeaders() Response headers}
 * <li>{@link OperationResponse#getContent() Response content}
 * </ul>
 *
 * @author Andy Wilkinson
 * @since 1.2.0
 * @deprecated since 2.0.1 in favor of
 * {@link org.springframework.restdocs.operation.preprocess.UriModifyingOperationPreprocessor}
 */
@Deprecated
public class UriModifyingOperationPreprocessor extends
		org.springframework.restdocs.operation.preprocess.UriModifyingOperationPreprocessor {

}
