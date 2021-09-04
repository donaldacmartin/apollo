"""cartierville.cartierville

Utility to save radio streams to local files

Functions:
    main(str, int, str) -> str
"""

from logging import INFO, basicConfig, info, error
from os.path import basename, join
from shutil import move

from cartierville.args import get_arg_parser
from cartierville.model import CartiervilleException, PodcastDTO
from cartierville.rss import update_rss
from cartierville.stream import stream_to_tmp_file

LOG_FORMAT = "%(asctime)s %(levelname)-8s %(message)s"


def main(podcast: PodcastDTO, duration: int, output_dir: str, uri_path: str):
    """Save a stream to a temp file, then move to the output directory"""

    try:
        tmp_file, file_size = stream_to_tmp_file(podcast.url, duration)

        info("Moving %s to %s" % (tmp_file, output_dir))
        move(tmp_file, output_dir)

        info("Updating RSS file")
        rss_path = join(output_dir, "feed.xml")
        link = uri_path + "/" + basename(tmp_file)
        update_rss(rss_path, podcast, link, duration, file_size)
    except CartiervilleException as cartierville_exception:
        error("Error encountered: %s" % cartierville_exception)
        raise cartierville_exception


if __name__ == "__main__":
    basicConfig(format=LOG_FORMAT, level=INFO)
    arg_parser = get_arg_parser()
    args = arg_parser.parse_args()
    podcast_req = PodcastDTO(args.title, args.summary, args.author, args.url)
    main(podcast_req, args.duration, args.output_dir, args.output_uri)
