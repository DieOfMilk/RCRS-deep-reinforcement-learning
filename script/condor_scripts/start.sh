#!/bin/bash

cd "./RCRS-deep-reinforcement-learning/script"
echo $HOSTNAME
source ~/.bashrc
conda activate condor
echo "start.sh start"
exec python3 "modify_PPO2.py"
