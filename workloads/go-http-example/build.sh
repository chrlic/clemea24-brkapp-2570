
#! /bin/bash

docker build . --tag=chrlic/go-compliance:v1.0

docker push chrlic/go-compliance:v1.0

