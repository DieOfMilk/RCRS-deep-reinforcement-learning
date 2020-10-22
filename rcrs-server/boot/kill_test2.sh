#!/bin/bash
echo "kill2 start"
PID2=`ps -C java -o pid`
echo $PID2
echo $PIDS
kill -9 $PID2
kill $$