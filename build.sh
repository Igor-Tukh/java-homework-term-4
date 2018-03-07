#!/bin/bash

set -ev

build_subdirectory () {
  while read name;
  do
    cd $name
    if [ -f "gradlew" ]
    then
      chmod +x gradlew
      ./gradlew build
    fi
    cd ..
  done
  exit
}

find -maxdepth 1 -mindepth 1 -type d | build_subdirectory
