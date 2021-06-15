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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import com.example.notes.NoteRepresentationModelAssembler.NoteModel;
import com.example.notes.TagRepresentationModelAssembler.TagModel;

@RestController
@RequestMapping("/notes")
class NotesController {

	private static final UriTemplate TAG_URI_TEMPLATE = new UriTemplate("/tags/{id}");

	private final NoteRepository noteRepository;

	private final TagRepository tagRepository;
	
	private final NoteRepresentationModelAssembler noteAssembler;
	
	private final TagRepresentationModelAssembler tagAssembler;

	NotesController(NoteRepository noteRepository, TagRepository tagRepository,
			NoteRepresentationModelAssembler noteAssembler, TagRepresentationModelAssembler tagAssembler) {
		this.noteRepository = noteRepository;
		this.tagRepository = tagRepository;
		this.noteAssembler = noteAssembler;
		this.tagAssembler = tagAssembler;
	}

	@RequestMapping(method = RequestMethod.GET)
	CollectionModel<NoteModel> all() {
		return noteAssembler.toCollectionModel(this.noteRepository.findAll());
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

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void delete(@PathVariable("id") long id) {
		this.noteRepository.deleteById(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	NoteModel note(@PathVariable("id") long id) {
		return this.noteAssembler.toModel(findNoteById(id));
	}

	@RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
	CollectionModel<TagModel> noteTags(@PathVariable("id") long id) {
		return this.tagAssembler.toCollectionModel(findNoteById(id).getTags());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void updateNote(@PathVariable("id") long id, @RequestBody NotePatchInput noteInput) {
		Note note = findNoteById(id);
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

	private Note findNoteById(long id) {
		Note note = this.noteRepository.findById(id);
		if (note == null) {
			throw new ResourceDoesNotExistException();
		}
		return note;
	}

	private List<Tag> getTags(List<URI> tagLocations) {
		List<Tag> tags = new ArrayList<>(tagLocations.size());
		for (URI tagLocation: tagLocations) {
			Tag tag = this.tagRepository.findById(extractTagId(tagLocation));
			if (tag == null) {
				throw new IllegalArgumentException("The tag '" + tagLocation
										+ "' does not exist");
			}
			tags.add(tag);
		}
		return tags;
	}

	private long extractTagId(URI tagLocation) {
		try {
			String idString = TAG_URI_TEMPLATE.match(tagLocation.toASCIIString()).get(
					"id");
			return Long.valueOf(idString);
		}
		catch (RuntimeException ex) {
			throw new IllegalArgumentException("The tag '" + tagLocation + "' is invalid");
		}
	}
}
