"""apollo.__main__

Utility to save radio streams to local files

Functions:
    main() -> None
"""

from logging import INFO, basicConfig, info
from time import sleep

from schedule import idle_seconds, run_pending

from apollo.config import read_shows
from apollo.job import schedule_show
from apollo.model import LOG_FORMAT


def main():
    """The main program"""

    for show in read_shows("./config.yml"):
        schedule_show(show)

    while True:
        sleep_time = idle_seconds()

        if sleep_time and sleep_time > 0:
            info(f"Sleeping {sleep_time} seconds until next schedule")
            sleep(sleep_time)
            run_pending()
        else:
            info("Nothing is scheduled, so exiting")
            return


if __name__ == "__main__":
    basicConfig(format=LOG_FORMAT, level=INFO)
    main()
