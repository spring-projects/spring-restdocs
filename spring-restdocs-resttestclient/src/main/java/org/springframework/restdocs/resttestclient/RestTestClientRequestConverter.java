/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.resttestclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.multipart.FilePart;
import org.springframework.http.converter.multipart.MultipartHttpMessageConverter;
import org.springframework.http.converter.multipart.Part;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

/**
 * A {@link RequestConverter} for creating an {@link OperationRequest} derived from an
 * {@link EntityExchangeResult}.
 *
 * @author Andy Wilkinson
 */
class RestTestClientRequestConverter implements RequestConverter<ExchangeResult> {

	private static final ResolvableType MULTIVALUEMAP_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class,
			String.class, Part.class);

	private static final MultipartHttpMessageConverter MULTIPART_CONVERTER = new MultipartHttpMessageConverter();

	@Override
	public OperationRequest convert(ExchangeResult result) {
		HttpHeaders headers = extractRequestHeaders(result);
		return new OperationRequestFactory().create(result.getUrl(), result.getMethod(), result.getRequestBodyContent(),
				headers, extractRequestParts(result), extractCookies(headers));
	}

	private HttpHeaders extractRequestHeaders(ExchangeResult result) {
		HttpHeaders extracted = new HttpHeaders();
		extracted.putAll(result.getRequestHeaders());
		extracted.remove(RestTestClient.RESTTESTCLIENT_REQUEST_ID);
		return extracted;
	}

	private @Nullable List<OperationRequestPart> extractRequestParts(ExchangeResult result) {
		MockHttpInputMessage inputMessage = new MockHttpInputMessage(result.getRequestBodyContent());
		inputMessage.getHeaders().putAll(result.getRequestHeaders());
		try {
			MultiValueMap<String, Part> parts = MULTIPART_CONVERTER.read(MULTIVALUEMAP_TYPE, inputMessage, null);
			return parts.values().stream().flatMap(List::stream).map(this::createOperationRequestPart).toList();
		}
		catch (HttpMessageNotReadableException ex) {
			return Collections.emptyList();
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private OperationRequestPart createOperationRequestPart(Part part) {
		return new OperationRequestPartFactory().create(part.name(),
				(part instanceof FilePart) ? ((FilePart) part).filename() : null, readPartBodyContent(part),
				part.headers());
	}

	private byte[] readPartBodyContent(Part part) {
		try (InputStream content = part.content()) {
			return StreamUtils.copyToByteArray(content);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private Collection<RequestCookie> extractCookies(HttpHeaders headers) {
		List<String> cookieHeaders = headers.get(HttpHeaders.COOKIE);
		if (cookieHeaders == null) {
			return Collections.emptyList();
		}
		headers.remove(HttpHeaders.COOKIE);
		return cookieHeaders.stream()
			.flatMap((value) -> Stream.of(value.split("; ")))
			.map(this::createRequestCookie)
			.collect(Collectors.toList());
	}

	private RequestCookie createRequestCookie(String header) {
		int separator = header.indexOf('=');
		return new RequestCookie(header.substring(0, separator), header.substring(separator + 1));
	}

}
