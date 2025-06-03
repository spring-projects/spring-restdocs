/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.restassured;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

/**
 * {@link Extension} that starts and stops a Tomcat server.
 *
 * @author Andy Wilkinson
 */
class TomcatServer implements BeforeAllCallback, AfterAllCallback {

	private int port;

	@Override
	public void beforeAll(ExtensionContext extensionContext) {
		Store store = extensionContext.getStore(Namespace.create(TomcatServer.class));
		store.getOrComputeIfAbsent(Tomcat.class, (key) -> {
			Tomcat tomcat = new Tomcat();
			tomcat.getConnector().setPort(0);
			Context context = tomcat.addContext("/", null);
			tomcat.addServlet("/", "test", new TestServlet());
			context.addServletMappingDecoded("/", "test");
			tomcat.addServlet("/", "set-cookie", new CookiesServlet());
			context.addServletMappingDecoded("/set-cookie", "set-cookie");
			tomcat.addServlet("/", "query-parameter", new QueryParameterServlet());
			context.addServletMappingDecoded("/query-parameter", "query-parameter");
			tomcat.addServlet("/", "form-url-encoded", new FormUrlEncodedServlet());
			context.addServletMappingDecoded("/form-url-encoded", "form-url-encoded");
			try {
				tomcat.start();
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			this.port = tomcat.getConnector().getLocalPort();
			return tomcat;
		});
	}

	@Override
	public void afterAll(ExtensionContext extensionContext) throws LifecycleException {
		Store store = extensionContext.getStore(Namespace.create(TomcatServer.class));
		Tomcat tomcat = store.get(Tomcat.class, Tomcat.class);
		if (tomcat != null) {
			tomcat.stop();
		}
	}

	int getPort() {
		return this.port;
	}

	/**
	 * {@link HttpServlet} used to handle requests in the tests.
	 */
	private static final class TestServlet extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			respondWithJson(response);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			respondWithJson(response);
		}

		private void respondWithJson(HttpServletResponse response) throws IOException, JsonProcessingException {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			Map<String, Object> content = new HashMap<>();
			content.put("a", "alpha");
			Map<String, String> link = new HashMap<>();
			link.put("rel", "rel");
			link.put("href", "href");
			content.put("links", Arrays.asList(link));
			response.getWriter().println(new ObjectMapper().writeValueAsString(content));
			response.setHeader("a", "alpha");
			response.setHeader("Foo", "http://localhost:12345/foo/bar");
			response.flushBuffer();
		}

	}

	/**
	 * {@link HttpServlet} used to handle cookies-related requests in the tests.
	 */
	private static final class CookiesServlet extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Cookie cookie = new Cookie("name", "value");
			cookie.setDomain("localhost");
			cookie.setHttpOnly(true);

			resp.addCookie(cookie);
		}

	}

	private static final class QueryParameterServlet extends HttpServlet {

		@Override
		protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			if (!req.getQueryString().equals("a=alpha&a=apple&b=bravo")) {
				throw new ServletException("Incorrect query string");
			}
			resp.setStatus(200);
		}

	}

	private static final class FormUrlEncodedServlet extends HttpServlet {

		@Override
		protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			if (!MediaType.APPLICATION_FORM_URLENCODED
				.isCompatibleWith(MediaType.parseMediaType(req.getContentType()))) {
				throw new ServletException("Incorrect Content-Type");
			}
			String content = FileCopyUtils.copyToString(new InputStreamReader(req.getInputStream()));
			if (!"a=alpha&a=apple&b=bravo".equals(content)) {
				throw new ServletException("Incorrect body content");
			}
			resp.setStatus(200);
		}

	}

}
