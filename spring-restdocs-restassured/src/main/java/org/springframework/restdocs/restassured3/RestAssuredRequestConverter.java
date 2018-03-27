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

package org.springframework.restdocs.restassured3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.MultiPartSpecification;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

/**
 * A converter for creating an {@link OperationRequest} from a REST Assured
 * {@link FilterableRequestSpecification}.
 *
 * @author Andy Wilkinson
 */
class RestAssuredRequestConverter
		implements RequestConverter<FilterableRequestSpecification> {

	@Override
	public OperationRequest convert(FilterableRequestSpecification requestSpec) {
		return new OperationRequestFactory().create(URI.create(requestSpec.getURI()),
				HttpMethod.valueOf(requestSpec.getMethod()), extractContent(requestSpec),
				extractHeaders(requestSpec), extractParameters(requestSpec),
				extractParts(requestSpec), extractCookies(requestSpec));
	}

	private Collection<RequestCookie> extractCookies(
			FilterableRequestSpecification requestSpec) {
		Collection<RequestCookie> cookies = new ArrayList<>();
		for (Cookie cookie : requestSpec.getCookies()) {
			cookies.add(new RequestCookie(cookie.getName(), cookie.getValue()));
		}
		return cookies;
	}

	private byte[] extractContent(FilterableRequestSpecification requestSpec) {
		return convertContent(requestSpec.getBody());
	}

	private byte[] convertContent(Object content) {
		if (content instanceof String) {
			return ((String) content).getBytes();
		}
		else if (content instanceof byte[]) {
			return (byte[]) content;
		}
		else if (content instanceof File) {
			return copyToByteArray((File) content);
		}
		else if (content instanceof InputStream) {
			return copyToByteArray((InputStream) content);
		}
		else if (content == null) {
			return new byte[0];
		}
		else {
			throw new IllegalStateException(
					"Unsupported request content: " + content.getClass().getName());
		}
	}

	private byte[] copyToByteArray(File file) {
		try {
			return FileCopyUtils.copyToByteArray(file);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Failed to read content from file " + file,
					ex);
		}
	}

	private byte[] copyToByteArray(InputStream inputStream) {
		try {
			inputStream.reset();
		}
		catch (IOException ex) {
			throw new IllegalStateException("Cannot read content from input stream "
					+ inputStream + " due to reset() failure");
		}
		try {
			return StreamUtils.copyToByteArray(inputStream);
		}
		catch (IOException ex) {
			throw new IllegalStateException(
					"Failed to read content from input stream " + inputStream, ex);
		}
	}

	private HttpHeaders extractHeaders(FilterableRequestSpecification requestSpec) {
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Header header : requestSpec.getHeaders()) {
			if (!isAllMediaTypesAcceptHeader(header)) {
				httpHeaders.add(header.getName(), header.getValue());
			}
		}
		return httpHeaders;
	}

	private boolean isAllMediaTypesAcceptHeader(Header header) {
		return HttpHeaders.ACCEPT.equals(header.getName())
				&& "*/*".equals(header.getValue());
	}

	private Parameters extractParameters(FilterableRequestSpecification requestSpec) {
		Parameters parameters = new Parameters();
		for (Entry<String, ?> entry : requestSpec.getQueryParams().entrySet()) {
			if (entry.getValue() instanceof Collection) {
				Collection<?> queryParams = ((Collection<?>) entry.getValue());
				for (Object queryParam : queryParams) {
					parameters.add(entry.getKey(), queryParam.toString());
				}
			}
			else {
				parameters.add(entry.getKey(), entry.getValue().toString());
			}
		}
		for (Entry<String, ?> entry : requestSpec.getRequestParams().entrySet()) {
			parameters.add(entry.getKey(), entry.getValue().toString());
		}
		for (Entry<String, ?> entry : requestSpec.getFormParams().entrySet()) {
			parameters.add(entry.getKey(), entry.getValue().toString());
		}
		return parameters;
	}

	private Collection<OperationRequestPart> extractParts(
			FilterableRequestSpecification requestSpec) {
		List<OperationRequestPart> parts = new ArrayList<>();
		for (MultiPartSpecification multiPartSpec : requestSpec.getMultiPartParams()) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(
					multiPartSpec.getMimeType() == null ? MediaType.TEXT_PLAIN
							: MediaType.parseMediaType(multiPartSpec.getMimeType()));
			parts.add(new OperationRequestPartFactory().create(
					multiPartSpec.getControlName(), multiPartSpec.getFileName(),
					convertContent(multiPartSpec.getContent()), headers));
		}
		return parts;
	}

}
