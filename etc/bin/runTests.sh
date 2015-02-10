#!/bin/sh

APPLICATION_DIR=/Users/ivan/Development/Wesovi/whitelabel/core/ms

echo "Setting up the profile"
export wlRunMode=test
cd $APPLICATION_DIR
echo "Running integration tests"
sbt test
#> logs/test_execution.log
echo "Tests execution completed"
