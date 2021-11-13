![Logo](/doc/logo-small.png)

![Python 3.10](https://img.shields.io/badge/python-3.10-informational)
[![Code style: black](https://img.shields.io/badge/code%20style-black-000000.svg)](https://github.com/psf/black)

> Radio to RSS service

## Prerequisites

- Python >= 3.10

## Build

```bash
poetry build
```

## Installation

```bash
pip3 install dist/*.tar.gz
```

## Docker

The docker image is `donaldacmartin/apollo`

In order to use Docker, the following volumes must be provided:

- `./config.yml`
- `./output` (substitute for each output directory in the config)

## Configuration

The config YAML file must provide the following:

- a dict of inputs
- each input contains a folder where we can put the file & the URI to this location
- a dict of shows
