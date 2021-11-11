"""cartierville.model

Exceptions & classes for the application

classes:
    CartiervilleException
    StreamException
    RSSException

    PodcastDTO
"""

from typing import List, NamedTuple


class CartiervilleException(Exception):
    """Base exception for this project"""


class ConfigException(CartiervilleException):
    """Errors thrown during the config setup"""


class StreamException(CartiervilleException):
    """Exceptions raised during the streaming process"""


class RSSException(CartiervilleException):
    """Exceptions raised while updating the XML file"""


class Output(NamedTuple):
    """Where we can output the podcast"""

    directory: str
    uri: str


class Show(NamedTuple):
    """A show to save"""

    title: str
    summary: str
    author: str
    url: str
    duration: int
    days: List[str]
    start_time: str
    output: Output


LOG_FORMAT = "%(asctime)s %(process)5s %(levelname)-8s %(funcName)s: %(message)s"


_OUTPUTS_SCHEMA = {
    "type": "dict",
    "schema": {"directory": {"type": "string"}, "uri": {"type": "string"}},
}

_SHOWS_SCHEMA = {
    "type": "dict",
    "schema": {
        "title": {"type": "string"},
        "summary": {"type": "string"},
        "author": {"type": "string"},
        "url": {"type": "string"},
        "duration": {"type": "number", "min": 0, "max": 10800},
        "days": {
            "type": "list",
            "allowed": [
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "saturday",
                "sunday",
            ],
        },
        "time": {"type": "string", "regex": "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"},
        "output": {"type": "string"},
    },
}

CONFIG_SCHEMA = {
    "outputs": {"type": "dict", "valuesrules": _OUTPUTS_SCHEMA},
    "shows": {"type": "list", "schema": _SHOWS_SCHEMA},
}


SAMPLE_RSS = """
<rss xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" version="2.0">
    <channel>
        <title>Cartierville</title>
        <link>http://www.yourwebsite.com</link>
        <language>fr-ca</language>
        <itunes:subtitle>Radio-to-RSS</itunes:subtitle>
        <itunes:author>Your name</itunes:author>
        <itunes:summary>Summary</itunes:summary>
        <description>Description</description>
        <itunes:owner>
            <itunes:name>Your name</itunes:name>
            <itunes:email>Your email address</itunes:email>
        </itunes:owner>
        <itunes:explicit>no</itunes:explicit>
        <itunes:image href="http://www.example.com/podcast-icon.jpg" />
        <itunes:category text="Category" />
    </channel>
</rss>
"""
