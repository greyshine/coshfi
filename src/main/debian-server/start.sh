#!/bin/bash

VERSION=1.0-SNAPSHOT
PORT=443

sudo java -jar coffeeshopfinder-$VERSION.jar --datadir=appdata --server.port=$PORT 2>&1 1>>logs.log &