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

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.StandardOperationRequest;
import org.springframework.restdocs.operation.StandardOperationResponse;

/**
 * An {@link OperationPreprocessor} that applies a {@link ContentModifier} to the content
 * of the request or response.
 *
 * @author Andy Wilkinson
 */
public class ContentModifyingOperationPreprocessor implements OperationPreprocessor {

	private final ContentModifier contentModifier;

	/**
	 * Create a new {@code ContentModifyingOperationPreprocessor} that will apply the
	 * given {@code contentModifier} to the operation's request or response.
	 *
	 * @param contentModifier the contentModifier
	 */
	public ContentModifyingOperationPreprocessor(ContentModifier contentModifier) {
		this.contentModifier = contentModifier;
	}

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		byte[] modifiedContent = this.contentModifier.modifyContent(request.getContent());
		return new StandardOperationRequest(request.getUri(), request.getMethod(),
				modifiedContent,
				getUpdatedHeaders(request.getHeaders(), modifiedContent),
				request.getParameters(), request.getParts());
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		byte[] modifiedContent = this.contentModifier
				.modifyContent(response.getContent());
		return new StandardOperationResponse(response.getStatus(), getUpdatedHeaders(
				response.getHeaders(), modifiedContent), modifiedContent);
	}

	private HttpHeaders getUpdatedHeaders(HttpHeaders headers, byte[] updatedContent) {
		HttpHeaders updatedHeaders = new HttpHeaders();
		updatedHeaders.putAll(headers);
		if (updatedHeaders.getContentLength() > -1) {
			updatedHeaders.setContentLength(updatedContent.length);
		}
		return updatedHeaders;
	}

}
