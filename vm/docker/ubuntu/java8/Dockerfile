# Install a more up to date mongodb than what is included in the default ubuntu repositories.

FROM ubuntu
MAINTAINER Ivan Corrales Solera

#update apt sources
RUN apt-get update --fix-missing

# Optional, upgrade to latest (takes a while), but before install sshd
RUN apt-get upgrade -y


# Install prerequisites
RUN apt-get install -y software-properties-common

# Install java8
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer