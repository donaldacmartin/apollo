"""podcast.stream

Handles streaming to a temporary file

Functions:
    stream_to_tmp_file(str, int) -> Tuple[str, int]
"""

from logging import info, error
from tempfile import NamedTemporaryFile
from time import time
from typing import Tuple

from requests import RequestException, get

from cartierville.model import StreamException

BUFFER_SIZE = 1024
MAX_BYTES_SIZE = 1024 * 1024 * 300


def stream_to_tmp_file(url: str, duration: int) -> Tuple[str, int]:
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

        return tmp_file.name, downloaded_bytes
    except RequestException as req_exception:
        error("Request error encountered: %s" % req_exception)
        raise StreamException("Error streaming") from req_exception
    except EnvironmentError as env_error:
        error("Environment error encountered: %s" % env_error)
        raise StreamException("Error saving to temp file") from env_error
