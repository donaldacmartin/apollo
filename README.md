# Apollo

## Introduction

I needed a simple application to record audio streams from Internet radio
stations and save them as MP3 files. This is that project.

The application will create a temporary file and save the stream there. The
location is printed at the end of the recording.

## Requirements

- Java JDK version 13
- Maven version 3

## Compilation

`mvn clean install`

## Usage

To record a stream from _example.com_ for 10 minutes:

`java -jar output.jar https://example.com/stream.mp3 600`
