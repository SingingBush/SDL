[SDL](https://sdlang.org/) (Simple Declarative Language) for Java
============

[![Build Status](https://travis-ci.org/SingingBush/SDL.svg?branch=master)](https://travis-ci.org/SingingBush/SDL)
[![Coverage Status](https://coveralls.io/repos/github/SingingBush/SDL/badge.svg?branch=master)](https://coveralls.io/github/SingingBush/SDL?branch=master)

> SDLang is a simple and concise way to textually represent data. It has an XML-like structure – tags, values and attributes – which makes it a versatile choice for data serialization, configuration files, or declarative languages. Its syntax was inspired by the C family of languages (C/C++, C#, D, Java, …).
> 
> <cite>sdlang.org</cite>

### Usage

To parse an SDL file simply create an InputStreamReader and pass it into the constructor of Parser:

```Java
final InputStream inputStream = new FileInputStream("c:\\data\\myfile.sdl");
final Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
final List<Tag> tags = new Parser(inputStreamReader).parse();
```

### Forked from [ikayzo/SDL](https://github.com/ikayzo/SDL):

This code was originally dumped in github in May 2011 with a single commit message stating that it was migrated from svn.

Since then there's been no activity and it seems that issues are ignored. Even the URL for documentation is broken.

The project seemed to be abandoned so I've forked it with the initial goal of

- publishing build artifacts to maven
- enable continuous integration
- keep project alive

Daniel Leuck, the original author, licensed the source as GPL v2.1