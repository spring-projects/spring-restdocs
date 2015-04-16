/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.util;

import static org.springframework.restdocs.util.IterableEnumeration.iterable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * An {@link HttpServletRequest} wrapper that provides a limited set of methods intended
 * to help in the documentation of the request.
 * 
 * @author Andy Wilkinson
 * @author Dmitriy Mayboroda
 *
 */
public class DocumentableHttpServletRequest {

	private final MockHttpServletRequest delegate;

	/**
	 * Creates a new {@link DocumentableHttpServletRequest} to document the given
	 * {@code request}.
	 * 
	 * @param request the request that is to be documented
	 */
	public DocumentableHttpServletRequest(MockHttpServletRequest request) {
		this.delegate = request;
	}

	/**
	 * Whether or not this request is a {@code GET} request.
	 * 
	 * @return {@code true} if it is a {@code GET} request, otherwise {@code false}
	 * @see HttpServletRequest#getMethod()
	 */
	public boolean isGetRequest() {
		return RequestMethod.GET == RequestMethod.valueOf(this.delegate.getMethod());
	}

	/**
	 * Whether or not this request is a {@code POST} request.
	 * 
	 * @return {@code true} if it is a {@code POST} request, otherwise {@code false}
	 * @see HttpServletRequest#getMethod()
	 */
	public boolean isPostRequest() {
		return RequestMethod.POST == RequestMethod.valueOf(this.delegate.getMethod());
	}

	/**
	 * Returns a Map of the request's headers. The entries are ordered based on the
	 * ordering of {@link HttpServletRequest#getHeaderNames()} and
	 * {@link HttpServletRequest#getHeaders(String)}.
	 * 
	 * @return the request's headers, keyed by name
	 * @see HttpServletRequest#getHeaderNames()
	 * @see HttpServletRequest#getHeaders(String)
	 */
	public Map<String, List<String>> getHeaders() {
		Map<String, List<String>> headersByName = new LinkedHashMap<String, List<String>>();
		for (String headerName : iterable(this.delegate.getHeaderNames())) {
			List<String> headers = new ArrayList<String>();
			headersByName.put(headerName, headers);
			for (String header : iterable(this.delegate.getHeaders(headerName))) {
				headers.add(header);
			}
		}
		return headersByName;
	}

	/**
	 * Returns the request's scheme.
	 * 
	 * @return the request's scheme
	 * @see HttpServletRequest#getScheme()
	 */
	public String getScheme() {
		return this.delegate.getScheme();
	}

	/**
	 * Returns the name of the host to which the request was sent.
	 * 
	 * @return the host's name
	 * @see HttpServletRequest#getServerName()
	 */
	public String getHost() {
		return this.delegate.getServerName();
	}

	/**
	 * Returns the port to which the request was sent.
	 * 
	 * @return the port
	 * @see HttpServletRequest#getServerPort()
	 */
	public int getPort() {
		return this.delegate.getServerPort();
	}

	/**
	 * Returns the request's method.
	 * 
	 * @return the request's method
	 * @see HttpServletRequest#getMethod()
	 */
	public String getMethod() {
		return this.delegate.getMethod();
	}

	/**
	 * Returns the length of the request's content
	 * 
	 * @return the content length
	 * @see HttpServletRequest#getContentLength()
	 */
	public long getContentLength() {
		return this.delegate.getContentLengthLong();
	}

	/**
	 * Returns a {@code String} of the request's content
	 * 
	 * @return the request's content
	 * @throws IOException if the content cannot be read
	 */
	public String getContentAsString() throws IOException {
		StringWriter bodyWriter = new StringWriter();
		FileCopyUtils.copy(this.delegate.getReader(), bodyWriter);
		return bodyWriter.toString();
	}

	/**
	 * Returns the request's URI including its query string. The query string is
	 * determined by calling {@link HttpServletRequest#getQueryString()}. If it's
	 * {@code null} and it is a {@code GET} request, the query string is then constructed
	 * from the request's {@link HttpServletRequest#getParameterMap()} parameter map.
	 * 
	 * @return the URI of the request, including its query string
	 */
	public String getRequestUriWithQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.delegate.getRequestURI());
		String queryString = getQueryString();
		if (StringUtils.hasText(queryString)) {
			sb.append('?').append(queryString);
		}
		return sb.toString();
	}

	/**
	 * Returns the request's parameter map formatted as a query string
	 * 
	 * @return The query string derived from the request's parameter map
	 * @see HttpServletRequest#getParameterMap()
	 */
	public String getParameterMapAsQueryString() {
		return toQueryString(this.delegate.getParameterMap());
	}

	/**
	 * Returns the context path of the servlet
	 *
	 * @return the context path
	 */
	public String getContextPath() {
		return this.delegate.getContextPath();
	}

	private String getQueryString() {
		if (this.delegate.getQueryString() != null) {
			return this.delegate.getQueryString();
		}
		if (isGetRequest()) {
			return getParameterMapAsQueryString();
		}
		return null;
	}

	private static String toQueryString(Map<String, String[]> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			for (String value : entry.getValue()) {
				if (sb.length() > 0) {
					sb.append("&");
				}
				sb.append(urlEncodeUTF8(entry.getKey())).append('=')
						.append(urlEncodeUTF8(value));
			}
		}
		return sb.toString();
	}

	private static String urlEncodeUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Unable to URL encode " + s + " using UTF-8",
					ex);
		}
	}

}
