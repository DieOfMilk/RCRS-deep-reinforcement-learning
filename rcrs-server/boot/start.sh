#!/bin/bash
trap 'echo "killing..."; echo $PIDS; killfunction;' 15
. functions.sh

function killfunction () {
    for i in $PIDS
    do
        pkill -P $i
    done
    exit
}

processArgs $*

# Delete old logs
rm -f $LOGDIR/*.log
# sh kill_test.sh

# startGIS --autorun --nomenu 
startKernel --nomenu --autorun
startSims
startViewer

echo "Start your agents"
waitFor $LOGDIR/kernel.log "Kernel has shut down" 30

wait $!
exit
