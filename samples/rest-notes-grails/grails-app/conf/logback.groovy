/*
 * Copyright 2014-2016 the original author or authors.
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

import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%level %logger - %msg%n"
	}
}

root(ERROR, ['STDOUT'])

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
	appender("FULL_STACKTRACE", FileAppender) {
		file = "${targetDir}/stacktrace.log"
		append = true
		encoder(PatternLayoutEncoder) {
			pattern = "%level %logger - %msg%n"
		}
	}
	logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}
