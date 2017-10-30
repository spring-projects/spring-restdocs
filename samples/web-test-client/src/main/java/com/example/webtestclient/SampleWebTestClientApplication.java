/*
 * Copyright 2014-2017 the original author or authors.
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

package com.example.webtestclient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.ipc.netty.http.server.HttpServer;

@EnableWebFlux
@Configuration
public class SampleWebTestClientApplication {

	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions.route(RequestPredicates.GET("/"), (request) -> ServerResponse.status(HttpStatus.OK).syncBody("Hello, World"));
	}

	public static void main(String[] args) {
		RouterFunction<?> routerFunction = new AnnotationConfigApplicationContext(SampleWebTestClientApplication.class).getBean(RouterFunction.class);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(routerFunction));
		HttpServer httpServer = HttpServer.create(8080);
		httpServer.startAndAwait(adapter);
	}

}
