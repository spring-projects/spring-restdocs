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

package org.springframework.restdocs;

import static org.springframework.restdocs.curl.CurlDocumentation.documentCurlRequest;
import static org.springframework.restdocs.http.HttpDocumentation.documentHttpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.documentHttpResponse;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.documentLinks;
import static org.springframework.restdocs.payload.PayloadDocumentation.documentRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.documentResponseFields;

import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinkExtractor;
import org.springframework.restdocs.hypermedia.LinkExtractors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * A Spring MVC Test {@code ResultHandler} for documenting RESTful APIs.
 * 
 * @author Andy Wilkinson
 * @author Andreas Evers
 * @see RestDocumentation#document(String)
 */
public class RestDocumentationResultHandler implements ResultHandler {

	private final String outputDir;

	private List<ResultHandler> delegates;

	RestDocumentationResultHandler(String outputDir) {
		this.outputDir = outputDir;

		this.delegates = new ArrayList<ResultHandler>();
		this.delegates.add(documentCurlRequest(this.outputDir));
		this.delegates.add(documentHttpRequest(this.outputDir));
		this.delegates.add(documentHttpResponse(this.outputDir));
	}

	@Override
	public void handle(MvcResult result) throws Exception {
		for (ResultHandler delegate : this.delegates) {
			delegate.handle(result);
		}
	}

	/**
	 * Document the links in the response using the given {@code descriptors}. The links
	 * are extracted from the response based on its content type.
	 * <p>
	 * If a link is present in the response but is not described by one of the descriptors
	 * a failure will occur when this handler is invoked. Similarly, if a link is
	 * described but is not present in the response a failure will also occur when this
	 * handler is invoked.
	 * 
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see HypermediaDocumentation#linkWithRel(String)
	 * @see LinkExtractors#extractorForContentType(String)
	 */
	public RestDocumentationResultHandler withLinks(LinkDescriptor... descriptors) {
		return withLinks(null, descriptors);
	}

	/**
	 * Document the links in the response using the given {@code descriptors}. The links
	 * are extracted from the response using the given {@code linkExtractor}.
	 * <p>
	 * If a link is present in the response but is not described by one of the descriptors
	 * a failure will occur when this handler is invoked. Similarly, if a link is
	 * described but is not present in the response a failure will also occur when this
	 * handler is invoked.
	 * 
	 * @param linkExtractor used to extract the links from the response
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see HypermediaDocumentation#linkWithRel(String)
	 */
	public RestDocumentationResultHandler withLinks(LinkExtractor linkExtractor,
			LinkDescriptor... descriptors) {
		this.delegates.add(documentLinks(this.outputDir, linkExtractor, descriptors));
		return this;
	}

	/**
	 * Document the fields in the request using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the request but is not described by one of the descriptors
	 * a failure will occur when this handler is invoked. Similarly, if a field is
	 * described but is not present in the request a failure will also occur when this
	 * handler is invoked.
	 * 
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see PayloadDocumentation#fieldWithPath(String)
	 */
	public RestDocumentationResultHandler withRequestFields(
			FieldDescriptor... descriptors) {
		this.delegates.add(documentRequestFields(this.outputDir, descriptors));
		return this;
	}

	/**
	 * Document the fields in the response using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the response but is not described by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * field is described but is not present in the response a failure will also occur
	 * when this handler is invoked.
	 * 
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see PayloadDocumentation#fieldWithPath(String)
	 */
	public RestDocumentationResultHandler withResponseFields(
			FieldDescriptor... descriptors) {
		this.delegates.add(documentResponseFields(this.outputDir, descriptors));
		return this;
	}

}
