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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestNotesControllerAdvice {

	@ExceptionHandler(IllegalArgumentException.class)
	public void handleIllegalArgumentException(IllegalArgumentException ex,
			HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	@ExceptionHandler(ResourceDoesNotExistException.class)
	public void handleResourceDoesNotExistException(ResourceDoesNotExistException ex,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(),
				"The resource '" + request.getRequestURI() + "' does not exist");
	}

}
