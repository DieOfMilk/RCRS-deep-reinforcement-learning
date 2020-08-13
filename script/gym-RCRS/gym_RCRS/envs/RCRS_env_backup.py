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
    def __init__(self, portNo, grpcNo, maxTimeStamp, buildingNo,mapName): # AreaList, agentList, algorithm,
        self.actionNo=4
        self.timeStamp=-1
        self.total_step = 0
        self.mapName=mapName
        # self.BuildingList = BuildingList
        self.buildingNo = buildingNo
        self.maxTimeStamp = maxTimeStamp
        self.portNo = str(portNo)
        self.grpcNo = str(grpcNo)
        self.maxWater = 25000
        # self.action_space = sapces.MultiDiscrete([len(AreaList)]*len(agentList))
        self.action_space = spaces.Discrete(int(self.buildingNo+2))
        # self.observation_space = spaces.Dict({'map':spaces.Box(low=0,high=3,shape=(self.row,self.col,1),dtype=np.int),'truckPosY':spaces.Discrete(self.row),'truckPosX':spaces.Discrete(self.col)})
        # self.observation_space = spaces.Box(low=[buildingmin],high=buildingmax+1,shape=(self.row,self.col,2),dtype=np.int)
        # self.observation_sapce = observation_sapce = spaces.Dict({'id':spaces.Discrete(1000),'neighbors':spaces.Box(low=0,high=1000,shape=(20,), dtype=np.int), 'totalCost':spaces.Discrete(1000)})
        self.observation_space = spaces.Box(low=np.array([0]*(buildingNo*3+1)+[-inf,-inf]+[0]).astype(int),high=np.array([inf]*(buildingNo*3)+[self.maxWater]+[inf,inf]+[1]).astype(int),dtype= np.int) ## id, is on fire
        self.obs= {}
        self.buildingIdList = [247, 248, 249, 250, 251, 253, 254, 255, 298, 905, 934, 935, 936, 937, 938, 939, 940, 941, 942, 943, 
        944, 945, 946, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956, 957, 958, 959, 960]
        seedNo=23
        self.np_random , seed = gym.utils.seeding.np_random(seedNo)
        self.server, self.connection  = self.serve(self.grpcNo)
        self.kernel = None
        self.agent = None
        self.reward = 0
        

    def step(self,action):
        # self.timeStamp +=1
        self.total_step +=1
        print("start step. Total step is : {}".format(self.total_step))
        if action == self.buildingNo:
            direction = "R"
        elif action == self.buildingNo+1:
            direction="M"
        elif action == self.buildingNo+2:
            direction="C"
        else:
            direction = "S"
            direction = direction + " " + str(self.buildingIdList[action])
        self.connection.inputAction(direction)
        self.done= False
        self.wait_request(30,0.1)
        self.obs = self.connection.getObs(30,0.1)
        self.reward = self.getReward()
        if self.timeStamp==self.maxTimeStamp:
            print("finished max time stamp")
            self.done=True
            # reward = self.stub.GetFinalReward(RCRS_pb2.Direction(direction=direction)).reward
            print("The final score is {}".format(self.reward))
        info = {}
        if not np.isin(1,self.obs[0:-3:3]): # about remaining fire
            info['is_success']=True   
        else:
            info['is_success']=False
        return self.obs, self.reward, self.done, info
    def step2(self,action):
        # self.timeStamp +=1
        # self.total_step +=1
        # print("start step. Total step is : {}".format(self.total_step))
        if action == self.buildingNo:
            direction = "R"
        elif action == self.buildingNo+1:
            direction="C"
        else:
            direction = "S"
            direction = direction + " " + str(self.buildingIdList[action])
        self.connection.inputAction(direction)
        self.done= False
        self.wait_request(30,0.1)
        self.obs = self.connection.getObs(30,0.1)
        self.reward = self.getReward()
        if self.timeStamp==self.maxTimeStamp:
            print("finished max time stamp")
            self.done=True
            # reward = self.stub.GetFinalReward(RCRS_pb2.Direction(direction=direction)).reward
            print("The final score is {}".format(self.reward))
        info = {}
        if not np.isin(1,self.obs[0:-3:3]): # about remaining fire
            info['is_success']=True   
        else:
            info['is_success']=False
        return self.obs, self.reward, self.done, info

    def reset(self):
        print("start reset")
        self.timeStamp = -1
        logName = self.mapName
        
        origin_map_path = '/home/jaebak/Desktop/temp/rcrs-server/maps/gml/EnvTest'
        map_path = '/home/jaebak/Desktop/temp/rcrs-server/maps/gml/' + logName
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
        logpath = os.path.join("/home/jaebak/Desktop/temp/rcrs-server/log",logName)
        mapfile = os.path.join("/home/jaebak/Desktop/temp/rcrs-server/maps/gml", logName)
        common_path = os.path.join(mapfile, 'config')
        mapfile = os.path.join(mapfile, 'map')
        self.kernel = subprocess.Popen("./start.sh -l {} -m {} -c {} -p {} -r {} -g".format(logpath,mapfile,common_path,self.portNo,self.grpcNo).split(), 
        shell=False, cwd = "/home/jaebak/Desktop/temp/rcrs-server/boot")
        # self.kernel = subprocess.Popen(["xterm","-e","./start.sh -l {} -m {} -c {} -p {} -r {} -g &".format(logpath,mapfile,common_path,self.portNo,self.grpcNo)], 
        # shell=False, cwd = "/home/jaebak/Desktop/temp/rcrs-server/boot")
        time.sleep(8)
        self.agent = subprocess.Popen("./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo).split(),shell=False, cwd = "/home/jaebak/Desktop/temp/rcrs-grpc-demo") 
        # self.agent = subprocess.Popen(["xterm","-e","./launch.sh -all -s localhost:{} -r {} &".format(self.portNo, self.grpcNo)],shell=False, cwd = "/home/jaebak/Desktop/temp/rcrs-grpc-demo") 
        self.wait_request(30,0.1) ## wait until action request
        self.obs = self.connection.getObs(30,0.1)
        self.step2(self.buildingNo)
        self.step2(self.buildingNo)
        return self.obs

    def render(self):
        print("start reset")
        self.timeStamp = -1
        logName = self.mapName
        
        origin_map_path = '/home/jaebak/Desktop/temp/rcrs-server/maps/gml/EnvTest'
        map_path = '/home/jaebak/Desktop/temp/rcrs-server/maps/gml/' + logName
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
        logpath = os.path.join("/home/jaebak/Desktop/temp/rcrs-server/log",logName)
        mapfile = os.path.join("/home/jaebak/Desktop/temp/rcrs-server/maps/gml", logName)
        common_path = os.path.join(mapfile, 'config')
        mapfile = os.path.join(mapfile, 'map')
        self.kernel = subprocess.Popen(["xterm","-e","./start.sh -l {} -m {} -c {} -p {} -r {}".format(logpath,mapfile,common_path,self.portNo,self.grpcNo)], shell=False, cwd = "/home/jaebak/Desktop/temp/rcrs-server/boot")
        time.sleep(8)
        self.agent = subprocess.Popen(["xterm","-e","./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo)],shell=False, cwd = "/home/jaebak/Desktop/temp/rcrs-grpc-demo")
        self.wait_request(30,0.1) ## wait until action request
        self.obs = self.connection.getObs(30,0.1)
        self.step(self.buildingNo)
        self.step(self.buildingNo)
        return self.obs

    def serve(self,portNo):
        connection = SimpleConnection()
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
        isonfire = np.array(self.obs[2:-3:3])
        for i in isonfire:
            firenumber +=i
        self.reward = 150-firenumber
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

    def __init__(self):
        self.locker = 0
        self.timestamp = -1
        self.obs = {}
        self.action = None
        self.busy = 0
        self.busycheck = 0
        # self.buildingIdList = buildingIdList

    def SetActionType(self, request, context):
        if request.check == 1:
            print("Select Action of Police Force: ")
            time.sleep(0.5)
            print("Done")
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        if request.check == 2:
            print("Select Action of Fire Brigade: ")
            self.busy = 0
        if request.check == 5:
            print("Fire Brigade is busy")
            self.busy = 1
            self.busycheck = 1
            
        if request.check == 3:
            print("Select Action of Ambulance Team: ")
            time.sleep(0.5)
            print("Done")
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        # time.sleep(10)
        check = self.wait_action(30,0.1)
        if not check:
            print("no action input")
            exit()
        # print("get action successfully")
        templist = self.action.split(" ")
        self.obs = None ## need to control when obs, action should be None
        self.action = None
        if self.busy ==1 :
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        if self.busycheck==1:
            self.busycheck=0
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        self.busycheck=1
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
        # print("start generating obs")
        # print(request)
        # print("area printed")
        obs=[]
        for i in request.areas:
            if i.uRN=="Building" or i.uRN=="AmbulanceCentre" or i.uRN=="FireStation" or i.uRN=="PoliceOffice" or i.uRN=="GasStation" or i.uRN=="Refuge":
                if i.isOnFire:
                    obs.append(1)
                else:
                    obs.append(0)
                obs.append(i.temperature)
                obs.append(i.fieryness)
                # print("one of obs added")
        for i in request.humans:
            if i.uRN=="FireBrigade":
                obs.append(i.water)
                obs.append(i.x)
                obs.append(i.y)
                obs.append(self.busycheck)
        self.obs = obs
        print("generate obs successfully")
        return RCRS_pb2.ActionType(actionType=0, x=float(0), y=float(0))

    def SetMove(self, request, context):
        x = input("Input x: ")
        y = input("Input y: ")
        return RCRS_pb2.Move(x=float(x), y= float(y))
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
                print("wait_action : {}".format(self.action))
                return True
            time.sleep(period)
        print("action get error")
        exit()
        return False
    def getObs(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end:
            if self.obs:
                print("return obs at get obs : {}".format(self.obs))
                return self.obs
            time.sleep(period)
        print("obs get error")
        exit()
        return False
