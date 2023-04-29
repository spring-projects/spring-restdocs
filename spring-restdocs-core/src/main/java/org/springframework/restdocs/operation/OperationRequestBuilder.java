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

package org.springframework.restdocs.operation;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * A builder for creating {@link OperationRequest OperationRequests}.
 *
 * @author Augusto Ravazoli
 */
public class OperationRequestBuilder {

  private URI uri;
  private HttpMethod method;
  private byte[] content;
  private HttpHeaders headers;
  private Collection<OperationRequestPart> parts;
  private Collection<RequestCookie> cookies;

  /**
   * Creates a new OperationRequestBuilder.
   * @return the {@code OperationRequestBuilder}.
   */
  public OperationRequestBuilder() {}

  /**
   * Creates a new OperationRequestBuilder using an existing {@link OperationRequest} as default.
   * @param The original {@link OperationRequest}.
   * @return {@code OperationRequestBuilder}.
   */
  public OperationRequestBuilder(OperationRequest original) {
    uri = original.getUri();
    method = original.getMethod();
    content = original.getContent();
    headers = original.getHeaders();
    parts = original.getParts();
    cookies = original.getCookies();
  }

  /**
   * Sets the URI of the request.
   * @param uri the request's uri.
   * @return a reference to this object.
   */
  public OperationRequestBuilder uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Sets the HTTP method of the request.
   * @param method the request's method.
   * @return a reference to this object.
   */
  public OperationRequestBuilder method(HttpMethod method) {
    this.method = method;
    return this;
  }

  /**
   * Sets the content of the request.
   * @param content the request's content.
   * @return a reference to this object.
   */
  public OperationRequestBuilder content(byte[] content) {
    this.content = content;
    return this;
  }

  /**
   * Sets the headers of the request.
   * @param headers the request's headers.
   * @return a reference to this object.
   */
  public OperationRequestBuilder headers(HttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  /**
   * Sets the parts of the request.
   * @param parts the request's parts.
   * @return a reference to this object.
   */
  public OperationRequestBuilder parts(Collection<OperationRequestPart> parts) {
    this.parts = parts;
    return this;
  }

  /**
   * Sets the cookies of the request.
   * @param cookies the request's cookies.
   * @return a reference to this object.
   */
  public OperationRequestBuilder cookies(Collection<RequestCookie> cookies) {
    this.cookies = cookies;
    return this;
  }

  /**
   * Builds the new operation request object. The {@code headers} will be augmented
	 * to ensure that they always include a {@code Content-Length} header if the request
	 * has any content and a {@code Host} header.
   * @return the {@code OperationRequest}.
   */
  public OperationRequest build() {
    return new StandardOperationRequest(
      uri,
      method,
      content,
      augmentHeaders(headers, uri, content),
      parts != null ? parts : Collections.emptyList(),
      cookies != null ? cookies : Collections.emptyList()
    );
  }

  private HttpHeaders augmentHeaders(HttpHeaders originalHeaders, URI uri, byte[] content) {
    String hostHeader = createHostHeader(uri);
    return new HttpHeadersHelper(originalHeaders)
      .addIfAbsent(HttpHeaders.HOST, hostHeader)
      .setContentLengthHeader(content)
      .getHeaders();
  }

  private String createHostHeader(URI uri) {
    if (uri.getPort() == -1) {
      return uri.getHost();
    }
    return uri.getHost() + ":" + uri.getPort();
  }

}
