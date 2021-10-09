"""podcast.rss

Update the XML file for RSS clients

Functions:
    update_rss(str, PodcastDTO, str, int, int) -> None
"""
from email.utils import formatdate
from os import listdir, remove
from os.path import basename, dirname, join
from time import gmtime, strftime
from uuid import uuid4
from xml.etree.ElementTree import ElementTree, SubElement, parse, register_namespace

from cartierville.model import PodcastDTO, RSSException

ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd"
MAX_KEEP_ITEMS = 5
ICONS = ["icon.jpg", "icon.png"]


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
        _clean_up(channel, rss_location)
        xml.write(rss_location)
    except Exception as exception:
        raise RSSException(exception) from exception
