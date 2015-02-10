#!/bin/bash

echo "Installing Ansible..."
apt-get autoremove -y
apt-get install software-properties-common
apt-add-repository ppa:ansible/ansible
#apt-get update
apt-get install ansible -y
#apt-get autoremove
#cd /vagrant/ansible