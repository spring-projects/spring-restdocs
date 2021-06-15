/*
 * Copyright 2014-2021 the original author or authors.
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

package com.example.notes;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.example.notes.TagRepresentationModelAssembler.TagModel;

@Component
class TagRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Tag, TagModel> {

	TagRepresentationModelAssembler() {
		super(TagsController.class, TagModel.class);
	}

	@Override
	public TagModel toModel(Tag entity) {
		TagModel model = new TagModel(entity);
		model.add(linkTo(methodOn(TagsController.class).tag(entity.getId())).withSelfRel(),
			linkTo(methodOn(TagsController.class).tagNotes(entity.getId())).withRel("tagged-notes"));
		return model;
	}
	
	@Override
	protected TagModel instantiateModel(Tag entity) {
		return new TagModel(entity);
	}

	@Relation(collectionRelation = "tags", itemRelation = "tag")
	static class TagModel extends RepresentationModel<TagModel> {
		
		private final Tag tag;
		
		TagModel(Tag tag) {
			this.tag = tag;
		}
		
		public String getName() {
			return this.tag.getName();
		}
		
	}

}
