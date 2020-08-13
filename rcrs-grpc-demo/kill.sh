#!/bin/bash
echo "kill start $0"
echo `pwd`
ps -ef | grep `pwd` | awk '{print "kill -9",$2}' | sh >/dev/null 2>&1
