/*
 * Copyright 2014-2016 the original author or authors.
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

package org.springframework.restdocs.mockmvc;

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
import org.springframework.restdocs.operation.ConversionException;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.restdocs.mockmvc.IterableEnumeration.iterable;

/**
 * A converter for creating an {@link OperationRequest} from a
 * {@link MockHttpServletRequest}.
 *
 * @author Andy Wilkinson
 *
 */
class MockMvcRequestConverter implements RequestConverter<MockHttpServletRequest> {

	private static final String SCHEME_HTTP = "http";

	private static final String SCHEME_HTTPS = "https";

	private static final int STANDARD_PORT_HTTP = 80;

	private static final int STANDARD_PORT_HTTPS = 443;

	@Override
	public OperationRequest convert(MockHttpServletRequest mockRequest) {
		try {
			HttpHeaders headers = extractHeaders(mockRequest);
			Parameters parameters = extractParameters(mockRequest);
			List<OperationRequestPart> parts = extractParts(mockRequest);
			String queryString = mockRequest.getQueryString();
			if (!StringUtils.hasText(queryString)
					&& "GET".equals(mockRequest.getMethod())) {
				queryString = parameters.toQueryString();
			}
			return new OperationRequestFactory().create(
					URI.create(
							getRequestUri(mockRequest) + (StringUtils.hasText(queryString)
									? "?" + queryString : "")),
					HttpMethod.valueOf(mockRequest.getMethod()),
					FileCopyUtils.copyToByteArray(mockRequest.getInputStream()), headers,
					parameters, parts);
		}
		catch (Exception ex) {
			throw new ConversionException(ex);
		}
	}

	private List<OperationRequestPart> extractParts(MockHttpServletRequest servletRequest)
			throws IOException, ServletException {
		List<OperationRequestPart> parts = new ArrayList<>();
		parts.addAll(extractServletRequestParts(servletRequest));
		if (servletRequest instanceof MockMultipartHttpServletRequest) {
			parts.addAll(extractMultipartRequestParts(
					(MockMultipartHttpServletRequest) servletRequest));
		}
		return parts;
	}

	private List<OperationRequestPart> extractServletRequestParts(
			MockHttpServletRequest servletRequest) throws IOException, ServletException {
		List<OperationRequestPart> parts = new ArrayList<>();
		for (Part part : servletRequest.getParts()) {
			parts.add(createOperationRequestPart(part));
		}
		return parts;
	}

	private OperationRequestPart createOperationRequestPart(Part part)
			throws IOException {
		HttpHeaders partHeaders = extractHeaders(part);
		List<String> contentTypeHeader = partHeaders.get(HttpHeaders.CONTENT_TYPE);
		if (part.getContentType() != null && contentTypeHeader == null) {
			partHeaders.setContentType(MediaType.parseMediaType(part.getContentType()));
		}
		return new OperationRequestPartFactory().create(part.getName(),
				StringUtils.hasText(part.getSubmittedFileName())
						? part.getSubmittedFileName() : null,
				FileCopyUtils.copyToByteArray(part.getInputStream()), partHeaders);
	}

	private List<OperationRequestPart> extractMultipartRequestParts(
			MockMultipartHttpServletRequest multipartRequest) throws IOException {
		List<OperationRequestPart> parts = new ArrayList<>();
		for (Entry<String, List<MultipartFile>> entry : multipartRequest.getMultiFileMap()
				.entrySet()) {
			for (MultipartFile file : entry.getValue()) {
				parts.add(createOperationRequestPart(file));
			}
		}
		return parts;
	}

	private OperationRequestPart createOperationRequestPart(MultipartFile file)
			throws IOException {
		HttpHeaders partHeaders = new HttpHeaders();
		if (StringUtils.hasText(file.getContentType())) {
			partHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
		}
		return new OperationRequestPartFactory().create(file.getName(),
				StringUtils.hasText(file.getOriginalFilename())
						? file.getOriginalFilename() : null,
				file.getBytes(), partHeaders);
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
		return (SCHEME_HTTP.equals(request.getScheme())
				&& request.getServerPort() != STANDARD_PORT_HTTP)
				|| (SCHEME_HTTPS.equals(request.getScheme())
						&& request.getServerPort() != STANDARD_PORT_HTTPS);
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
