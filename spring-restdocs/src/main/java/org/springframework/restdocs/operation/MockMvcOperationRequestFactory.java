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

package org.springframework.restdocs.operation;

import static org.springframework.restdocs.util.IterableEnumeration.iterable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * A factory for creating an {@link OperationRequest} from a
 * {@link MockHttpServletRequest}.
 * 
 * @author Andy Wilkinson
 *
 */
public class MockMvcOperationRequestFactory {

	private static final String SCHEME_HTTP = "http";

	private static final String SCHEME_HTTPS = "https";

	private static final int STANDARD_PORT_HTTP = 80;

	private static final int STANDARD_PORT_HTTPS = 443;

	/**
	 * Creates a new {@code OperationRequest} derived from the given {@code mockRequest}.
	 * 
	 * @param mockRequest the request
	 * @return the {@code OperationRequest}
	 * @throws Exception if the request could not be created
	 */
	public OperationRequest createOperationRequest(MockHttpServletRequest mockRequest)
			throws Exception {
		HttpHeaders headers = extractHeaders(mockRequest);
		Parameters parameters = extractParameters(mockRequest);
		List<OperationRequestPart> parts = extractParts(mockRequest);
		String queryString = mockRequest.getQueryString();
		if (!StringUtils.hasText(queryString) && "GET".equals(mockRequest.getMethod())) {
			queryString = parameters.toQueryString();
		}
		return new StandardOperationRequest(URI.create(getRequestUri(mockRequest)
				+ (StringUtils.hasText(queryString) ? "?" + queryString : "")),
				HttpMethod.valueOf(mockRequest.getMethod()),
				FileCopyUtils.copyToByteArray(mockRequest.getInputStream()), headers,
				parameters, parts);
	}

	private List<OperationRequestPart> extractParts(MockHttpServletRequest servletRequest)
			throws IOException, ServletException {
		List<OperationRequestPart> parts = new ArrayList<>();
		for (Part part : servletRequest.getParts()) {
			HttpHeaders partHeaders = extractHeaders(part);
			List<String> contentTypeHeader = partHeaders.get(HttpHeaders.CONTENT_TYPE);
			if (part.getContentType() != null && contentTypeHeader == null) {
				partHeaders
						.setContentType(MediaType.parseMediaType(part.getContentType()));
			}
			parts.add(new StandardOperationRequestPart(part.getName(), StringUtils
					.hasText(part.getSubmittedFileName()) ? part.getSubmittedFileName()
					: null, FileCopyUtils.copyToByteArray(part.getInputStream()),
					partHeaders));
		}
		if (servletRequest instanceof MockMultipartHttpServletRequest) {
			for (Entry<String, List<MultipartFile>> entry : ((MockMultipartHttpServletRequest) servletRequest)
					.getMultiFileMap().entrySet()) {
				for (MultipartFile file : entry.getValue()) {
					HttpHeaders partHeaders = new HttpHeaders();
					if (StringUtils.hasText(file.getContentType())) {
						partHeaders.setContentType(MediaType.parseMediaType(file
								.getContentType()));
					}
					parts.add(new StandardOperationRequestPart(file.getName(),
							StringUtils.hasText(file.getOriginalFilename()) ? file
									.getOriginalFilename() : null, file.getBytes(),
							partHeaders));
				}
			}
		}
		return parts;
	}

	private HttpHeaders extractHeaders(Part part) {
		HttpHeaders partHeaders = new HttpHeaders();
		for (String headerName : part.getHeaderNames()) {
			for (String value : part.getHeaders(headerName)) {
				partHeaders.add(headerName, value);
			}
		}
		return partHeaders;
	}

	private Parameters extractParameters(MockHttpServletRequest servletRequest) {
		Parameters parameters = new Parameters();
		for (String name : iterable(servletRequest.getParameterNames())) {
			for (String value : servletRequest.getParameterValues(name)) {
				parameters.add(name, value);
			}
		}
		return parameters;
	}

	private HttpHeaders extractHeaders(MockHttpServletRequest servletRequest) {
		HttpHeaders headers = new HttpHeaders();
		for (String headerName : iterable(servletRequest.getHeaderNames())) {
			for (String value : iterable(servletRequest.getHeaders(headerName))) {
				headers.add(headerName, value);
			}
		}
		return headers;
	}

	private boolean isNonStandardPort(MockHttpServletRequest request) {
		return (SCHEME_HTTP.equals(request.getScheme()) && request.getServerPort() != STANDARD_PORT_HTTP)
				|| (SCHEME_HTTPS.equals(request.getScheme()) && request.getServerPort() != STANDARD_PORT_HTTPS);
	}

	private String getRequestUri(MockHttpServletRequest request) {
		StringWriter uriWriter = new StringWriter();
		PrintWriter printer = new PrintWriter(uriWriter);

		printer.printf("%s://%s", request.getScheme(), request.getServerName());
		if (isNonStandardPort(request)) {
			printer.printf(":%d", request.getServerPort());
		}
		printer.print(request.getRequestURI());
		return uriWriter.toString();
	}

}
