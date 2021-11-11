"""cartierville.job

Schedules jobs by creating a process for each one.

Functions:
    schedule_show(Show) -> None
"""

from logging import INFO, basicConfig, info, error
from multiprocessing import Process
from os import chmod
from os.path import basename
from shutil import move

from schedule import every

from cartierville.model import LOG_FORMAT, CartiervilleException, Show
from cartierville.rss import update_rss
from cartierville.stream import stream_to_tmp_file


def _job(show: Show) -> None:
    try:
        basicConfig(format=LOG_FORMAT, level=INFO)

        info(f"Starting to stream {show.title}")
        tmp_file, file_size = stream_to_tmp_file(show)

        info(f"Moving {tmp_file} to {show.output.directory}")
        chmod(move(tmp_file, show.output.directory), 0x744)

        info("Updating RSS file")
        update_rss(show, basename(tmp_file), file_size)
    except CartiervilleException as cartierville_exception:
        error(f"Error encountered: {cartierville_exception}")


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
