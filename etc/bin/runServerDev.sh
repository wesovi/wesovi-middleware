#!/bin/sh

APPLICATION_DIR=/Users/ivan/Development/Wesovi/whitelabel/core/application

echo "Setting up the profile"
export wlRunMode=dev
cd $APPLICATION_DIR
echo "Launching the server...."
sbt run
