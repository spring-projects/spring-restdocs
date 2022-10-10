/*
 * Copyright 2014-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.mockmvc;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;

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
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * A converter for creating an {@link OperationRequest} from a
 * {@link MockHttpServletRequest}.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
class MockMvcRequestConverter implements RequestConverter<MockHttpServletRequest> {

	@Override
	public OperationRequest convert(MockHttpServletRequest mockRequest) {
		try {
			HttpHeaders headers = extractHeaders(mockRequest);
			List<OperationRequestPart> parts = extractParts(mockRequest);
			Collection<RequestCookie> cookies = extractCookies(mockRequest, headers);
			return new OperationRequestFactory().create(getRequestUri(mockRequest),
					HttpMethod.valueOf(mockRequest.getMethod()), getRequestContent(mockRequest, headers), headers,
					parts, cookies);
		}
		catch (Exception ex) {
			throw new ConversionException(ex);
		}
	}

	private URI getRequestUri(MockHttpServletRequest mockRequest) {
		String queryString = "";
		if (mockRequest.getQueryString() != null) {
			queryString = mockRequest.getQueryString();
		}
		else if ("GET".equals(mockRequest.getMethod()) || mockRequest.getContentLengthLong() > 0) {
			queryString = urlEncodedParameters(mockRequest);
		}
		StringBuffer requestUrlBuffer = mockRequest.getRequestURL();
		if (queryString.length() > 0) {
			requestUrlBuffer.append("?").append(queryString.toString());
		}
		return URI.create(requestUrlBuffer.toString());
	}

	private String urlEncodedParameters(MockHttpServletRequest mockRequest) {
		StringBuilder parameters = new StringBuilder();
		MultiValueMap<String, String> queryParameters = parse(mockRequest.getQueryString());
		for (String name : IterableEnumeration.of(mockRequest.getParameterNames())) {
			if (!queryParameters.containsKey(name)) {
				String[] values = mockRequest.getParameterValues(name);
				if (values.length == 0) {
					append(parameters, name);
				}
				else {
					for (String value : values) {
						append(parameters, name, value);
					}
				}
			}
		}
		return parameters.toString();
	}

	private byte[] getRequestContent(MockHttpServletRequest mockRequest, HttpHeaders headers) {
		byte[] content = mockRequest.getContentAsByteArray();
		if ("GET".equals(mockRequest.getMethod())) {
			return content;
		}
		MediaType contentType = headers.getContentType();
		if (contentType == null || MediaType.APPLICATION_FORM_URLENCODED.includes(contentType)) {
			Map<String, String[]> parameters = mockRequest.getParameterMap();
			if (!parameters.isEmpty() && (content == null || content.length == 0)) {
				StringBuilder contentBuilder = new StringBuilder();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				MultiValueMap<String, String> queryParameters = parse(mockRequest.getQueryString());
				mockRequest.getParameterMap().forEach((name, values) -> {
					List<String> queryParameterValues = queryParameters.get(name);
					if (values.length == 0) {
						if (queryParameterValues == null) {
							append(contentBuilder, name);
						}
					}
					else {
						for (String value : values) {
							if (queryParameterValues == null || !queryParameterValues.contains(value)) {
								append(contentBuilder, name, value);
							}
						}
					}
				});
				return contentBuilder.toString().getBytes(StandardCharsets.UTF_8);
			}
		}
		return content;
	}

	private Collection<RequestCookie> extractCookies(MockHttpServletRequest mockRequest, HttpHeaders headers) {
		if (mockRequest.getCookies() == null || mockRequest.getCookies().length == 0) {
			return Collections.emptyList();
		}
		List<RequestCookie> cookies = new ArrayList<>();
		for (jakarta.servlet.http.Cookie servletCookie : mockRequest.getCookies()) {
			cookies.add(new RequestCookie(servletCookie.getName(), servletCookie.getValue()));
		}
		headers.remove(HttpHeaders.COOKIE);
		return cookies;
	}

	private List<OperationRequestPart> extractParts(MockHttpServletRequest servletRequest)
			throws IOException, ServletException {
		List<OperationRequestPart> parts = new ArrayList<>();
		parts.addAll(extractServletRequestParts(servletRequest));
		if (servletRequest instanceof MockMultipartHttpServletRequest) {
			parts.addAll(extractMultipartRequestParts((MockMultipartHttpServletRequest) servletRequest));
		}
		return parts;
	}

	private List<OperationRequestPart> extractServletRequestParts(MockHttpServletRequest servletRequest)
			throws IOException, ServletException {
		List<OperationRequestPart> parts = new ArrayList<>();
		for (Part part : servletRequest.getParts()) {
			parts.add(createOperationRequestPart(part));
		}
		return parts;
	}

	private OperationRequestPart createOperationRequestPart(Part part) throws IOException {
		HttpHeaders partHeaders = extractHeaders(part);
		List<String> contentTypeHeader = partHeaders.get(HttpHeaders.CONTENT_TYPE);
		if (part.getContentType() != null && contentTypeHeader == null) {
			partHeaders.setContentType(MediaType.parseMediaType(part.getContentType()));
		}
		return new OperationRequestPartFactory().create(part.getName(),
				StringUtils.hasText(part.getSubmittedFileName()) ? part.getSubmittedFileName() : null,
				FileCopyUtils.copyToByteArray(part.getInputStream()), partHeaders);
	}

	private List<OperationRequestPart> extractMultipartRequestParts(MockMultipartHttpServletRequest multipartRequest)
			throws IOException {
		List<OperationRequestPart> parts = new ArrayList<>();
		for (Entry<String, List<MultipartFile>> entry : multipartRequest.getMultiFileMap().entrySet()) {
			for (MultipartFile file : entry.getValue()) {
				parts.add(createOperationRequestPart(file));
			}
		}
		return parts;
	}

	private OperationRequestPart createOperationRequestPart(MultipartFile file) throws IOException {
		HttpHeaders partHeaders = new HttpHeaders();
		if (StringUtils.hasText(file.getContentType())) {
			partHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
		}
		return new OperationRequestPartFactory().create(file.getName(),
				StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : null, file.getBytes(),
				partHeaders);
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

	private HttpHeaders extractHeaders(MockHttpServletRequest servletRequest) {
		HttpHeaders headers = new HttpHeaders();
		for (String headerName : IterableEnumeration.of(servletRequest.getHeaderNames())) {
			for (String value : IterableEnumeration.of(servletRequest.getHeaders(headerName))) {
				headers.add(headerName, value);
			}
		}
		return headers;
	}

	private static void append(StringBuilder sb, String key) {
		append(sb, key, "");
	}

	private static void append(StringBuilder sb, String key, String value) {
		doAppend(sb, urlEncode(key) + "=" + urlEncode(value));
	}

	private static void doAppend(StringBuilder sb, String toAppend) {
		if (sb.length() > 0) {
			sb.append("&");
		}
		sb.append(toAppend);
	}

	private static String urlEncode(String s) {
		if (!StringUtils.hasLength(s)) {
			return "";
		}
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	private static MultiValueMap<String, String> parse(String query) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		if (!StringUtils.hasLength(query)) {
			return parameters;
		}
		try (Scanner scanner = new Scanner(query)) {
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
				processParameter(scanner.next(), parameters);
			}
		}
		return parameters;
	}

	private static void processParameter(String parameter, MultiValueMap<String, String> parameters) {
		String[] components = parameter.split("=");
		if (components.length > 0 && components.length < 3) {
			if (components.length == 2) {
				String name = components[0];
				String value = components[1];
				parameters.add(decode(name), decode(value));
			}
			else {
				List<String> values = parameters.computeIfAbsent(components[0], (p) -> new LinkedList<>());
				values.add("");
			}
		}
		else {
			throw new IllegalArgumentException("The parameter '" + parameter + "' is malformed");
		}
	}

	private static String decode(String encoded) {
		return URLDecoder.decode(encoded, StandardCharsets.US_ASCII);
	}

}
