# Apollo

## Introduction

I needed a simple application to record audio streams from Internet radio
stations and save them as MP3 files. This is that project.

The application will create a temporary file and save the stream there. The
location is printed at the end of the recording.

We can currently stream up to 10 items at once. Information should be provided
in a CSV file described below.

## Requirements

- Java JDK version 13
- Maven version 3

## Compilation

`mvn clean install`

## Usage

To record a streams stored in the *shows.csv* file:

`java -jar output.jar /home/user/shows.csv`

## CSV File Format

| Show Name     | Start Time | Time Zone       | Mon | Tue | Wed | Thu | Fri | Sat | Sun | Duration (mins) | URL                                   |
|-------------- | ---------- | --------------- | --- | --- | --- | --- | --- | --- | --- | --------------- | ------------------------------------- |
| MorningShow   | 06:00      | America/Toronto | Y   | Y   | Y   | Y   | Y   | N   | N   | 60              | https://example.com/morningstream.mp3 |
| EveningEurope | 18:00      | Europe/Paris    | Y   | N   | Y   | N   | Y   | N   | N   | 120             | https://example.com/eveningstream.mp3 |
