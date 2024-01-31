
#! /bin/bash

trap ctrl_c INT

function ctrl_c() {
        echo "** Trapped CTRL-C"
        kill $UPSTREAM_PID
}

java --version

java -jar echoapp-0.0.1-SNAPSHOT.jar --server.port=8181


