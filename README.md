# Spring REST docs [![Build status][10]][11]

The primary goal of this project is to make it easy to document RESTful services by
combining content that's been hand-written with auto-generated examples produced
with the [Spring MVC Test][2] framework. The result is intended to be an easy-to-read
user guide, akin to [GitHub's API documentation][3] for example, rather than the fully
automated, dense API documentation produced by tools like [Swagger][4].

For a broader introduction see the Documenting RESTful APIs presentation. Both the
[slides][9] and a [video recording][13] are available.

## Quickstart

The project requires Spring Framework 4.1 and Java 7 or later. Snapshots are published to
`https://repo.spring.io/snapshot`. Alternatively, it can be built locally using Gradle:

```
$ ./gradlew build install
```

The quickest way to get started is to look at one of the two sample projects. Both
projects implement a RESTful service for creating tagged notes and illustrate the use
of Maven or Gradle. The two projects have different implementations:
`rest-notes-spring-hateoas` is implemented using Spring MVC and Spring Hateoas where as
`rest-notes-spring-data-rest` is implemented using Spring Data REST.

Every example request and response in the documentation is auto-generated using custom
Spring MVC Test result handlers. This ensures that the examples match the service that
they are documenting.

### Building a sample with Gradle

To see the sample project's documentation, move into its directory and use Gradle to
build it. For example:

```
$ cd samples/rest-notes-spring-data-rest
$ ./gradlew asciidoctor
```

Once the build is complete, open one of the following:

- build/asciidoc/html5/getting-started-guide.html
- build/asciidoc/html5/api-guide.html

### Building a sample with Maven

To see the sample project's documentation, move into its directory and use Maven to build
it. For example:

```
$ cd samples/rest-notes-spring-hateoas
$ mvn package
```

Once the build is complete, open one of the following:

- target/generated-docs/getting-started-guide.html
- target/generated-docs/api-guide.html

## How it works

There are three main pieces involved in using this project to document your RESTful
service.

### Build configuration

Both Maven and Gradle are supported.

#### Gradle configuration

You can look at either samples' `build.gradle` file to see the required configuration.
The key parts are described below.

Configure the Asciidoctor plugin:

```groovy
plugins {
    id "org.asciidoctor.convert" version "1.5.2"
}
```

Add a dependency on `spring-restdocs` in the `testCompile` configuration:

```groovy
dependencies {
	testCompile 'org.springframework.restdocs:spring-restdocs:0.1.0.BUILD-SNAPSHOT'
}
```

Configure a property to control the location of the generated snippets:

```groovy
ext {
	generatedDocumentation = file('build/generated-snippets')
}
```

Configure the `test` task with a system property to control the location to which the
snippets are generated:

```groovy
test {
	systemProperty 'org.springframework.restdocs.outputDir', generatedDocumentation
	outputs.dir generatedDocumentation
}
```

Configure the `asciidoctor` task. The `generated` attribute is used to provide easy
access to the generated snippets:

```groovy
asciidoctor {
	attributes 'generated': generatedDocumentation
	inputs.dir generatedDocumentation
	dependsOn test
}

```

You may want to include the generated documentation in your project's jar file, for
example to have it [served as static content by Spring Boot][12]. You can do so by
configuring the `jar` task to depend on the `asciidoctor` task and to copy the
generated documentation into the jar's `static` directory:

```groovy
jar {
	dependsOn asciidoctor
	from ("${asciidoctor.outputDir}/html5") {
		into 'static/docs'
	}
}

```

#### Maven configuration

You can look at either samples' `pom.xml` file to see the required configuration. The key
parts are described below:

Add a dependency on `spring-restdocs` in the `test` scope:

```xml
<dependency>
	<groupId>org.springframework.restdocs</groupId>
	<artifactId>spring-restdocs</artifactId>
	<version>0.1.0.BUILD-SNAPSHOT</version>
	<scope>test</scope>
</dependency>
```

Configure the SureFire plugin with a system property to control the location to which
the snippets are generated:

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<includes>
			<include>**/*Documentation.java</include>
		</includes>
		<systemPropertyVariables>
			<org.springframework.restdocs.outputDir>${project.build.directory}/generated-snippets</org.springframework.restdocs.outputDir>
		</systemPropertyVariables>
	</configuration>
</plugin>
```

Configure the Asciidoctor plugin. The `generated` attribute is used to provide easy
access to the generated snippets:

```xml
<plugin>
	<groupId>org.asciidoctor</groupId>
	<artifactId>asciidoctor-maven-plugin</artifactId>
	<version>1.5.2</version>
	<executions>
		<execution>
			<id>generate-docs</id>
			<phase>package</phase>
			<goals>
				<goal>process-asciidoc</goal>
			</goals>
			<configuration>
				<backend>html</backend>
				<doctype>book</doctype>
				<attributes>
					<generated>${project.build.directory}/generated-snippets</generated>
				</attributes>
			</configuration>
		</execution>
	</executions>
</plugin>
```

You may want to include the generated documentation in your project's jar file, for
example to have it [served as static content by Spring Boot][12]. You can do so by
changing the configuration of the Asciidoctor plugin so that it runs in the
`prepare-package` phase and then configuring Maven's resources plugin to copy the
generated documentation into a location where it'll be included in the project's jar:

```xml
<plugin>
	<groupId>org.asciidoctor</groupId>
	<artifactId>asciidoctor-maven-plugin</artifactId>
	<version>1.5.2</version>
	<executions>
		<execution>
			<id>generate-docs</id>
			<phase>prepare-package</phase>
			<!-- … -->
		</execution>
	</executions>
</plugin>
<plugin>
	<artifactId>maven-resources-plugin</artifactId>
	<version>2.7</version>
	<executions>
		<execution>
			<id>copy-resources</id>
			<phase>prepare-package</phase>
			<goals>
				<goal>copy-resources</goal>
			</goals>
			<configuration>
				<outputDirectory>${project.build.outputDirectory}/static/docs</outputDirectory>
				<resources>
					<resource>
						<directory>${project.build.directory}/generated-docs</directory>
					</resource>
				</resources>
			</configuration>
		</execution>
	</executions>
</plugin>
```

### Programatically generated snippets

Spring's MVC Test framework is used to make requests to the service that you are
documenting. A custom `ResultHandler` is used to produce individual documentation
snippets for its request and its response as well as a snippet that contains both its
request and its response.

You can configure the scheme, host, and port of any URIs that appear in the
documentation snippets:

```java
@Before
public void setUp() {
	this.mockMvc = MockMvcBuilders
			.webAppContextSetup(this.context)
			.apply(new RestDocumentationConfigurer()
					.withScheme("https")
					.withHost("localhost")
					.withPort(8443))
			.build();
}
```

The default values are `http`, `localhost`, and `8080`. You can omit the above
configuration if these defaults meet your needs.

To document a MockMvc call, you use MockMvc's `andDo` method, passing it a
`RestDocumentationResultHandler` that can be easily obtained from
the static `RestDocumentation.document` method:

```java
public void getIndex() {
	this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andDo(document("index"));
}
```

The code above will perform a `GET` request against the index (`/`) of the service with
an accept header indicating that a JSON response is required. It will write the cURL
command for the request and the resulting response to files in a directory named
`index` in the project's `build/generated-snippets/` directory. Three files will
be written:

 - `index/curl-request.adoc`
 - `index/http-request.adoc`
 - `index/http-response.adoc`

#### Parameterized output directories

The `document` method supports parameterized output directories. The following parameters
are supported:

| Parameter     | Description
| ------------- | -----------
| {methodName}  | The name of the test method, formatted using camelcase
| {method-name} | The name of the test method, formatted with dash separators
| {method_name} | The name of the test method, formatted with underscore separators
| {step}        | The count of calls to `MockMvc.perform` in the current test

For example, `document("{method-name}")` in a test method named `creatingANote` will
write snippets into a directory named `creating-a-note`.

The `{step}` parameter is particularly useful in combination with Spring MVC Test's
`alwaysDo` functionality. It allows documentation to be configured once in a setup method:

```java
@Before
public void setUp() {
	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(new RestDocumentationConfigurer())
			.alwaysDo(document("{method-name}/{step}/"))
			.build();
}
```

With this configuration in place, every call to `MockMvc.perform` will produce
documentation snippets without any further configuration. Take a look at the
`GettingStartedDocumentation` classes in each of the sample applications to see this
functionality in action.

#### Pretty-printed snippets

To improve the readability of the generated snippets you may want to configure your
application to produce JSON that has been pretty-printed. If you are a Spring Boot
user you can do so by setting the `spring.jackson.serialization.indent_output` property
to `true`.

### Hand-written documentation

Producing high-quality, easily readable documentation is difficult and the process is
only made harder by trying to write the documentation in an ill-suited format such as
Java annotations. This project addresses this by allowing you to write the bulk of
your documentation's text using [Asciidoctor][1]. The default location for source files
depends on whether you're using Maven or Gradle. By default, Asciidoctor's Maven plugin
looks in `src/main/asciidoc`, whereas the Asciidoctor Gradle plugin looks in
`src/docs/asciidoc`

To include the programmatically generated snippets in your documentation, you use
Asciidoc's [`include` macro][6]. The Maven and Gradle configuration described above
configures an attribute, `generated`, that you can use to reference the directory to
which the snippets are written. For example, to include both the request and response
snippets described above:

```
include::{generated}/index/curl-request.adoc[]
include::{generated}/index/http-response.adoc[]
```

## Generating snippets in your IDE

As described above, a system property is used to configure the location to which the
generated snippets are written. When running documentation tests in your IDE this system
property will not have been set. If the property is not set the snippets will be written
to standard out.

If you'd prefer that your IDE writes the snippets to disk you can use a file
in `src/test/resources` named `documentation.properties` to configure the property.
For example:

```properties
org.springframework.restdocs.outputDir: target/generated-snippets

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
[7]: samples/rest-notes-spring-data-rest
[8]: samples/rest-notes-spring-hateoas
[9]: https://speakerdeck.com/ankinson/documenting-restful-apis-webinar
[10]: https://build.spring.io/plugins/servlet/buildStatusImage/SRD-PUB (Build status)
[11]: https://build.spring.io/browse/SRD-PUB
[12]: http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-static-content
[13]: https://www.youtube.com/watch?v=knH5ihPNiUs&feature=youtu.be
