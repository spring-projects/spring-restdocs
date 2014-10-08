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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import com.example.notes.NoteResourceAssembler.NoteResource;

@RestController
@RequestMapping("/notes")
public class NotesController {

	private final NoteRepository noteRepository;

	private final TagRepository tagRepository;

	private final NoteResourceAssembler noteResourceAssembler;

	@Autowired
	public NotesController(NoteRepository noteRepository, TagRepository tagRepository,
			NoteResourceAssembler noteResourceAssembler) {
		this.noteRepository = noteRepository;
		this.tagRepository = tagRepository;
		this.noteResourceAssembler = noteResourceAssembler;
	}

	@RequestMapping
	Iterable<NoteResource> all() {
		return this.noteResourceAssembler.toResources(this.noteRepository.findAll());
	}

	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	HttpHeaders create(@RequestBody NoteInput noteInput) {
		Note note = new Note();
		note.setTitle(noteInput.getTitle());
		note.setBody(noteInput.getBody());
		note.setTags(getTags(noteInput.getTagUris()));

		this.noteRepository.save(note);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders
				.setLocation(linkTo(NotesController.class).slash(note.getId()).toUri());

		return httpHeaders;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Resource<Note> note(@PathVariable("id") long id) {
		Note note = this.noteRepository.findOne(id);
		return this.noteResourceAssembler.toResource(note);
	}

	@RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
	ResourceSupport noteTags(@PathVariable("id") long id) {
		ResourceSupport resource = new ResourceSupport();
		Note note = this.noteRepository.findOne(id);
		for (Tag tag : note.getTags()) {
			resource.add(linkTo(TagsController.class).slash(tag.getId()).withRel("tag"));
		}
		return resource;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	void updateNote(@PathVariable("id") long id, @RequestBody NotePatchInput noteInput) {
		Note note = this.noteRepository.findOne(id);
		if (noteInput.getTagUris() != null) {
			note.setTags(getTags(noteInput.getTagUris()));
		}
		if (noteInput.getTitle() != null) {
			note.setTitle(noteInput.getTitle());
		}
		if (noteInput.getBody() != null) {
			note.setBody(noteInput.getBody());
		}
		this.noteRepository.save(note);
	}

	private List<Tag> getTags(List<URI> tagLocations) {
		UriTemplate template = new UriTemplate("/tags/{id}");
		return tagLocations
				.stream()
				.map(location -> this.tagRepository.findOne(Long.valueOf(template.match(
						location.toASCIIString()).get("id"))))
				.collect(Collectors.toList());
	}
}
