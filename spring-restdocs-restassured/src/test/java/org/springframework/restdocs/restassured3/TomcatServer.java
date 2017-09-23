/*
 * Copyright 2014-2017 the original author or authors.
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

package org.springframework.restdocs.restassured3;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.rules.ExternalResource;

/**
 * {@link ExternalResource} that starts and stops a Tomcat server.
 *
 * @author Andy Wilkinson
 */
class TomcatServer extends ExternalResource {

	private Tomcat tomcat;

	private int port;

	@Override
	protected void before() throws LifecycleException {
		this.tomcat = new Tomcat();
		this.tomcat.getConnector().setPort(0);
		Context context = this.tomcat.addContext("/", null);
		this.tomcat.addServlet("/", "test", new TestServlet());
		context.addServletMappingDecoded("/", "test");
		this.tomcat.addServlet("/", "set-cookie", new CookiesServlet());
		context.addServletMappingDecoded("/set-cookie", "set-cookie");
		this.tomcat.start();
		this.port = this.tomcat.getConnector().getLocalPort();
	}

	@Override
	protected void after() {
		try {
			this.tomcat.stop();
		}
		catch (LifecycleException ex) {
			throw new RuntimeException(ex);
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

		private void respondWithJson(HttpServletResponse response)
				throws IOException, JsonProcessingException {
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
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			Cookie cookie = new Cookie("name", "value");
			cookie.setDomain("localhost");
			cookie.setHttpOnly(true);

			resp.addCookie(cookie);
		}

	}

}
