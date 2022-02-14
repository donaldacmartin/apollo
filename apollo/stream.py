"""apollo.stream

Handles streaming to a temporary file

Functions:
    stream_to_tmp_file(Show) -> Tuple[str, int]
"""

from logging import info, error
from tempfile import NamedTemporaryFile
from time import time
from typing import Tuple

from requests import RequestException, get

from apollo.model import SoundCloudSource, StreamException, MP3Source

BUFFER_SIZE = 1024
MAX_BYTES_SIZE = 1024 * 1024 * 300


def stream_to_tmp_file(source: MP3Source) -> Tuple[str, int]:
    """Save a stream to a temp file, then move to the output directory"""

    try:
        end_time = time() + source.duration
        downloaded_bytes = 0

        with NamedTemporaryFile(delete=False) as tmp_file:
            info(f"Temp file created at {tmp_file.name}")

            with get(source.url, stream=True) as req:
                info(f"Streaming for {source.duration} seconds")

                while time() < end_time and downloaded_bytes <= MAX_BYTES_SIZE:
                    tmp_file.write(req.raw.read(BUFFER_SIZE))
                    downloaded_bytes += BUFFER_SIZE

                info(f"Stream completed after {downloaded_bytes} bytes")

        return tmp_file.name, downloaded_bytes
    except RequestException as req_exception:
        error(f"Request error encountered: {req_exception}")
        raise StreamException("Error streaming") from req_exception
    except EnvironmentError as env_error:
        error(f"Environment error encountered: {env_error}")
        raise StreamException("Error saving to temp file") from env_error


def download_to_tmp_file(source: SoundCloudSource) -> Tuple[str, int]:
    """Download from the API to a temp file, then move to the output directory"""

    try:
        downloaded_bytes = 0

        with NamedTemporaryFile(delete=False) as tmp_file:
            info(f"Temp file created at {tmp_file.name}")

            with get(source.url, stream=True) as req:
                info(f"Downloading {source.url}")

                for chunk in req.iter_content(chunk_size=BUFFER_SIZE):
                    tmp_file.write(chunk)
                    downloaded_bytes += BUFFER_SIZE

                info(f"Download completed after {downloaded_bytes} bytes")

        return tmp_file.name, downloaded_bytes
    except RequestException as req_exception:
        error(f"Request error encountered: {req_exception}")
        raise StreamException("Error streaming") from req_exception
    except EnvironmentError as env_error:
        error(f"Environment error encountered: {env_error}")
        raise StreamException("Error saving to temp file") from env_error
