import tensorflow as tf
import numpy as np
import random
import math
import os
import gym_RCRS
import time
import gym
from models.myPPO2 import myPPO2 
from stable_baselines.common.policies import MlpPolicy, MlpLstmPolicy
from stable_baselines.common.vec_env import DummyVecEnv, VecNormalize, VecVideoRecorder
from stable_baselines.common.evaluation import evaluate_policy
from stable_baselines import DQN
from stable_baselines import logger
from stable_baselines.common import explained_variance, ActorCriticRLModel, tf_util, SetVerbosity, TensorboardWriter
from stable_baselines.common.runners import AbstractEnvRunner
from stable_baselines.common.policies import ActorCriticPolicy, RecurrentActorCriticPolicy
from stable_baselines.common.schedules import get_schedule_fn
from stable_baselines.common.tf_util import total_episode_reward_logger
from stable_baselines.common.math_util import safe_mean
import sys
from datetime import datetime
from tensorboard.backend.event_processing.event_accumulator import EventAccumulator
import pickle
import shutil
from stable_baselines.bench import Monitor
from pathlib import Path






if __name__=='__main__':
    startTime = datetime.now()
    # with tf.Graph().as_default():
    #         gpu_options = tf.GPUOptions(allow_growth=True)
    log_dir = './tmp/record/'
    portNo= input("Port No")
    grpcNo= input("grpc No")
    mapName = input("input map name")
    learning_rate = float(input("learning_rate"))
    gamma = float(input("gamma"))
    save_path = os.path.join('./log',mapName)
    save_path = os.path.join(save_path,'model')
    env = gym.make("RCRS-v0", portNo=portNo, grpcNo=grpcNo, buildingNo=57, maxTimeStamp=35,mapName=mapName, verbose=False,gamma=gamma)
    # model =DQN('MlpPolicy', env, learning_rate=3e-4, prioritized_replay=True, verbose=0,tensorboard_log="./tmp/tensor/Env_test2")
    # env = Monitor(env, log_dir, allow_early_resets=True)
    temp = env
    # env=DummyVecEnv([lambda:env])
    if not os.path.exists(save_path):
        os.makedirs(save_path)
    model = myPPO2(MlpPolicy, env, nminibatches=1,verbose=1, learning_rate=learning_rate, gamma = gamma, save_path=save_path)
    model.learn(total_timesteps = 20000)
    model.save(save_path)
    temp.killprocess()
    env.close()
    endTime = datetime.now()
    processTime = endTime-startTime
    print(processTime)
    print("Successfully translated")
    exit()
