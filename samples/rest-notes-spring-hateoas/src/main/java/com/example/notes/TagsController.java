/*
 * Copyright 2014-2017 the original author or authors.
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

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.notes.NoteRepresentationModelAssembler.NoteModel;
import com.example.notes.TagRepresentationModelAssembler.TagModel;

@RestController
@RequestMapping("tags")
class TagsController {

	private final TagRepository repository;
	
	private final TagRepresentationModelAssembler tagAssembler;
	
	private final NoteRepresentationModelAssembler noteAssembler;

	TagsController(TagRepository repository, TagRepresentationModelAssembler tagAssembler,
			NoteRepresentationModelAssembler noteAssembler) {
		this.repository = repository;
		this.tagAssembler = tagAssembler;
		this.noteAssembler = noteAssembler;
	}

	@RequestMapping(method = RequestMethod.GET)
	CollectionModel<TagModel> all() {
		return this.tagAssembler.toCollectionModel(this.repository.findAll());
	}

	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	HttpHeaders create(@RequestBody TagInput tagInput) {
		Tag tag = new Tag();
		tag.setName(tagInput.getName());

		this.repository.save(tag);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(TagsController.class).slash(tag.getId()).toUri());

		return httpHeaders;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void delete(@PathVariable("id") long id) {
		this.repository.deleteById(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	TagModel tag(@PathVariable("id") long id) {
		return this.tagAssembler.toModel(findTagById(id));
	}

	@RequestMapping(value = "/{id}/notes", method = RequestMethod.GET)
	CollectionModel<NoteModel> tagNotes(@PathVariable("id") long id) {
		return this.noteAssembler.toCollectionModel(findTagById(id).getNotes());
	}

	private Tag findTagById(long id) {
		Tag tag = this.repository.findById(id);
		if (tag == null) {
			throw new ResourceDoesNotExistException();
		}
		return tag;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void updateTag(@PathVariable("id") long id, @RequestBody TagPatchInput tagInput) {
		Tag tag = findTagById(id);
		if (tagInput.getName() != null) {
			tag.setName(tagInput.getName());
		}
		this.repository.save(tag);
	}
}
