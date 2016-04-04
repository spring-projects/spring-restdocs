/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs.restassured.operation.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.preprocess.ContentModifier;
import org.springframework.restdocs.operation.preprocess.ContentModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * An {@link OperationPreprocessor} that modifies URIs in the request and in the response
 * by changing one or more of their host, scheme, and port. URIs in the following
 * locations are modified:
 * <ul>
 * <li>{@link OperationRequest#getUri() Request URI}
 * <li>{@link OperationRequest#getHeaders() Request headers}
 * <li>{@link OperationRequest#getContent() Request content}
 * <li>{@link OperationRequestPart#getHeaders() Request part headers}
 * <li>{@link OperationRequestPart#getContent() Request part content}
 * <li>{@link OperationResponse#getHeaders() Response headers}
 * <li>{@link OperationResponse#getContent() Response content}
 * </ul>
 *
 * @author Andy Wilkinson
 */
public final class UriModifyingOperationPreprocessor implements OperationPreprocessor {

	private final UriModifyingContentModifier contentModifier = new UriModifyingContentModifier();

	private final OperationPreprocessor contentModifyingDelegate = new ContentModifyingOperationPreprocessor(
			this.contentModifier);

	private String scheme;

	private String host;

	private String port;

	/**
	 * Modifies the URI to use the given {@code scheme}. {@code null}, the default, will
	 * leave the scheme unchanged.
	 *
	 * @param scheme the scheme
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor scheme(String scheme) {
		this.scheme = scheme;
		this.contentModifier.setScheme(scheme);
		return this;
	}

	/**
	 * Modifies the URI to use the given {@code host}. {@code null}, the default, will
	 * leave the host unchanged.
	 *
	 * @param host the host
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor host(String host) {
		this.host = host;
		this.contentModifier.setHost(host);
		return this;
	}

	/**
	 * Modifies the URI to use the given {@code port}.
	 *
	 * @param port the port
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor port(int port) {
		return port(Integer.toString(port));
	}

	/**
	 * Removes the port from the URI.
	 *
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor removePort() {
		return port("");
	}

	private UriModifyingOperationPreprocessor port(String port) {
		this.port = port;
		this.contentModifier.setPort(port);
		return this;
	}

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(request.getUri());
		if (this.scheme != null) {
			uriBuilder.scheme(this.scheme);
		}
		if (this.host != null) {
			uriBuilder.host(this.host);
		}
		if (this.port != null) {
			if (StringUtils.hasText(this.port)) {
				uriBuilder.port(this.port);
			}
			else {
				uriBuilder.port(null);
			}
		}
		HttpHeaders modifiedHeaders = modify(request.getHeaders());
		if (this.host != null) {
			modifiedHeaders.set(HttpHeaders.HOST, this.host);
		}
		return this.contentModifyingDelegate.preprocess(
				new OperationRequestFactory().create(uriBuilder.build(true).toUri(),
						request.getMethod(), request.getContent(), modifiedHeaders,
						request.getParameters(), modify(request.getParts())));
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		return this.contentModifyingDelegate
				.preprocess(new OperationResponseFactory().create(response.getStatus(),
						modify(response.getHeaders()), response.getContent()));
	}

	private HttpHeaders modify(HttpHeaders headers) {
		HttpHeaders modified = new HttpHeaders();
		for (Entry<String, List<String>> header : headers.entrySet()) {
			for (String value : header.getValue()) {
				modified.add(header.getKey(), this.contentModifier.modify(value));
			}
		}
		return modified;
	}

	private Collection<OperationRequestPart> modify(
			Collection<OperationRequestPart> parts) {
		List<OperationRequestPart> modifiedParts = new ArrayList<>();
		OperationRequestPartFactory factory = new OperationRequestPartFactory();
		for (OperationRequestPart part : parts) {
			modifiedParts.add(factory.create(part.getName(), part.getSubmittedFileName(),
					this.contentModifier.modifyContent(part.getContent(),
							part.getHeaders().getContentType()),
					modify(part.getHeaders())));
		}
		return modifiedParts;
	}

	private static final class UriModifyingContentModifier implements ContentModifier {

		private static final Pattern SCHEME_HOST_PORT_PATTERN = Pattern
				.compile("(http[s]?)://([^/:#?]+)(:[0-9]+)?");

		private String scheme;

		private String host;

		private String port;

		private void setScheme(String scheme) {
			this.scheme = scheme;
		}

		private void setHost(String host) {
			this.host = host;
		}

		private void setPort(String port) {
			this.port = port;
		}

		@Override
		public byte[] modifyContent(byte[] content, MediaType contentType) {
			String input;
			if (contentType != null && contentType.getCharSet() != null) {
				input = new String(content, contentType.getCharSet());
			}
			else {
				input = new String(content);
			}

			return modify(input).getBytes();
		}

		private String modify(String input) {
			List<String> replacements = Arrays.asList(this.scheme, this.host,
					StringUtils.hasText(this.port) ? ":" + this.port : this.port);

			int previous = 0;

			Matcher matcher = SCHEME_HOST_PORT_PATTERN.matcher(input);
			StringBuilder builder = new StringBuilder();
			while (matcher.find()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					if (matcher.start(i) >= 0) {
						builder.append(input.substring(previous, matcher.start(i)));
					}
					if (matcher.start(i) >= 0) {
						previous = matcher.end(i);
					}
					builder.append(
							getReplacement(matcher.group(i), replacements.get(i - 1)));
				}
			}

			if (previous < input.length()) {
				builder.append(input.substring(previous));
			}
			return builder.toString();
		}

		private String getReplacement(String original, String candidate) {
			if (candidate != null) {
				return candidate;
			}
			if (original != null) {
				return original;
			}
			return "";
		}
	}
}
