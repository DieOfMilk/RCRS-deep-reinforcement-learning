#!/bin/bash
echo "kill start $0"
echo `pwd`
ps | grep java | awk '{print "kill -9",$1}' | sh >/dev/null 2>&1 | echo "killed"
exit