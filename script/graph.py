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
    total=[]
    for i in range(19):
        path = os.path.join('.','RCRS{}/data.pkl'.format(i))
        try:
            with open (path,'rb') as f:
                temp = pickle.load(f)
            # data = np.asarray([])
            # for i in temp:
            #     data = np.append(data,temp)
            # print(data.shape)
            # # print(data[0])
            # data = data.flatten()
            total.append(temp)
        except:
            print("error happend")
            pass
    total_path = os.path.join('.','total')
    with open(total_path, 'wb') as f:
        pickle.dump(total,f)
    # print(total)
    # print(total)
    # print(total[0])
    # new_total=[]
    # for i in total:
    #     temp=[]
    #     y = 0
    #     for j in i:
    #         if j>=y:
    #             y=j
    #         else:
    #             temp.append(y)
    #             y=0
    #         if j==1:
    #             print("there is 1")
    #     temp2 = []
    #     new_total.append(temp2)
    # new_total_path = os.path.join('.','new_total')
    # print(len(new_total))
    # print(new_total[0])
    # with open(new_total_path, 'wb') as f:
    #     pickle.dump(new_total,f)
    for i in [6]:
        plt.plot(range(len(total[i])),total[i], label="Learning_rate {}\n Decay_rate {}".format(0.004,0.99))
    plt.ylim([0,10])
    plt.xlabel("Episode No.",fontsize=48)
    plt.ylabel("Remained Fire No.",fontsize=48)
    plt.legend(loc="upper right",fontsize=24)
    plt.show()


    
