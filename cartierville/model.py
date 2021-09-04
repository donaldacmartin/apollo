"""cartierville.model

Exceptions & classes for the application

classes:
    CartiervilleException
    StreamException
    RSSException

    PodcastDTO
"""

from typing import NamedTuple


class CartiervilleException(Exception):
    """Base exception for this project"""


class StreamException(CartiervilleException):
    """Exceptions raised during the streaming process"""


class RSSException(CartiervilleException):
    """Exceptions raised while updating the XML file"""


class PodcastDTO(NamedTuple):
    """A podcast request"""

    title: str
    summary: str
    author: str
    url: str
