"""podcast.rss

Update the XML file for RSS clients

Functions:
    update_rss(Show, str, int) -> None
"""

from email.utils import formatdate
from os import R_OK, W_OK, access, chmod, listdir, remove
from os.path import basename, dirname, exists, join
from time import gmtime, strftime
from uuid import uuid4
from xml.etree.ElementTree import (
    ElementTree,
    ParseError,
    SubElement,
    parse,
    register_namespace,
)

from cartierville.model import SAMPLE_RSS, RSSException, Show

ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd"
MAX_KEEP_ITEMS = 5
ICONS = ["icon.jpg", "icon.png"]


def _get_xml(filename: str) -> ElementTree:
    if not exists(filename):
        with open(filename, "w", encoding="utf-8") as empty_file:
            empty_file.write(SAMPLE_RSS)

    if access(filename, R_OK | W_OK):
        try:
            return parse(filename)
        except ParseError as parse_error:
            raise RSSException(f"XML at {filename} is invalid") from parse_error
    else:
        raise RSSException(f"File {filename} is not read/writable")


def _serialise_duration(duration: int) -> str:
    return strftime("%H:%M:%S", gmtime(duration))


def _add_element(parent: ElementTree, name: str, text: str) -> None:
    SubElement(parent, name).text = text


def _add_enclosure(item: ElementTree, url: str, size: int) -> None:
    attributes = {"url": url, "type": "audio/mpeg", "length": str(size)}
    SubElement(item, "enclosure", attributes)


def _add_item(channel: ElementTree, show: Show, filename: str, file_size: int) -> None:
    link = show.output.uri + "/" + filename

    item = SubElement(channel, "item")
    _add_element(item, "title", show.title)
    _add_element(item, "itunes:summary", show.summary)
    _add_element(item, "description", show.summary)
    _add_element(item, "link", link)
    _add_element(item, "pubDate", formatdate())
    _add_element(item, "itunes:author", show.author)
    _add_element(item, "itunes:duration", _serialise_duration(show.duration))
    _add_element(item, "itunes:explicit", "no")
    _add_element(item, "guid", str(uuid4()))
    _add_enclosure(item, link, file_size)


def _remove_old_items(channel: ElementTree) -> None:
    items = channel.findall("item")
    num_to_delete = max(0, len(items) - MAX_KEEP_ITEMS)
    elements_to_delete = items[:num_to_delete]

    for element in elements_to_delete:
        channel.remove(element)


def _delete_files_not_in_list(channel: ElementTree, rss_location: str) -> None:
    rss_dir = dirname(rss_location)
    rss_file = basename(rss_location)
    dir_files = set(listdir(rss_dir))

    items = channel.findall("item")
    links = [item.find("link").text for item in items]

    expected_filenames = set(
        [link.split("/")[-1] for link in links] + [rss_file] + ICONS
    )

    files_to_delete = list(dir_files.difference(expected_filenames))
    paths_to_delete = [join(rss_dir, filename) for filename in files_to_delete]

    for path in paths_to_delete:
        remove(path)


def _clean_up(channel: ElementTree, rss_location: str) -> None:
    _remove_old_items(channel)
    _delete_files_not_in_list(channel, rss_location)


def update_rss(show: Show, filename: str, file_size: int) -> None:
    """Add an episode to the RSS XML"""
    rss_location = join(show.output.directory, "feed.xml")

    register_namespace("itunes", ITUNES_NAMESPACE)
    xml = _get_xml(rss_location)
    root = xml.getroot()
    channel = root.find("channel")
    _add_item(channel, show, filename, file_size)

    _clean_up(channel, rss_location)
    xml.write(rss_location)
    chmod(rss_location, 0x744)
