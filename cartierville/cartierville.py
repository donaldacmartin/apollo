"""cartierville.cartierville

Utility to save radio streams to local files

Functions:
    main(str, int, str) -> str
"""

from argparse import ArgumentParser
from logging import INFO, basicConfig, info, error
from shutil import move
from tempfile import NamedTemporaryFile
from time import time

from requests import RequestException, get


LOG_FORMAT = "%(asctime)s %(levelname)-8s %(message)s"
BUFFER_SIZE = 1024
MAX_BYTES_SIZE = 1024 * 1024 * 300


class CartiervilleException(Exception):
    """Base exception for this project"""


def _get_arg_parser() -> ArgumentParser:
    argparser = ArgumentParser()

    argparser.add_argument(
        "--url", type=str, required=True, help="Stream location to save"
    )

    argparser.add_argument(
        "--duration", type=int, required=True, help="Duration in seconds"
    )

    argparser.add_argument(
        "--output-dir", type=str, required=True, help="Output location"
    )

    return argparser


def main(url: str, duration: int, output_dir: str) -> str:
    """Save a stream to a temp file, then move to the output directory"""

    try:
        end_time = time() + duration
        downloaded_bytes = 0

        with NamedTemporaryFile(delete=False) as tmp_file:
            info("Temp file created at %s" % tmp_file.name)

            with get(url, stream=True) as req:
                info("Stream opened from %s for %d seconds" % (url, duration))

                while time() < end_time and downloaded_bytes <= MAX_BYTES_SIZE:
                    tmp_file.write(req.raw.read(BUFFER_SIZE))
                    downloaded_bytes += BUFFER_SIZE

                info("Stream completed after %d bytes" % downloaded_bytes)

        info("Moving %s to %s" % (tmp_file.name, output_dir))
        return move(tmp_file.name, output_dir)
    except RequestException as req_exception:
        error("Request error encountered: %s" % req_exception)
        raise CartiervilleException("Error streaming") from req_exception
    except EnvironmentError as env_error:
        error("Environment error encountered: %s" % env_error)
        raise CartiervilleException("Error saving to temp file") from env_error


if __name__ == "__main__":
    basicConfig(format=LOG_FORMAT, level=INFO)
    arg_parser = _get_arg_parser()
    args = arg_parser.parse_args()
    main(args.url, args.duration, args.output_dir)
