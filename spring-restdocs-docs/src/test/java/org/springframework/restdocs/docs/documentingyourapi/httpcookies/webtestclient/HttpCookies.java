/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.httpcookies.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class HttpCookies {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
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
