#!/bin/bash

SERVICE_NAME="rphc"
PATH_TO_JAR="/home/pi/share/workspaces/max/raspberry-pi-home-control/target/final/raspberry-pi-home-control-0.0.1.jar"
PID_PATH_NAME="/tmp/rphc-pid"

echo "Starting $SERVICE_NAME ..."

if [ ! -f $PID_PATH_NAME ]; then
	nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
	echo $! > $PID_PATH_NAME
	echo "$SERVICE_NAME started ..."
else
	echo "$SERVICE_NAME is already running ..."
fi
