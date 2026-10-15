/*
 * Copyright 2014-present the original author or authors.
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
