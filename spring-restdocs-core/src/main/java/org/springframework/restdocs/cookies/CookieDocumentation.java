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

package org.springframework.restdocs.cookies;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.snippet.Snippet;

/**
 * Static factory methods for documenting a RESTful API's request and response cookies.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 * @since 3.0
 */
public abstract class CookieDocumentation {

	private CookieDocumentation() {

	}

	/**
	 * Creates a {@code CookieDescriptor} that describes a cookie with the given
	 * {@code name}.
	 * @param name the name of the cookie
	 * @return a {@code CookieDescriptor} ready for further configuration
	 */
	public static CookieDescriptor cookieWithName(String name) {
		return new CookieDescriptor(name);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * request. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur.
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet requestCookies(CookieDescriptor... descriptors) {
		return requestCookies(Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * request. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur.
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet requestCookies(List<CookieDescriptor> descriptors) {
		return new RequestCookiesSnippet(descriptors);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * request. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented cookies will be ignored.
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet relaxedRequestCookies(CookieDescriptor... descriptors) {
		return relaxedRequestCookies(Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * request. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented cookies will be ignored.
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet relaxedRequestCookies(List<CookieDescriptor> descriptors) {
		return new RequestCookiesSnippet(descriptors, true);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's request. The given {@code attributes} will be available during snippet
	 * generation and the cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet requestCookies(Map<String, Object> attributes,
			CookieDescriptor... descriptors) {
		return requestCookies(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's request. The given {@code attributes} will be available during snippet
	 * generation and the cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the request,
	 * a failure will also occur.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet requestCookies(Map<String, Object> attributes,
			List<CookieDescriptor> descriptors) {
		return new RequestCookiesSnippet(descriptors, attributes);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's request. The given {@code attributes} will be available during snippet
	 * generation and the cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented cookies will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet relaxedRequestCookies(Map<String, Object> attributes,
			CookieDescriptor... descriptors) {
		return relaxedRequestCookies(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's request. The given {@code attributes} will be available during snippet
	 * generation and the cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented cookies will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's cookies
	 * @return the snippet that will document the request cookies
	 * @see #cookieWithName(String)
	 */
	public static RequestCookiesSnippet relaxedRequestCookies(Map<String, Object> attributes,
			List<CookieDescriptor> descriptors) {
		return new RequestCookiesSnippet(descriptors, attributes, true);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * response. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will also occur.
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet responseCookies(CookieDescriptor... descriptors) {
		return responseCookies(Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * response. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will also occur.
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet responseCookies(List<CookieDescriptor> descriptors) {
		return new ResponseCookiesSnippet(descriptors);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * response. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented cookies will be ignored.
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet relaxedResponseCookies(CookieDescriptor... descriptors) {
		return relaxedResponseCookies(Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API operation's
	 * response. The cookies will be documented using the given {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented cookies will be ignored.
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet relaxedResponseCookies(List<CookieDescriptor> descriptors) {
		return new ResponseCookiesSnippet(descriptors, true);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's response. The given {@code attributes} will be available during
	 * snippet generation and the cookies will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a cookie is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will also occur.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet responseCookies(Map<String, Object> attributes,
			CookieDescriptor... descriptors) {
		return responseCookies(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's response. The given {@code attributes} will be available during
	 * snippet generation and the cookies will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a cookie is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will also occur.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet responseCookies(Map<String, Object> attributes,
			List<CookieDescriptor> descriptors) {
		return new ResponseCookiesSnippet(descriptors, attributes);
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's response. The given {@code attributes} will be available during
	 * snippet generation and the cookies will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented cookies will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet relaxedResponseCookies(Map<String, Object> attributes,
			CookieDescriptor... descriptors) {
		return relaxedResponseCookies(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the cookies of the API
	 * operations's response. The given {@code attributes} will be available during
	 * snippet generation and the cookies will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a cookie is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented cookies will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's cookies
	 * @return the snippet that will document the response cookies
	 * @see #cookieWithName(String)
	 */
	public static ResponseCookiesSnippet relaxedResponseCookies(Map<String, Object> attributes,
			List<CookieDescriptor> descriptors) {
		return new ResponseCookiesSnippet(descriptors, attributes, true);
	}

}
