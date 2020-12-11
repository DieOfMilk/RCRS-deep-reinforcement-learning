#!/bin/bash
trap 'killfunction;' 15
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

waitFor $LOGDIR/kernel.log "Kernel has shut down" 30

wait $!
exit
