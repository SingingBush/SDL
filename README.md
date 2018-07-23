[SDLang](https://sdlang.org/) (Simple Declarative Language) for Java
============

[![Build Status](https://travis-ci.org/SingingBush/SDL.svg?branch=master)](https://travis-ci.org/SingingBush/SDL)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.singingbush/sdlang/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.singingbush/sdlang)
[![Javadocs](https://www.javadoc.io/badge/com.singingbush/sdlang.svg)](https://www.javadoc.io/doc/com.singingbush/sdlang)
[![Coverage Status](https://coveralls.io/repos/github/SingingBush/SDL/badge.svg?branch=master)](https://coveralls.io/github/SingingBush/SDL?branch=master)

> SDLang is a simple and concise way to textually represent data. It has an XML-like structure – tags, values and attributes – which makes it a versatile choice for data serialization, configuration files, or declarative languages. Its syntax was inspired by the C family of languages (C/C++, C#, D, Java, …).
> 
> <cite>sdlang.org</cite>

### Adding the dependency to your project

Releases are available from Maven Central

```xml
    <dependency>
        <groupId>com.singingbush</groupId>
        <artifactId>sdlang</artifactId>
        <version>2.1.0</version>
    </dependency>
```

### Usage

To parse an SDL file simply create an InputStreamReader and pass it into the constructor of Parser:

```java
final InputStream inputStream = new FileInputStream("c:\\data\\myfile.sdl");
final Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
final List<Tag> tags = new Parser(inputStreamReader).parse();
```

To write SDL from Java objects:

```java
final Tag tag = SDL.tag("thing")
    .withNamespace("my")
    .withComment("This text will precede the 'my:thing' when serialised")
    .withValue(SDL.value('g'))
    .withChild(SDL.tag("child")
        .withComment("child tags can also have comments")
        .withValues(SDL.value(ZonedDateTime.now()), SDL.value("some text"))
        .build()
    )
    .withAttribute("flt", SDL.value(0.0f))
    .withAttribute("lng", SDL.value(1_000L))
    .build();
```

### Forked from [ikayzo/SDL](https://github.com/ikayzo/SDL):

This code was originally dumped in github in May 2011 with a single commit message stating that it was migrated from svn.

Since then there's been no activity in that repository and it seems that issues are ignored. Even the URL for documentation is broken.

As the project appears to be abandoned I've forked it with the goal of

- [x] Adding more unit tests
- [x] Enabling continuous integration using [travis-ci.org](travis-ci.org)
- [x] Reporting on Test Coverage using [coveralls.io](coveralls.io)
- [x] Fixing existing bugs
- [x] Overhaul the project and start rewriting the codebase
- [x] Publish build artifacts to maven central

Daniel Leuck, the original author, licensed the source as [LGPL v2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt)
