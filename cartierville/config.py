"""cartierville.config

Parses and validates the config YAML file.

Functions:
    read_shows(str) -> List[Show]
"""

from os import R_OK, W_OK, access
from os.path import exists
from typing import Dict, List
from yaml import YAMLError, safe_load

from cerberus import Validator

from cartierville.model import CONFIG_SCHEMA, ConfigException, Output, Show


def _is_valid(yaml_dict: dict) -> bool:
    return Validator(CONFIG_SCHEMA).validate(yaml_dict)


def _validate_outputs(outputs: List[Output]) -> None:
    for output in outputs:
        if exists(output.directory):
            if access(output.directory, W_OK):
                continue

            raise ConfigException(f"{output.directory} is not writable")

        raise ConfigException(f"{output.directory} does not exist")


def _read_yaml(filename: str) -> dict:
    if exists(filename):
        if access(filename, R_OK):
            try:
                with open(filename, "r", encoding="utf-8") as yaml_file:
                    yaml_dict = safe_load(yaml_file)

                if _is_valid(yaml_dict):
                    return yaml_dict

                raise ConfigException(f"File {filename} readable, but invalid")
            except OSError as os_error:
                raise ConfigException("Error reading the config file") from os_error
            except YAMLError as yaml_error:
                raise ConfigException("Error parsing the YAML") from yaml_error

        raise ConfigException(f"File {filename} is not readable")

    raise ConfigException(f"File {filename} does not exist")


def _to_show(show: dict, outputs: Dict[str, Output]) -> Show:
    if show["output"] in outputs:
        return Show(
            show["title"],
            show["summary"],
            show["author"],
            show["url"],
            int(show["duration"]),
            show["days"],
            show["time"],
            outputs[show["output"]],
        )

    raise ConfigException(f"Output {show['output']} does not exist")


def _to_config_obj(yaml_dict: dict) -> List[Show]:
    outputs = {name: Output(**entry) for name, entry in yaml_dict["outputs"].items()}
    _validate_outputs(list(outputs.values()))
    return [_to_show(show, outputs) for show in yaml_dict["shows"]]


def read_shows(filename: str) -> List[Show]:
    """Reads and validates the config YAML file"""

    yaml_dict = _read_yaml(filename)
    return _to_config_obj(yaml_dict)
