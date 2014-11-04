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

Once the build is complete, open on of the generated pieces HTML documentation:

```
open build/asciidoc/getting-started-guide.html
open build/asciidoc/api-guide.html
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
documenting. Any such request wrapped in a call to `RestDocumentation.document` will
produce individual documentation snippets for its request and its response as well as
a snippet that contains both its request and its response.

You can configure the scheme, host, and port of any URIs that appear in the
documentation snippets:

```java
@Before
public void setUp() {
	this.mockMvc = MockMvcBuilders
			.webAppContextSetup(this.context)
			.apply(new RestDocumentationConfiguration()
					.withScheme("https")
					.withHost("localhost")
					.withPort(8443))
			.build();
}
```

The default values are `http`, `localhost`, and `8080`. You can omit the above
configuration if these defaults meet your needs.

To document a MockMvc call, wrap it in a call to `RestDocumentation.document`:

```java
public void getIndex() {
	document("index", this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)));
}
```

The code above will perform a `GET` request against the index (`/`) of the service with
an accept header indicating that a JSON response is required. It will write the cURL
command for the request and the resulting response to files in a directory named
`index` in the project's `build/generated-documentation/` directory. Three files will
be written:

 - `index/request.asciidoc`
 - `index/response.asciidoc`
 - `index/request-response.asciidoc`

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
include::{generated}/index/request.asciidoc[]
include::{generated}/index/response.asciidoc[]
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