#!/usr/bin/env bash
set -e
shopt -s globstar

rm -rf build
mkdir -p build

javac -d build src/**/*.java

cd build

jar cfve "../solver.jar" de.unmr.bacluster.Main **/*.class

cd ..
rm -rf build
