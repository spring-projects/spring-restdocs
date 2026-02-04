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

package org.springframework.restdocs.operation.preprocess;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
	 * An {@link OperationPreprocessor} that modifies URIs in the request and in the response
	 * by changing one or more of their host, scheme, port, and path. URIs in the following
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
 * @since 2.0.1
 */
public class UriModifyingOperationPreprocessor implements OperationPreprocessor {

	private final UriModifyingContentModifier contentModifier = new UriModifyingContentModifier();

	private final OperationPreprocessor contentModifyingDelegate = new ContentModifyingOperationPreprocessor(
			this.contentModifier);

	private @Nullable String scheme;

	private @Nullable String host;

	private @Nullable String port;

	private @Nullable String pathPrefix;

	/**
	 * Modifies the URI to use the given {@code scheme}. {@code null}, the default, will
	 * leave the scheme unchanged.
	 * @param scheme the scheme
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor scheme(@Nullable String scheme) {
		this.scheme = scheme;
		this.contentModifier.setScheme(scheme);
		return this;
	}

	/**
	 * Modifies the URI to use the given {@code host}. {@code null}, the default, will
	 * leave the host unchanged.
	 * @param host the host
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor host(@Nullable String host) {
		this.host = host;
		this.contentModifier.setHost(host);
		return this;
	}

	/**
	 * Modifies the URI to use the given {@code port}.
	 * @param port the port
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor port(int port) {
		return port(Integer.toString(port));
	}

	/**
	 * Removes the port from the URI.
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

	/**
	 * Adds the given {@code pathPrefix} to the URI's path. {@code null}, the default, will
	 * leave the path unchanged.
	 * @param pathPrefix the path prefix to add
	 * @return {@code this}
	 */
	public UriModifyingOperationPreprocessor pathPrefix(@Nullable String pathPrefix) {
		this.pathPrefix = pathPrefix;
		this.contentModifier.setPathPrefix(pathPrefix);
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
		if (this.pathPrefix != null) {
			String rawPath = request.getUri().getRawPath();
			uriBuilder.replacePath(prefixPath(this.pathPrefix, rawPath));
		}
		URI modifiedUri = uriBuilder.build(true).toUri();
		HttpHeaders modifiedHeaders = modify(request.getHeaders());
		modifiedHeaders.set(HttpHeaders.HOST,
				modifiedUri.getHost() + ((modifiedUri.getPort() != -1) ? ":" + modifiedUri.getPort() : ""));
		return this.contentModifyingDelegate
			.preprocess(new OperationRequestFactory().create(uriBuilder.build(true).toUri(), request.getMethod(),
					request.getContent(), modifiedHeaders, modify(request.getParts()), request.getCookies()));
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		return this.contentModifyingDelegate.preprocess(new OperationResponseFactory().create(response.getStatus(),
				modify(response.getHeaders()), response.getContent(), response.getCookies()));
	}

	private HttpHeaders modify(HttpHeaders headers) {
		HttpHeaders modified = new HttpHeaders();
		for (Entry<String, List<String>> header : headers.headerSet()) {
			for (String value : header.getValue()) {
				modified.add(header.getKey(), this.contentModifier.modify(value));
			}
		}
		return modified;
	}

	private Collection<OperationRequestPart> modify(Collection<OperationRequestPart> parts) {
		List<OperationRequestPart> modifiedParts = new ArrayList<>();
		OperationRequestPartFactory factory = new OperationRequestPartFactory();
		for (OperationRequestPart part : parts) {
			modifiedParts.add(factory.create(part.getName(), part.getSubmittedFileName(),
					this.contentModifier.modifyContent(part.getContent(), part.getHeaders().getContentType()),
					modify(part.getHeaders())));
		}
		return modifiedParts;
	}

	private String prefixPath(String pathPrefix, @Nullable String rawPath) {
		String normalizedPrefix = normalizePrefix(pathPrefix);
		String normalizedPath = normalizePath(rawPath);
		if (!StringUtils.hasText(normalizedPath)) {
			return normalizedPrefix;
		}
		return normalizedPrefix + "/" + normalizedPath;
	}

	private String normalizePrefix(String pathPrefix) {
		String prefix = pathPrefix;
		if (!prefix.startsWith("/")) {
			prefix = "/" + prefix;
		}
		while (prefix.endsWith("/") && prefix.length() > 1) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		return prefix;
	}

	private String normalizePath(@Nullable String rawPath) {
		if (!StringUtils.hasText(rawPath) || "/".equals(rawPath)) {
			return "";
		}
		String path = rawPath;
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	private static final class UriModifyingContentModifier implements ContentModifier {

		private static final Pattern URI_PATTERN = Pattern.compile("(http[s]?://[^\\s\"']+)");

		private @Nullable String scheme;

		private @Nullable String host;

		private @Nullable String port;

		private @Nullable String pathPrefix;

		private void setScheme(@Nullable String scheme) {
			this.scheme = scheme;
		}

		private void setHost(@Nullable String host) {
			this.host = host;
		}

		private void setPort(@Nullable String port) {
			this.port = port;
		}

		private void setPathPrefix(@Nullable String pathPrefix) {
			this.pathPrefix = pathPrefix;
		}

		@Override
		public byte[] modifyContent(byte[] content, @Nullable MediaType contentType) {
			String input;
			if (contentType != null && contentType.getCharset() != null) {
				input = new String(content, contentType.getCharset());
			}
			else {
				input = new String(content);
			}

			return modify(input).getBytes();
		}

		private String modify(String input) {
			if (this.scheme == null && this.host == null && this.port == null && this.pathPrefix == null) {
				return input;
			}
			int previous = 0;
			Matcher matcher = URI_PATTERN.matcher(input);
			StringBuilder builder = new StringBuilder();
			while (matcher.find()) {
				builder.append(input, previous, matcher.start());
				String original = matcher.group(1);
				builder.append(modifyUriString(original));
				previous = matcher.end();
			}
			if (previous < input.length()) {
				builder.append(input.substring(previous));
			}
			return builder.toString();
		}

		private String modifyUriString(String original) {
			try {
				UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(original);
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
				if (this.pathPrefix != null) {
					String existingPath = uriBuilder.build(true).getPath();
					String prefix = this.pathPrefix;
					String normalizedPrefix = prefix.startsWith("/") ? prefix : "/" + prefix;
					if (normalizedPrefix.endsWith("/") && normalizedPrefix.length() > 1) {
						normalizedPrefix = normalizedPrefix.substring(0, normalizedPrefix.length() - 1);
					}
					String normalizedPath = (existingPath == null || existingPath.isEmpty() || "/".equals(existingPath))
							? ""
							: existingPath.startsWith("/") ? existingPath.substring(1) : existingPath;
					uriBuilder.replacePath(normalizedPath.isEmpty() ? normalizedPrefix
							: normalizedPrefix + "/" + normalizedPath);
				}
				return uriBuilder.build(true).toUriString();
			}
			catch (IllegalArgumentException ex) {
				return original;
			}
		}

	}

}
