/*
 * Copyright 2014-2022 the original author or authors.
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

package com.example.restassured;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@EnableWebFlux
@Configuration
class SampleRestAssuredApplication {

	@Bean
	RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions.route(RequestPredicates.GET("/"),
				(request) -> ServerResponse.status(HttpStatus.OK).bodyValue("Hello, World"));
	}
	
	@Bean
	WebServer webServer(RouterFunction<ServerResponse> routerFunction) {
		return new WebServer(routerFunction);
	}
	
	static class WebServer implements DisposableBean {
		
		private final DisposableServer httpServer;
		
		WebServer(RouterFunction<?> routerFunction) {
			ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(routerFunction));
			HttpServer httpServer = HttpServer.create().handle(adapter);
			this.httpServer = httpServer.bindNow();
		}

		@Override
		public void destroy() throws Exception {
			this.httpServer.disposeNow();
		}

		int getPort() {
			return ((InetSocketAddress)this.httpServer.address()).getPort();
		}

	}

}
