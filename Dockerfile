FROM python:3.10-slim-bullseye

COPY poetry.lock pyproject.toml $HOME/
ADD apollo $HOME/apollo/

RUN pip3 install poetry
RUN poetry install
RUN poetry build
RUN pip3 install dist/*.tar.gz

ENTRYPOINT [ "python3", "-m", "apollo" ]