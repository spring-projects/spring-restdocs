/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.httpcookies.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class HttpCookies {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		this.restTestClient.get()
			.uri("/people")
			.cookie("JSESSIONID", "ACBCDFD0FF93D5BB=") // <1>
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("cookies", requestCookies(// <2>
					cookieWithName("JSESSIONID").description("Session token")), // <3>
					responseCookies(// <4>
							cookieWithName("JSESSIONID").description("Updated session token"),
							cookieWithName("logged_in").description("User is logged in"))));
	}

}
