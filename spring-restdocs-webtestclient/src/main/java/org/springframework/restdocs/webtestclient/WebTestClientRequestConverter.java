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

package org.springframework.restdocs.webtestclient;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.QueryStringParser;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * A {@link RequestConverter} for creating an {@link OperationRequest} derived from an
 * {@link ExchangeResult}.
 *
 * @author Andy Wilkinson
 */
class WebTestClientRequestConverter implements RequestConverter<ExchangeResult> {

	private static final ResolvableType FORM_DATA_TYPE = ResolvableType
			.forClassWithGenerics(MultiValueMap.class, String.class, String.class);

	private final QueryStringParser queryStringParser = new QueryStringParser();

	private final FormHttpMessageReader formDataReader = new FormHttpMessageReader();

	@Override
	public OperationRequest convert(ExchangeResult result) {
		HttpHeaders headers = extractRequestHeaders(result);
		return new OperationRequestFactory().create(result.getUrl(), result.getMethod(),
				result.getRequestBodyContent(), headers, extractParameters(result),
				extractRequestParts(result), extractCookies(headers));
	}

	private HttpHeaders extractRequestHeaders(ExchangeResult result) {
		HttpHeaders extracted = new HttpHeaders();
		extracted.putAll(result.getRequestHeaders());
		extracted.remove(WebTestClient.WEBTESTCLIENT_REQUEST_ID);
		return extracted;
	}

	private Parameters extractParameters(ExchangeResult result) {
		Parameters parameters = new Parameters();
		parameters.addAll(this.queryStringParser.parse(result.getUrl()));
		if (MediaType.APPLICATION_FORM_URLENCODED
				.isCompatibleWith(result.getRequestHeaders().getContentType())) {
			parameters.addAll(this.formDataReader
					.readMono(FORM_DATA_TYPE,
							new ExchangeResultReactiveHttpInputMessage(result), null)
					.block());
		}
		return parameters;
	}

	private List<OperationRequestPart> extractRequestParts(ExchangeResult result) {
		if (!ClassUtils.isPresent(
				"org.synchronoss.cloud.nio.multipart.NioMultipartParserListener",
				getClass().getClassLoader())) {
			return Collections.emptyList();
		}
		return new MultipartHttpMessageReader(new SynchronossPartHttpMessageReader())
				.readMono(ResolvableType.forClass(Part.class),
						new ExchangeResultReactiveHttpInputMessage(result),
						Collections.emptyMap())
				.onErrorReturn(new LinkedMultiValueMap<>()).block().values().stream()
				.flatMap((parts) -> parts.stream().map(this::createOperationRequestPart))
				.collect(Collectors.toList());
	}

	private OperationRequestPart createOperationRequestPart(Part part) {
		ByteArrayOutputStream content = readPartBodyContent(part);
		return new OperationRequestPartFactory().create(part.name(),
				part instanceof FilePart ? ((FilePart) part).filename() : null,
				content.toByteArray(), part.headers());
	}

	private ByteArrayOutputStream readPartBodyContent(Part part) {
		ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
		DataBufferUtils.write(part.content(), contentStream).blockFirst();
		return contentStream;
	}

	private Collection<RequestCookie> extractCookies(HttpHeaders headers) {
		List<String> cookieHeaders = headers.get(HttpHeaders.COOKIE);
		if (cookieHeaders == null) {
			return Collections.emptyList();
		}
		headers.remove(HttpHeaders.COOKIE);
		return cookieHeaders.stream().map(this::createRequestCookie)
				.collect(Collectors.toList());
	}

	private RequestCookie createRequestCookie(String header) {
		String[] components = header.split("=");
		return new RequestCookie(components[0], components[1]);
	}

	private final class ExchangeResultReactiveHttpInputMessage
			implements ReactiveHttpInputMessage {

		private final ExchangeResult result;

		private ExchangeResultReactiveHttpInputMessage(ExchangeResult result) {
			this.result = result;
		}

		@Override
		public HttpHeaders getHeaders() {
			return this.result.getRequestHeaders();
		}

		@Override
		public Flux<DataBuffer> getBody() {
			DefaultDataBuffer buffer = new DefaultDataBufferFactory().allocateBuffer();
			buffer.write(this.result.getRequestBodyContent());
			return Flux.fromArray(new DataBuffer[] { buffer });
		}

	}

}
