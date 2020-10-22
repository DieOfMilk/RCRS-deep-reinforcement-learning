#!/bin/bash
trap 'echo "agent killing..."; echo $PIDS; killfunction;' 15

function killfunction () {
    for i in $PIDS
    do
        kill $i
    done
    exit
}

PIDS=$!
LOADER="adf.sample.SampleLoader"
echo "dirname $0"
cd `dirname $0`

PWD=`pwd`
echo "$PWD"
CP=`find $PWD/library/ -name '*.jar' ! -name '*-sources.jar' | awk -F '\n' -v ORS=':' '{print}'`
# echo "${CP}"
if [ ! -z "$1" ]; then
  echo "hi"
  echo "${CP}./build/classes/java/main"
  java -classpath '${CP}./build/classes/java/main' adf.Main ${LOADER} $*
  PIDS="$PIDS $!"
else
  echo "Options:"
  echo "-t [FB],[FS],[PF],[PO],[AT],[AC] number of agents"
  echo "-fb [FB]                         number of FireBrigade"
  echo "-fs [FS]                         number of FireStation"
  echo "-pf [PF]                         number of PoliceForce"
  echo "-po [PO]                         number of PoliceOffice"
  echo "-at [AT]                         number of AmbulanceTeam"
  echo "-ac [AC]                         number of AmbulanceCentre"
  echo "-all                             [alias] -t -1,-1,-1,-1,-1,-1"
  echo "-s [HOST]:[PORT]                 RCRS server host and port"
  echo "-h [HOST]                        RCRS server host (port:7000)"
  echo "-local                           [alias] -h localhost"
  echo "-pre [0|1]                       Precompute flag"
  echo "-d [0|1]                         Debug flag"
  echo "-mc [FILE]                       ModuleConfig file name"
  echo "-p [PORT]                        Server port number"
  echo "-r [GRPC]                        Grpc port number"
fi


