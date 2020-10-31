import gym
from gym import error, spaces, utils
from gym.utils import seeding
import numpy as np
import logging
import grpc
from gym_RCRS.envs import RCRS_pb2
from gym_RCRS.envs import RCRS_pb2_grpc
import time
import threading
from concurrent import futures
import os
import shutil
import subprocess
import signal
from numpy import inf
import pickle
from matplotlib import pyplot as plt
import matplotlib

if __name__=='__main__':
    result_path = os.path.join('.','result.pkl')
    with open(result_path,'rb') as f:
        total = pickle.load(f)
    new_total=[]
    for i in total:
        new_total.append(sum(i)/3)
    gready = []
    for _ in range(10,990,20):
        gready.append(-1)

    matplotlib.rcParams.update({'font.size':30})

    plt.plot(range(10,990,20),new_total, label="PPO")
    plt.plot(range(10,990,20),gready, label="greedy methods")
    plt.xlabel("Episode Number",fontsize=48)
    plt.ylabel("Total Sum of Rewards",fontsize=48)
    plt.xticks(range(0,1000,50))
    plt.legend(loc="lower right",fontsize=24)
    plt.gcf().subplots_adjust(top=0.96, bottom=0.122, right=0.98, left=0.11)
    plt.show()


    
