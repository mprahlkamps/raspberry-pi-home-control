#!/bin/bash

SERVICE_NAME="rphc"
PATH_TO_JAR="/home/pi/share/workspaces/max/raspberry-pi-home-control/target/final/raspberry-pi-home-control-0.0.1.jar"
PID_PATH_NAME="/tmp/rphc-pid"

if [ -f $PID_PATH_NAME ]; then
	PID=$(cat $PID_PATH_NAME);
	echo "$SERVICE_NAME stoping ..."
	kill $PID;
	echo "$SERVICE_NAME stopped ..."
	rm $PID_PATH_NAME
else
	echo "$SERVICE_NAME is not running ..."
fi

