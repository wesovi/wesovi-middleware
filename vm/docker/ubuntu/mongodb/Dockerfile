# Install a more up to date mongodb than what is included in the default ubuntu repositories.

FROM ubuntu:latest
MAINTAINER Ivan Corrales Solera

#update apt sources
RUN apt-get update --fix-missing

# Optional, upgrade to latest (takes a while), but before install sshd
RUN apt-get upgrade -y


# Install prerequisites
RUN apt-get install -y software-properties-common

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10
RUN echo "deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen" | tee -a /etc/apt/sources.list.d/10gen.list
RUN apt-get update
RUN apt-get -y install apt-utils
RUN apt-get -y install mongodb-10gen

RUN mkdir -p /data/mongodb

# Define mountable directories.
VOLUME ["/data/mongodb"]

# Define working directory.
WORKDIR /data/mongodb

# Expose ports.
#   - 27017: process
#   - 28017: http
EXPOSE 27017
EXPOSE 28017

ENTRYPOINT ["/usr/bin/mongod", "--config", "/etc/mongodb.conf"]