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
import static org.springframework.restdocs.request.RequestDocumentation.documentQueryParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinkExtractor;
import org.springframework.restdocs.hypermedia.LinkExtractors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
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

	private SnippetWritingResultHandler curlRequest;

	private SnippetWritingResultHandler httpRequest;

	private SnippetWritingResultHandler httpResponse;

	private List<SnippetWritingResultHandler> delegates = new ArrayList<>();

	RestDocumentationResultHandler(String outputDir) {
		this.outputDir = outputDir;
		this.curlRequest = documentCurlRequest(this.outputDir, null);
		this.httpRequest = documentHttpRequest(this.outputDir, null);
		this.httpResponse = documentHttpResponse(this.outputDir, null);
	}

	/**
	 * Customizes the default curl request snippet generation to make the given attributes
	 * available.
	 * 
	 * @param attributes the attributes
	 * @return {@code this}
	 */
	public RestDocumentationResultHandler withCurlRequest(Map<String, Object> attributes) {
		this.curlRequest = documentCurlRequest(this.outputDir, attributes);
		return this;
	}

	/**
	 * Customizes the default HTTP request snippet generation to make the given attributes
	 * available.
	 * 
	 * @param attributes the attributes
	 * @return {@code this}
	 */
	public RestDocumentationResultHandler withHttpRequest(Map<String, Object> attributes) {
		this.httpRequest = documentHttpRequest(this.outputDir, attributes);
		return this;
	}

	/**
	 * Customizes the default HTTP response snippet generation to make the given
	 * attributes available.
	 * 
	 * @param attributes the attributes
	 * @return {@code this}
	 */
	public RestDocumentationResultHandler withHttpResponse(Map<String, Object> attributes) {
		this.httpResponse = documentHttpResponse(this.outputDir, attributes);
		return this;
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
		return withLinks(null, null, descriptors);
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
		return this.withLinks(null, linkExtractor, descriptors);
	}

	/**
	 * Document the links in the response using the given {@code descriptors}. The links
	 * are extracted from the response based on its content type. The given
	 * {@code attributes} are made available during the generation of the links snippet.
	 * <p>
	 * If a link is present in the response but is not described by one of the descriptors
	 * a failure will occur when this handler is invoked. Similarly, if a link is
	 * described but is not present in the response a failure will also occur when this
	 * handler is invoked.
	 * 
	 * @param attributes the attributes
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see HypermediaDocumentation#linkWithRel(String)
	 * @see LinkExtractors#extractorForContentType(String)
	 */
	public RestDocumentationResultHandler withLinks(Map<String, Object> attributes,
			LinkDescriptor... descriptors) {
		return withLinks(attributes, null, descriptors);
	}

	/**
	 * Document the links in the response using the given {@code descriptors}. The links
	 * are extracted from the response using the given {@code linkExtractor}. The given
	 * {@code attributes} are made available during the generation of the links snippet.
	 * <p>
	 * If a link is present in the response but is not described by one of the descriptors
	 * a failure will occur when this handler is invoked. Similarly, if a link is
	 * described but is not present in the response a failure will also occur when this
	 * handler is invoked.
	 * 
	 * @param attributes the attributes
	 * @param linkExtractor used to extract the links from the response
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see HypermediaDocumentation#linkWithRel(String)
	 */
	public RestDocumentationResultHandler withLinks(Map<String, Object> attributes,
			LinkExtractor linkExtractor, LinkDescriptor... descriptors) {
		this.delegates.add(documentLinks(this.outputDir, attributes, linkExtractor,
				descriptors));
		return this;
	}

	/**
	 * Document the fields in the request using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the request but is not documented by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request a
	 * failure will also occur. For payloads with a hierarchical structure, documenting a
	 * field is sufficient for all of its descendants to also be treated as having been
	 * documented.
	 * 
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see PayloadDocumentation#fieldWithPath(String)
	 */
	public RestDocumentationResultHandler withRequestFields(
			FieldDescriptor... descriptors) {
		return this.withRequestFields(null, descriptors);
	}

	/**
	 * Document the fields in the request using the given {@code descriptors}. The given
	 * {@code attributes} are made available during the generation of the request fields
	 * snippet.
	 * <p>
	 * If a field is present in the request but is not documented by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the request a
	 * failure will also occur. For payloads with a hierarchical structure, documenting a
	 * field is sufficient for all of its descendants to also be treated as having been
	 * documented.
	 * 
	 * @param descriptors the link descriptors
	 * @param attributes the attributes
	 * @return {@code this}
	 * @see PayloadDocumentation#fieldWithPath(String)
	 */
	public RestDocumentationResultHandler withRequestFields(
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		this.delegates
				.add(documentRequestFields(this.outputDir, attributes, descriptors));
		return this;
	}

	/**
	 * Document the fields in the response using the given {@code descriptors}.
	 * <p>
	 * If a field is present in the response but is not documented by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field is sufficient for all of its descendants to also be treated as having been
	 * documented.
	 * 
	 * @param descriptors the link descriptors
	 * @return {@code this}
	 * @see PayloadDocumentation#fieldWithPath(String)
	 */
	public RestDocumentationResultHandler withResponseFields(
			FieldDescriptor... descriptors) {
		return this.withResponseFields(null, descriptors);
	}

	/**
	 * Document the fields in the response using the given {@code descriptors}. The given
	 * {@code attributes} are made available during the generation of the request fields
	 * snippet.
	 * <p>
	 * If a field is present in the response but is not documented by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * field is documented, is not marked as optional, and is not present in the response
	 * a failure will also occur. For payloads with a hierarchical structure, documenting
	 * a field is sufficient for all of its descendants to also be treated as having been
	 * documented.
	 * 
	 * @param descriptors the link descriptors
	 * @param attributes the attributes
	 * @return {@code this}
	 * @see PayloadDocumentation#fieldWithPath(String)
	 */
	public RestDocumentationResultHandler withResponseFields(
			Map<String, Object> attributes, FieldDescriptor... descriptors) {
		this.delegates
				.add(documentResponseFields(this.outputDir, attributes, descriptors));
		return this;
	}

	/**
	 * Documents the parameters in the request's query string using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is present in the query string but is not described by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * parameter is described but is not present in the request a failure will also occur
	 * when this handler is invoked.
	 * 
	 * @param descriptors the parameter descriptors
	 * @return {@code this}
	 * @see RequestDocumentation#parameterWithName(String)
	 */
	public RestDocumentationResultHandler withQueryParameters(
			ParameterDescriptor... descriptors) {
		return this.withQueryParameters(null, descriptors);
	}

	/**
	 * Documents the parameters in the request's query string using the given
	 * {@code descriptors}. The given {@code attributes} are made available during the
	 * generation of the query parameters snippet.
	 * <p>
	 * If a parameter is present in the query string but is not described by one of the
	 * descriptors a failure will occur when this handler is invoked. Similarly, if a
	 * parameter is described but is not present in the request a failure will also occur
	 * when this handler is invoked.
	 * 
	 * @param descriptors the parameter descriptors
	 * @param attributes the attributes
	 * @return {@code this}
	 * @see RequestDocumentation#parameterWithName(String)
	 */
	public RestDocumentationResultHandler withQueryParameters(
			Map<String, Object> attributes, ParameterDescriptor... descriptors) {
		this.delegates.add(documentQueryParameters(this.outputDir, attributes,
				descriptors));
		return this;
	}

	@Override
	public void handle(MvcResult result) throws Exception {
		this.curlRequest.handle(result);
		this.httpRequest.handle(result);
		this.httpResponse.handle(result);
		for (ResultHandler delegate : this.delegates) {
			delegate.handle(result);
		}
	}

}
