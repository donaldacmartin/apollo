"""cartierville.args

Returns the argument parser when invoking the program from the command line

Functions:
    get_arg_parser() -> ArgumentParser
"""

from argparse import ArgumentParser


def get_arg_parser() -> ArgumentParser:
    """Return the argument parser"""

    argparser = ArgumentParser()

    argparser.add_argument("--title", type=str, required=True, help="Show title")

    argparser.add_argument(
        "--summary", type=str, required=True, help="Brief description of show"
    )

    argparser.add_argument(
        "--author", type=str, required=True, help="Who recorded the show"
    )

    argparser.add_argument(
        "--url", type=str, required=True, help="Stream location to save"
    )

    argparser.add_argument(
        "--duration", type=int, required=True, help="Duration in seconds"
    )

    argparser.add_argument(
        "--output-dir", type=str, required=True, help="Output location"
    )

    argparser.add_argument(
        "--output-uri", type=str, required=True, help="Public file location"
    )

    return argparser
