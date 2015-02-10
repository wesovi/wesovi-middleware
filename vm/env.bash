#!/bin/sh

# install git virtualbox vagrant xcode
# Xcode > Preferences > Downloads > Install "Command Line Tools"

# Python dependencies for Ansible
sudo easy_install jinja2
sudo easy_install PyYAML
sudo easy_install paramiko

# Get a stable branch because trust me
cd ~/Sites
git clone -b release1.0 git://github.com/ansible/ansible.git

# Add the following to ~/.bash_profile
source ~/Sites/ansible/hacking/env-setup -q
export ANSIBLE_HOSTS=~/.ansible_hosts

# Source it
source ~/.bash_profile

# Install the vagrant-ansible gem
# No longer needed as of Vagrant >=1.2
# vagrant gem install vagrant-ansible

# Download a starter pack of playbooks and an example Vagrantfile
git clone https://github.com/francisbesset/ansible-playbooks
curl https://raw.github.com/dsander/vagrant-ansible/master/Vagrantfile.sample > Vagrantfile

# GO
