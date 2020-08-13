#!/bin/bash
trap 'echo "killing..."; ./kill_test.sh; exit' 15
trap 'echo "killing..."; ./kill_test.sh; exit' INT
. functions.sh

processArgs $*

# Delete old logs
rm -f $LOGDIR/*.log
# sh kill.sh

# startGIS --autorun --nomenu 
startKernel --nomenu --autorun
startSims
startViewer

echo "Start your agents"
waitFor $LOGDIR/kernel.log "Kernel has shut down" 5
# echo "Done"

# sleep 5
# echo "HI"
# jobs -p
# jobs -p %JOB_SPEC
# kill 
# kill $PIDS
# kill $!
# ./kill.sh
