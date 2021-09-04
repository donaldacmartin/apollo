"""podcast.rss

Update the XML file for RSS clients

Functions:
    update_rss(str, PodcastDTO, str, int, int) -> None
"""
from email.utils import formatdate
from time import gmtime, strftime
from uuid import uuid4
from xml.etree.ElementTree import ElementTree, SubElement, parse, register_namespace

from cartierville.model import PodcastDTO, RSSException

ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd"


def _serialise_duration(duration: int) -> str:
    return strftime("%H:%M:%S", gmtime(duration))


def _add_element(parent: ElementTree, name: str, text: str) -> None:
    SubElement(parent, name).text = text


def _add_enclosure(item: ElementTree, url: str, size: int) -> None:
    attributes = {"url": url, "type": "audio/mpeg", "length": str(size)}
    SubElement(item, "enclosure", attributes)


def _add_item(
    channel: ElementTree, podcast: PodcastDTO, link: str, duration: int, file_size: int
) -> None:
    item = SubElement(channel, "item")
    _add_element(item, "title", podcast.title)
    _add_element(item, "itunes:summary", podcast.summary)
    _add_element(item, "description", podcast.summary)
    _add_element(item, "link", link)
    _add_element(item, "pubDate", formatdate())
    _add_element(item, "itunes:author", podcast.author)
    _add_element(item, "itunes:duration", _serialise_duration(duration))
    _add_element(item, "itunes:explicit", "no")
    _add_element(item, "guid", str(uuid4()))
    _add_enclosure(item, link, file_size)


def update_rss(
    rss_location: str, podcast: PodcastDTO, link: str, duration: int, file_size: int
) -> None:
    """Add an episode to the RSS XML"""
    try:
        register_namespace("itunes", ITUNES_NAMESPACE)
        xml = parse(rss_location)
        root = xml.getroot()
        channel = root.find("channel")
        _add_item(channel, podcast, link, duration, file_size)
        xml.write(rss_location)
    except Exception as exception:
        raise RSSException(exception) from exception
