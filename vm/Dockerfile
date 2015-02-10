# Scala, Java, and sbt for Ubuntu 12.04 LTS
#
# Version     0.1

FROM ubuntu:precise
MAINTAINER Aleksander Pejcic aleksander.pejcic@gmail.com

RUN echo "deb http://archive.ubuntu.com/ubuntu precise main universe" > /etc/apt/sources.list

# install add-apt-repository tool
RUN apt-get install -y python-software-properties

# Java 7 apt repository
RUN add-apt-repository ppa:webupd8team/java

# install wget for downloading files
RUN apt-get install -y wget

# Typesafe repo (contains old versions but they have all dependencies we need later on)
RUN wget http://apt.typesafe.com/repo-deb-build-0002.deb
RUN dpkg -i repo-deb-build-0002.deb
RUN rm -f repo-deb-build-0002.deb

# update apt repositories
RUN apt-get update

# Packages
RUN apt-get update && \
    apt-get -y install --no-install-recommends openjdk-7-jdk mongodb wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN wget -q http://downloads.typesafe.com/scala/2.11.5/scala-2.11.5.deb && \
    dpkg -i scala-2.11.5.deb && \
    rm scala-2.11.5.deb

RUN wget -q https://dl.bintray.com/sbt/debian/sbt-0.13.7.deb && \
    dpkg -i sbt-0.13.7.deb && \
    rm sbt-0.13.7.deb

# Default application logic
WORKDIR /vagrant
CMD sbt ~re-start