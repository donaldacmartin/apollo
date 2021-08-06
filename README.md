# Cartierville

[![Build Status](https://ci.donald-martin.ca/buildStatus/icon?job=cartierville%2Fmain)](https://ci.donald-martin.ca/job/cartierville/job/main/)

Utility to stream radio and save to a local file

## Requirements

- Python 3.7 or higher

## Installation

Download the compiled .tar.gz file and run `pip3 install DOWNLOAD.tar.gz`.

## Usage

The following arguments must be provided:
- `url` The location of the stream to save
- `duration` How long to stream (in seconds)
- `output-dir` Where to put the file (a temporary file is used during streaming)

```
python3 -m \
    cartierville.cartierville \
    --url https://example.com/stream.mp3 \
    --duration 60 \
    --output-dir ./
```