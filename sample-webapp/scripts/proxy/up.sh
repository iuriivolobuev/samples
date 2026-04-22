#!/bin/bash

with_init=${1:-true}

cd "$(dirname "$0")" || exit 1
if [ "$with_init" = true ]; then
  npm init -y
  npm install express http-proxy-middleware
fi
node server.js
