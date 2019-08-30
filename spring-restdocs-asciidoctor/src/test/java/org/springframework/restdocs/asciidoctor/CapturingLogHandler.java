/*
 * Copyright 2019 the original author or authors.
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

package org.springframework.restdocs.asciidoctor;

import java.util.ArrayList;
import java.util.List;

import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;

public class CapturingLogHandler implements LogHandler {

	private static final List<LogRecord> logRecords = new ArrayList<LogRecord>();

	@Override
	public void log(LogRecord logRecord) {
		logRecords.add(logRecord);
	}

	static List<LogRecord> getLogRecords() {
		return logRecords;
	}

	static void clear() {
		logRecords.clear();
	}

}
