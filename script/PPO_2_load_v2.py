import tensorflow as tf
import numpy as np
import random
import math
import os
import gym_RCRS2
import time
import gym
from stable_baselines import PPO2
from models.myPPO2 import myPPO2  
from stable_baselines.common.policies import MlpPolicy, CnnPolicy
from stable_baselines.common.vec_env import DummyVecEnv, VecNormalize, VecVideoRecorder
from stable_baselines.common.evaluation import evaluate_policy
from stable_baselines import DQN
import sys
from datetime import datetime
from tensorboard.backend.event_processing.event_accumulator import EventAccumulator
import pickle
import shutil
from stable_baselines.bench import Monitor

from stable_baselines import logger
from stable_baselines.common import explained_variance, ActorCriticRLModel, tf_util, SetVerbosity, TensorboardWriter
from stable_baselines.common.runners import AbstractEnvRunner
from stable_baselines.common.policies import ActorCriticPolicy, RecurrentActorCriticPolicy
from stable_baselines.common.schedules import get_schedule_fn
from stable_baselines.common.tf_util import total_episode_reward_logger
from stable_baselines.common.math_util import safe_mean



if __name__=='__main__':
    startTime = datetime.now()
    # with tf.Graph().as_default():
    #         gpu_options = tf.GPUOptions(allow_growth=True)
    log_dir = './tmp/record/'
    if len(sys.argv) == 4:
        env = gym.make("RCRS2-v0", portNo=int(sys.argv[1]), grpcNo=int(sys.argv[2]), buildingNo=36, maxTimeStamp=100,mapName=sys.argv[3])
    else:
        env = gym.make("RCRS2-v0", portNo=7003, grpcNo=50053, buildingNo=56, maxTimeStamp=30, mapName='bigTest2',verbose=False)
    # model =DQN('MlpPolicy', env, learning_rate=3e-4, prioritized_replay=True, verbose=0,tensorboard_log="./tmp/tensor/Env_test2")
    # env = Monitor(env, log_dir, allow_early_resets=True)
    temp = env
    env = DummyVecEnv([lambda: env]) 
    # env = VecNormalize(env, norm_obs=True, norm_reward=False, clip_obs=10.)
    learning_rate = 0.07
    trial=1
    
    result = []
    for j in range(29):
        print(j)
        model = myPPO2.load('./log/RCRS_big2_1/{}.zip'.format(j*20+10),env = env)
        temp_result=[]
        for _ in range(3):
            idNumber = 3
            obs = env.reset()
            total_sum=0
            for i in range(30):
                if idNumber == 3:
                    action,state = model.predict(obs[0])
                    print("action is", action)
                    temp.input(action)
                    action,state = model.predict(obs[0])
                    print("action is", action)
                    temp.input(action)
                else:
                    action,state = model.predict(obs[0])
                    print("action is", action)
                    temp.input(action)
                obs, rewards, dones, info = temp.step()
                idNumber = info[0]['idNumber']
                if idNumber==3:
                    total_sum+=rewards[0][0]
                    total_sum+=rewards[0][1]
                elif idNumber==2:
                    total_sum+=rewards[0][1]
                else:
                    total_sum+=rewards[0][0]
                # if i==0:
                #     temp2=input()
                # env.render()
                if dones[0]: 
                    temp_result.append(total_sum)
                    break
                # temp2=input()
        result.append(temp_result)
    print(result)
    with open(os.path.join('.','result.pkl'),'wb') as f:
        pickle.dump(result, f)
    print("Successfully translated")
    temp.killprocess()
    env.close()
    exit()



