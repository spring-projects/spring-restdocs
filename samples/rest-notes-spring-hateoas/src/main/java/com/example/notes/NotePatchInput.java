/*
 * Copyright 2014 the original author or authors.
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

package com.example.notes;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotePatchInput {
	
	@NullOrNotBlank 
	private final String title;
	
	private final String body;
	
	private final List<URI> tagUris;

	@JsonCreator
	public NotePatchInput(@JsonProperty("title") String title,
			@JsonProperty("body") String body, @JsonProperty("tags") List<URI> tagUris) {
		this.title = title;
		this.body = body;
		this.tagUris = tagUris == null ? Collections.<URI>emptyList() : tagUris;
	}
	
	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	@JsonProperty("tags")
	public List<URI> getTagUris() {
		return this.tagUris;
	}
}
