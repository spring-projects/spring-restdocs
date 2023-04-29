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

import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * A builder for creating {@code OperationResponse OperationsResponses}.
 *
 * @author Augusto Ravazoli
 */
public class OperationResponseBuilder {

  private HttpStatusCode status;
  private HttpHeaders headers;
  private byte[] content;
  private Collection<ResponseCookie> cookies;

  /**
   * Creates a new OperationResponseBuilder.
   * @return the {@code OperationResponseBuilder}.
   */
  public OperationResponseBuilder() {}

  /**
   * Creates a new OperationResponseBuilder using an existing {@link OperationResponse} as default.
   * @param The original {@link OperationResponse}.
   * @return {@code OperationResponseBuilder}.
   */
  public OperationResponseBuilder(OperationResponse original) {
    status = original.getStatus();
    headers = original.getHeaders();
    content = original.getContent();
    cookies = original.getCookies();
  }

  /**
   * Sets the status code of the response.
   * @param status the response's status.
   * @return a reference to this object.
   */
  public OperationResponseBuilder status(HttpStatusCode status) {
    this.status = status;
    return this;
  }

  /**
   * Sets the headers of the response.
   * @param headers the response's headers.
   * @return a reference to this object.
   */
  public OperationResponseBuilder headers(HttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  /**
   * Sets the content of the response.
   * @param content the request's content.
   * @return a reference to this object.
   */
  public OperationResponseBuilder content(byte[] content) {
    this.content = content;
    return this;
  }

  /**
   * Sets the cookies of the response.
   * @param cookies the response's cookies.
   * @return a reference to this object.
   */
  public OperationResponseBuilder cookies(Collection<ResponseCookie> cookies) {
    this.cookies = cookies;
    return this;
  }

  /**
   * Builds the new operation response object. If the response has any content, the given
   * {@code headers} will be augmented to ensure that they include a
   * {@code Content-Length} header.
   * @return the {@code OperationResponse}.
   */
  public OperationResponse build() {
    return new StandardOperationResponse(
      status,
      augmentHeaders(headers, content),
      content,
      cookies != null ? cookies : Collections.emptyList()
    );
  }

  private HttpHeaders augmentHeaders(HttpHeaders originalHeaders, byte[] content) {
    return new HttpHeadersHelper(originalHeaders).setContentLengthHeader(content).getHeaders();
  }

}
