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

import java.util.Arrays;

import org.springframework.restdocs.core.RestDocumentationResultHandlers.LinkDocumentingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;

public class RestDocumentationResultActions implements ResultActions {

	private final ResultActions delegate;

	private final String outputDir;

	public RestDocumentationResultActions(String outputDir, ResultActions delegate) {
		this.outputDir = outputDir;
		this.delegate = delegate;
	}

	@Override
	public RestDocumentationResultActions andExpect(ResultMatcher matcher)
			throws Exception {
		this.delegate.andExpect(matcher);
		return this;
	}

	@Override
	public RestDocumentationResultActions andDo(ResultHandler handler) throws Exception {
		this.delegate.andDo(handler);
		return this;
	}

	@Override
	public MvcResult andReturn() {
		return this.delegate.andReturn();
	}

	public RestDocumentationResultActions andDocumentLinks(LinkExtractor linkExtractor, LinkDescriptor... descriptors)
			throws Exception {
		this.delegate.andDo(new LinkDocumentingResultHandler(this.outputDir, linkExtractor, Arrays
				.asList(descriptors)));
		return this;
	}

}
