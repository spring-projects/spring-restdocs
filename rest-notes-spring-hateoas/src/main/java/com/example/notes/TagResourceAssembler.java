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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.example.notes.TagResourceAssembler.TagResource;

@Component
public class TagResourceAssembler extends ResourceAssemblerSupport<Tag, TagResource> {

	public TagResourceAssembler() {
		super(TagsController.class, TagResource.class);
	}

	@Override
	public TagResource toResource(Tag tag) {
		TagResource resource = createResourceWithId(tag.getId(), tag);
		resource.add(linkTo(TagsController.class).slash(tag.getId()).slash("notes")
				.withRel("tagged-notes"));
		return resource;
	}

	@Override
	protected TagResource instantiateResource(Tag entity) {
		return new TagResource(entity);
	}

	static class TagResource extends Resource<Tag> {

		public TagResource(Tag content) {
			super(content);
		}
	}

}
