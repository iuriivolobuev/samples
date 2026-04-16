#!/bin/bash

docker run --name postgres \
  -e POSTGRES_DB=dev -e POSTGRES_USER=devuser -e POSTGRES_PASSWORD=devpass \
  -p 5432:5432 -d postgres:18.3
