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

if __name__=='__main__':
    mapName = input("Please enter the map name at script/log")
    path = os.path.join('.','log/{}/data.pkl'.format(mapName))
    try:
        with open (path,'rb') as f:
            data = pickle.load(f)
        data = np.array(data)
        data = data.flatten()
    except:
        pass
    result = []
    y = len(data)//99
    for i in range(y):
        result.append(np.sum(data[99*i:99*(i+1)]))
    plt.plot(range(len(result)),result, label="{}".format(mapName))
    plt.legend(loc="upper right")
    plt.show()


    
