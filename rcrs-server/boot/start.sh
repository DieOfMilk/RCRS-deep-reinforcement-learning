#!/bin/bash
trap 'echo "killing..."; ./kill_test.sh; exit' 15
trap 'echo "killing..."; ./kill_test.sh; exit' INT
. functions.sh

processArgs $*

# Delete old logs
rm -f $LOGDIR/*.log
sh kill_test.sh

# startGIS --autorun --nomenu 
startKernel --nomenu --autorun
startSims
startViewer

echo "Start your agents"
waitFor $LOGDIR/kernel.log "Kernel has shut down" 30

kill $PIDS
./kill_test.sh
