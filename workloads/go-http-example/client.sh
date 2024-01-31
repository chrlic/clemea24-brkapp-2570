
#! /bin/bash

while true 
do
  curl http://localhost:8090/go/hello
  echo ""
  sleep 5
  curl http://localhost:8090/go/headers
  echo ""
  sleep 5
done