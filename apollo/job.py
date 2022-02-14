"""apollo.job

Schedules jobs by creating a process for each one.

Functions:
    schedule_show(Show) -> None
"""

from logging import INFO, basicConfig, info, error
from multiprocessing import Process
from os import chmod
from os.path import basename
from shutil import move
from typing import Tuple

from schedule import every

from apollo.model import (
    LOG_FORMAT,
    ApolloException,
    MP3Source,
    Show,
    SoundCloudSource,
    StreamException,
)
from apollo.rss import update_rss
from apollo.stream import download_to_tmp_file, stream_to_tmp_file


def _process_file(show: Show) -> Tuple[str, int]:
    if isinstance(show.source, MP3Source):
        info(f"Starting to stream {show.title} from {show.source.url}")
        return stream_to_tmp_file(show.source)

    if isinstance(show.source, SoundCloudSource):
        info(f"Downloading {show.title} from {show.source.url}")
        return download_to_tmp_file(show.source)

    error(f"Unexpected source for {show.title}")
    raise StreamException("Unexpected source")


def _job(show: Show) -> None:
    try:
        basicConfig(format=LOG_FORMAT, level=INFO)

        info(f"Starting to stream {show.title}")
        tmp_file, file_size = _process_file(show)

        info(f"Moving {tmp_file} to {show.output.directory}")
        chmod(move(tmp_file, show.output.directory), 0x744)

        info("Updating RSS file")
        update_rss(show, basename(tmp_file), file_size)
    except ApolloException as apollo_exception:
        error(f"Error encountered: {apollo_exception}")


def _run_proc(show: Show) -> None:
    Process(target=_job, args=(show,)).start()


def schedule_show(show: Show) -> None:
    """Schedules a show based on the day of week and start time"""

    if "monday" in show.days:
        every().monday.at(show.start_time).do(_run_proc, show=show)

    if "tuesday" in show.days:
        every().tuesday.at(show.start_time).do(_run_proc, show=show)

    if "wednesday" in show.days:
        every().wednesday.at(show.start_time).do(_run_proc, show=show)

    if "thursday" in show.days:
        every().thursday.at(show.start_time).do(_run_proc, show=show)

    if "friday" in show.days:
        every().friday.at(show.start_time).do(_run_proc, show=show)

    if "saturday" in show.days:
        every().saturday.at(show.start_time).do(_run_proc, show=show)

    if "sunday" in show.days:
        every().sunday.at(show.start_time).do(_run_proc, show=show)
