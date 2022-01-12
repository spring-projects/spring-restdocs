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

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
class ErrorController {

	@RequestMapping("/error")
	ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
		HttpStatus httpStatus = HttpStatus.valueOf((int)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
		Map<String, Object> body = Map.of("status", httpStatus.value(),
				"error", httpStatus.getReasonPhrase(),
				"path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI),
				"timestamp", Instant.now().toEpochMilli());
		ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, httpStatus);
		return response;
	}
	
}
