FROM ubuntu:24.04
ENV DEBIAN_FRONTEND=noninteractive LANG=C.UTF-8
RUN apt-get update && apt-get install -y --no-install-recommends libreoffice-writer fonts-noto-cjk fontconfig && rm -rf /var/lib/apt/lists/*
WORKDIR /tmp
