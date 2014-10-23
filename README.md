## Goal

The primary goal of the project is to make it easier to produce accurate documentation
of a RESTful service using [AsciiDoctor][1] and Spring's [MVC Test Framework][2]. The
documentation is intended to be an easily read user guide, akin to [GitHub's API
documentation][3] for example, rather than dense API documentation produced by tools
like [Swagger][4].

## Quickstart

The project requires Java 7 or later. It's built with Gradle:

```
$ ./gradlew build install
```

Once the main project's built, take a look at one of the two sample projects. Both
projects implement a RESTful service for creating tagged notes but have different
implementations: `rest-notes-spring-hateoas` is implemented using Spring MVC and Spring
Hateoas while `rest-notes-spring-data-rest` is implemented using Spring Data REST.

To see the sample project's documentation move into its directory and use Gradle to
build the documentation. For example:

```
$ cd rest-notes-spring-data-rest
$ ./gradlew restDocumentation
```

Once the build is complete, open the generated documentation:

```
open build/asciidoc/main.html
```

Every example request and response in the documentation is auto-generated using custom
Spring MVC Test result handlers. This ensures that the examples match the service that
they are documenting.

## How does it work

There are three main pieces involved in using this project to document your RESTful
service.

### Gradle plugin

A Gradle plugin is provided. This plugin builds on top of the [AsciiDoctor plugin][5]
and is responsible for producing the documentation during the build. Assuming you've
built and installed the project as described in the quick start, the plugin as
configured in your project as follows:

```groovy
buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}
	dependencies {
		classpath 'org.springframework.restdocs:spring-restdocs-gradle-plugin:0.1.0.BUILD-SNAPSHOT'
	}
}

apply plugin: 'org.springframework.restdocs'
```

### Programatically generated snippets

Spring's MVC Test framework is used to make requests to the service that you are
documenting. Through the use of a custom JUnit runner and some MockMvc configuration
documentation snippets for those request and their responses is automatically generated.

The runner is configured using `@RunWith` on the documentation class. For example:

```java
@RunWith(RestDocumentationJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class GettingStartedDocumentation {
	// …
}
```

`RestDocumentationJUnit4ClassRunner` is an extension of Spring Framework's
`SpringJUnit4ClassRunner` so all the standard Spring Test Framework functionality is
available.

The MockMvc configuration is applied during its creation. This is typically done in
an `@Before` method, for example:

```java
@Before
public void setUp() {
	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(new RestDocumentationConfiguration()).build();
}
```

With this configuration in place any requests made to the REST service using MockMvc
will have their requests and responses documented. For example:

```java
public void getIndex() {
	this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON));
}
```

The code above will perform a `GET` request against the index (`/`) of the service with
an accept header indicating that a JSON response is required. It will automatically
write the cURL command for the request and the resulting response to files beneath the
project's `build/generated-documentation` directory. This location is automatically
configured by the Gradle plugin. The names of the files are determined by the name of
the class and method from which the call was made. In this example, the files will be
called:

 - `GettingStartedDocumentation/getIndexRequest.asciidoc`
 - `GettingStartedDocumentation/getIndexResponse.asciidoc`

### Documentation written in Asciidoc

Producing high-quality, easily readable documentation is difficult and the process is
only made harder by trying to write the documentation in an ill-suited format such as
Java annotations. This project addresses this by allowing you to write the bulk of
your documentation's text as an Asciidoc document. These files should be placed in
`src/documentation/asciidoc`.

To include the programmatically generated snippets in your documentation, you use
Asciidoc's [`include` macro][6]. The Gradle plugin provides an attribute, `generated`,
that you can use to reference the directory to which the snippets are written. For
example, to include both the request and response snippets described above:

```
include::{generated}/GettingStartedDocumentation/getIndexRequest.asciidoc[]
include::{generated}/GettingStartedDocumentation/getIndexResponse.asciidoc[]
```

## Learning more

To learn more, take a look at the accompanying sample projects:

 - [rest-notes-spring-data-rest][7]
 - [rest-notes-spring-hateoas][8]


[1]: http://asciidoctor.org
[2]: http://docs.spring.io/spring-framework/docs/4.1.1.RELEASE/spring-framework-reference/html/testing.html#spring-mvc-test-framework
[3]: https://developer.github.com/v3/
[4]: http://swagger.io
[5]: http://plugins.gradle.org/plugin/org.asciidoctor.gradle.asciidoctor
[6]: http://www.methods.co.nz/asciidoc/userguide.html#_system_macros
[7]: rest-notes-spring-data-rest
[8]: rest-notes-spring-hateoas