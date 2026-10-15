/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.subsections.body;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;

class CustomSubsectionId {

	void customSubsectionId() {
		// tag::custom-subsection-id[]
		responseBody(beneathPath("weather.temperature").withSubsectionId("temp"));
		// end::custom-subsection-id[]
	}

}
