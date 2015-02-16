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

package org.springframework.restdocs.curl;

import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * Abstract base class for Spring Mock MVC {@link ResultHandler ResultHandlers} that
 * produce documentation snippets relating to cURL.
 * 
 * @author Andy Wilkinson
 */
public abstract class CurlSnippetResultHandler extends SnippetWritingResultHandler {

	private final CurlConfiguration curlConfiguration = new CurlConfiguration();

	public CurlSnippetResultHandler(String outputDir, String fileName) {
		super(outputDir, fileName);
	}

	CurlConfiguration getCurlConfiguration() {
		return this.curlConfiguration;
	}

	public CurlSnippetResultHandler includeResponseHeaders() {
		this.curlConfiguration.setIncludeResponseHeaders(true);
		return this;
	}
}