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



class RCRSEnv(gym.Env):
    metadata={'render.modes':['human']}
    def __init__(self, portNo, grpcNo, maxTimeStamp, buildingNo,mapName, verbose=False): # AreaList, agentList, algorithm,
        self.actionNo=4
        self.timeStamp=-1
        self.total_step = 0
        self.mapName=mapName
        # self.BuildingList = BuildingList
        self.verbose = verbose
        self.buildingNo = buildingNo
        self.maxTimeStamp = maxTimeStamp
        self.portNo = str(portNo)
        self.grpcNo = str(grpcNo)
        self.maxWater = 25000
        self.agentList = [1962675462,210552869]
        self.action_space = spaces.MultiDiscrete([self.buildingNo+2]*len(self.agentList))
        self.observation_space = spaces.Box(low=np.array([0]*(buildingNo*3+len(self.agentList))+[-inf,-inf]*len(self.agentList)+[1]*len(self.agentList)).astype(int),high=np.array([inf]*(buildingNo*3)+[self.maxWater]*len(self.agentList)+[inf,inf]*len(self.agentList)+[2]*len(self.agentList)).astype(int),dtype= np.int) ## id, is on fire
        self.obs= {}
        self.buildingIdList = [247, 248, 249, 250, 251, 253, 254, 255, 905, 934, 935, 936, 937, 938, 939, 940, 941, 942, 943, 
        944, 945, 946, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956, 957, 958, 959, 960] # removed 298
        seedNo=23
        self.np_random , seed = gym.utils.seeding.np_random(seedNo)
        self.kernel = None
        self.agent = None
        self.reward = 0
        

    def step(self,action):
        # self.timeStamp +=1
        self.total_step +=1
        print("start step. Total step is : {}".format(self.total_step))
        self.run_action(action)
        self.done= False
        self.wait_request(30,0.1)
        self.obs = self.connection.getObs(30,0.1)
        self.obs=np.append(self.obs,self.connection.getBusy(30,0.1))
        print(self.obs)
        self.reward = self.getReward()
        if self.timeStamp==self.maxTimeStamp:
            print("finished max time stamp")
            self.done=True
            print("The final score is {}".format(self.reward))
        info = {}
        if not np.isin(1,self.obs[0:-8:3]): # about remaining fire
            info['is_success']=True   
        else:
            info['is_success']=False
        return self.obs, self.reward, self.done, info
    def run_action(self, action):
        action_list = []
        for i in action:
            if i == self.buildingNo:
                action_list.append("R")
            elif i == self.buildingNo+1:
                action_list.append("C")
            # elif i == self.buildingNo+2:
            #     action_list.append("C")
            else:
                action_list.append("S "+ str(self.buildingIdList[i]))    
        self.connection.inputAction(action_list)

    def reset(self):
        print("start reset")
        self.timeStamp = -1
        self.server, self.connection  = self.serve(self.grpcNo)
        logName = self.mapName
        
        origin_map_path = './../rcrs-server/maps/gml/EnvTest'
        map_path = './../rcrs-server/maps/gml/' + logName
        try:
            shutil.copytree(origin_map_path, map_path)
        except:
            pass
        if self.kernel:
            self.kernel.terminate()
            self.kernel.wait()
        if self.agent:
            self.agent.terminate()
            self.agent.wait()
        commonCfg = []
        common_path = os.path.join(map_path, 'config/common.cfg')
        with open(common_path, 'r') as f:
            while True:
                line = f.readline()
                if not line: break
                commonCfg.append(line)
        commonCfg[6] = "kernel.port: " + self.portNo +'\n'
        with open(common_path, 'w') as f:
            for i in commonCfg:
                f.writelines(i)
        logpath = os.path.join("./../log",logName)
        mapfile = os.path.join("./../maps/gml", logName)
        common_path = os.path.join(mapfile, 'config')
        mapfile = os.path.join(mapfile, 'map')
        if self.verbose:
            self.kernel = subprocess.Popen("./start.sh -l {} -m {} -c {} -p {} -r {}".format(logpath,mapfile,common_path,self.portNo,self.grpcNo).split(), 
            shell=False, cwd = "./../rcrs-server/boot")
        else:
            self.kernel = subprocess.Popen("./start.sh -l {} -m {} -c {} -p {} -r {} -g".format(logpath,mapfile,common_path,self.portNo,self.grpcNo).split(), 
            shell=False, cwd = "./../rcrs-server/boot")
        time.sleep(8)
        self.agent = subprocess.Popen("./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo).split(),shell=False, cwd = "./../rcrs-grpc-demo") 
        self.wait_request(30,0.1) ## wait until action request
        self.obs = self.connection.getObs(30,0.1)
        self.obs=np.append(self.obs,self.connection.getBusy(30,0.1))
        self.step([self.buildingNo,self.buildingNo])
        self.step([self.buildingNo,self.buildingNo])
        return self.obs

    def render(self):
        self.timeStamp = -1
        logName = self.mapName
        
        origin_map_path = './../rcrs-server/maps/gml/EnvTest'
        map_path = './../rcrs-server/maps/gml/' + logName
        try:
            shutil.copytree(origin_map_path, map_path)
        except:
            pass
        if self.kernel:
            self.kernel.terminate()
        if self.agent:
            self.agent.terminate()
        commonCfg = []
        common_path = os.path.join(map_path, 'config/common.cfg')
        with open(common_path, 'r') as f:
            while True:
                line = f.readline()
                if not line: break
                commonCfg.append(line)
        commonCfg[6] = "kernel.port: " + self.portNo +'\n'
        with open(common_path, 'w') as f:
            for i in commonCfg:
                f.writelines(i)
        logpath = os.path.join("./../rcrs-server/log",logName)
        mapfile = os.path.join("./../rcrs-server/maps/gml", logName)
        common_path = os.path.join(mapfile, 'config')
        mapfile = os.path.join(mapfile, 'map')
        self.kernel = subprocess.Popen(["xterm","-e","./start.sh -l {} -m {} -c {} -p {} -r {}".format(logpath,mapfile,common_path,self.portNo,self.grpcNo)], shell=False, cwd = "./../rcrs-server/boot")
        time.sleep(8)
        self.agent = subprocess.Popen(["xterm","-e","./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo)],shell=False, cwd = "./../rcrs-grpc-demo")
        self.wait_request(30,0.1) ## wait until action request
        self.obs = self.connection.getObs(30,0.1)
        self.step(self.buildingNo)
        self.step(self.buildingNo)
        return self.obs

    def serve(self,portNo):
        connection = SimpleConnection(self.agentList,self.buildingIdList)
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
        RCRS_pb2_grpc.add_SimpleConnectionServicer_to_server(connection, server)
        port = '[::]:' + portNo
        server.add_insecure_port(port)
        server.start()
        return server, connection
    def wait_request(self, timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end:
            if self.timeStamp != self.connection.getTimeStamp():
                self.timeStamp = self.connection.getTimeStamp()
                print("run time stamp with {}".format(self.timeStamp))
                return True
            time.sleep(period)
        print("No action request error")
        exit()
        return False
    def getReward(self):
        firenumber = 0
        isonfire = np.array(self.obs[0:-8:3])
        for i in isonfire:
            firenumber +=i
        self.reward = 36-firenumber
        return self.reward
    def getObs(self):
        return self.obs
    def killprocess(self):
        print("start kill process")
        if self.kernel:
            self.kernel.terminate()
            self.kernel.wait()
        if self.agent:
            self.agent.terminate()
            self.kernel.wait()


class SimpleConnection(RCRS_pb2_grpc.SimpleConnectionServicer):

    def __init__(self,agentList,buildingIdList):
        self.locker = 0
        self.timestamp = -1
        self.obs = None
        self.action = None
        self.busy = [0,0]
        self.busycheck = np.array([0,0])
        self.agentList = agentList
        self.buildingIdList = buildingIdList
    def AskBusy(self, request, context):
        # print("get something on here")
        # print(request)
        for i in range(len(self.agentList)):
            if request.AgentID == self.agentList[i]:
                # print("yes")
                self.busycheck[i] = 1
                self.busy[i] = request.Busy
                # print("check busy from agent")
                break
            # else:
                # print("No", request.AgentID)
        return RCRS_pb2.Check(check=1)

    def SetActionType(self, request, context):
        if request.AgentType == 1:
            time.sleep(0.5)
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        if request.AgentType == 2:
            print("Select Action of Fire Brigade: ")
        if request.AgentType == 3:
            time.sleep(0.5)
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        # time.sleep(10)
        check = self.wait_action(30,0.1)
        if not check:
            print("no action input")
            exit()
        for i in range(len(self.agentList)):
            if request.AgentID == self.agentList[i]:
                templist = self.action[i].split(" ")
                break
        for i in range(len(self.agentList)):
            if self.agentList[i] == request.AgentID:
                if templist[0] == "S":
                    x = templist[1]
                    y = "0"
                    return RCRS_pb2.ActionType(actionType= 1, x=float(x), y=float(y))
                elif templist[0] == "M":
                    x="0"
                    y="0"
                    return RCRS_pb2.ActionType(actionType= 2, x=float(x), y=float(y))
                elif templist[0] == "C":
                    x="0"
                    y="0"
                    return RCRS_pb2.ActionType(actionType= 3, x=float(x), y=float(y))
                else:
                    x="0"
                    y="0"
                    return RCRS_pb2.ActionType(actionType= 4, x=float(x), y=float(y))
    def RunTimestep(self, request, context):
        ## should wait until previous timestep is finished
        self.request = request
        self.timestamp = request.time
        obs=np.zeros(3*len(self.buildingIdList),dtype=np.int)
        temp = np.zeros(3*len(self.agentList),dtype=np.int)
        for i in request.areas:
            if i.uRN=="Building" or i.uRN=="AmbulanceCentre" or i.uRN=="FireStation" or i.uRN=="PoliceOffice" or i.uRN=="GasStation" or i.uRN=="Refuge":
                for j in range(len(self.buildingIdList)):
                    if i.iD == self.buildingIdList[j]:
                        if i.isOnFire:
                            obs[3*j]=1
                        else:
                            obs[3*j]=0
                        obs[3*j+1]=i.temperature
                        obs[3*j+2]=i.fieryness
                        break
        for i in request.humans:
            if i.uRN=="FireBrigade":
                for j in range(len(self.agentList)):
                    if i.iD == self.agentList[j]:
                        temp[j*3]=i.water
                        temp[j*3+1]=i.x
                        temp[j*3+2]=i.y
                        break
                # for j in range(len(self.agentList)):
                #     if self.agentList[j] == i.iD:
                #         obs.append(self.busy[j])
                # obs.append(self.busycheck)
        obs = np.append(obs,temp)
        self.obs = obs
        self.action = None
        print("generate obs successfully")
        return RCRS_pb2.ActionType(actionType=0, x=float(0), y=float(0))

    def getTimeStamp(self):
        return self.timestamp
    def inputAction(self, action):
        self.action = action
        print("input Action : {}".format(self.action))
        return True
    def wait_action(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end:
            if self.action:
                return True
            time.sleep(period)
        print("action get error")
        exit()
        return False
    def getObs(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end:
            if not (self.obs is None):
                print("return obs at get obs")
                obs = self.obs.copy()
                self.obs= None
                return obs
            time.sleep(period)
        print("obs get error")
        exit()
        return False
    def getBusy(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end:
            if not np.isin(0,self.busycheck):
                print("get Busy")
                busy = self.busy.copy()
                self.busycheck = np.zeros(len(self.agentList),dtype=np.int)
                return busy
            time.sleep(period)
        print("busy get error")
        exit()
        return False
