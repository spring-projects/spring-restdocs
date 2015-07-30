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

package org.springframework.restdocs.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents an HTTP response.
 *
 * @author Andy Wilkinson
 */
class HttpResponseSnippet extends TemplatedSnippet {

	HttpResponseSnippet() {
		this(null);
	}

	HttpResponseSnippet(Map<String, Object> attributes) {
		super("http-response", attributes);
	}

	@Override
	public Map<String, Object> document(MvcResult result) throws IOException {
		HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(
				"responseBody",
				StringUtils.hasLength(result.getResponse().getContentAsString()) ? String
						.format("%n%s", result.getResponse().getContentAsString()) : "");
		model.put("statusCode", status.value());
		model.put("statusReason", status.getReasonPhrase());
		model.put("headers", headers(result));
		return model;
	}

	private List<Map<String, String>> headers(MvcResult result) {
		List<Map<String, String>> headers = new ArrayList<>();
		for (String headerName : result.getResponse().getHeaderNames()) {
			for (String header : result.getResponse().getHeaders(headerName)) {
				headers.add(header(headerName, header));
			}
		}
		return headers;
	}

	private Map<String, String> header(String name, String value) {
		Map<String, String> header = new HashMap<>();
		header.put("name", name);
		header.put("value", value);
		return header;
	}
}