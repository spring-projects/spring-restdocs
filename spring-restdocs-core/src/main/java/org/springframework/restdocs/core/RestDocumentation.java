/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.core;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.core.RestDocumentationResultHandlers.documentCurlRequest;
import static org.springframework.restdocs.core.RestDocumentationResultHandlers.documentCurlRequestAndResponse;
import static org.springframework.restdocs.core.RestDocumentationResultHandlers.documentCurlResponse;

public class RestDocumentation {

	public static ResultActions document(String outputDir, ResultActions resultActions)
			throws Exception {
		return resultActions
				.andDo(documentCurlRequest(outputDir).includeResponseHeaders())
				.andDo(documentCurlResponse(outputDir).includeResponseHeaders())
				.andDo(documentCurlRequestAndResponse(outputDir).includeResponseHeaders());
	}
}
