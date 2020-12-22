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
import gc



class RCRSEnv(gym.Env):
    metadata={'render.modes':['human']}
    def __init__(self, portNo, grpcNo, maxTimeStamp, buildingNo,mapName, gamma,verbose=False): # AreaList, agentList, algorithm,
        self.actionNo=4
        self.timeStamp=-1
        self.total_step = -1
        self.mapName=mapName
        # self.BuildingList = BuildingList
        self.verbose = verbose
        self.gamma = gamma
        self.buildingIdList = [871,877,883,885,886,887,888,889,894,895,896,904,908,909,913,915,916,920,923,925,927,
928,1029,1039,1049,1060,1094,1104,1118,1128,1140,1151,1162,1173,1196,1201,1203,1208,1215,
1221,1222,1233,1280,1281,1294,1306,1307,1314,1319,1329,1330,1335,1346,1350,1353,1361,1363]#removed 906
        self.buildingNo = len(self.buildingIdList)
        self.maxTimeStamp = maxTimeStamp
        self.portNo = str(portNo)
        self.grpcNo = str(grpcNo)
        self.maxWater = 30000
        self.agentList = [1694383525,1762940799]
        self.action_space = spaces.Discrete(self.buildingNo+2)
        self.observation_space = spaces.Box(low=np.array([0]*(self.buildingNo*2+3*len(self.agentList))+[0]*(len(self.agentList)-1)).astype(int),high=np.array([1]*(self.buildingNo*2)+[1]*3*len(self.agentList)+[1]*(len(self.agentList)-1)).astype(int),dtype= np.int)
        self.obs= {}
         # removed 298
        seedNo=23
        self.np_random , seed = gym.utils.seeding.np_random(seedNo)
        self.kernel = None
        self.agent = None
        self.reward = [0,0]
        self.action = [None,None]
        self.closed = False
        self.server = None
        self.maxMapSize =200000
        

    def step(self):
        # self.timeStamp +=1
        self.total_step +=1
        # print("start step. Total step is : {}".format(self.total_step))
        self.done= False
        self.run_action()
        while True:
            self.connection.set_step_finished()
            self.wait_request(900,0.1)
            self.obs = self.connection.getObs(900,0.1)
            self.busy = self.connection.getBusy(900,0.1)
            if np.isin(2,self.busy) or self.timeStamp == self.maxTimeStamp:
                break
        # print(self.prevObs)
        self.reward[0] = self.gamma*self.getReward(self.obs) - self.getReward(self.prevObs1)
        self.reward[1] = self.gamma*self.getReward(self.obs) - self.getReward(self.prevObs2)
        if (self.prevObs1[-6]<5000 and self.obs[-6]>5000) or (self.prevObs1[-3]<5000 and self.obs[-3]>5000):
            self.reward[0] += 1
        if (self.prevObs2[-6]<5000 and self.obs[-6]>5000) or (self.prevObs2[-3]<5000 and self.obs[-3]>5000):
            self.reward[1] += 1
        if self.busy[0] == 2 and self.busy[1] == 2:
            self.idNumber =3
            self.action[0] = None
            self.action[1] = None
            self.prevObs1 = self.obs.copy()
            self.prevObs2 = self.obs.copy()
            self.obs=np.append(self.obs,self.busy[1])
        elif self.busy[0] == 2:
            self.idNumber = 1
            self.action[0] = None
            self.prevObs1 = self.obs.copy()
            self.obs=np.append(self.obs,self.busy[1])
        elif self.busy[1] == 2:
            self.idNumber = 2
            self.action[1] = None
            self.prevObs2 = self.obs.copy()
            self.obs=np.append(self.obs,self.busy[0])
        else: ## timestamp finished
            self.idNumber = 4  
            self.obs=np.append(self.obs,self.busy[1])
        # if self.idNumber == 2:
        #     self.obs = self.convertObs()
        if self.timeStamp>=self.maxTimeStamp:
            while self.idNumber != 3:
                self.input(self.buildingNo)
                self.run_action()
                self.connection.set_step_finished()
                self.wait_request(900,0.1)
                trashobs = self.connection.getObs(900,0.1)
                trashbusy = self.connection.getBusy(900,0.1)
                trashobs=np.append(trashobs,trashbusy[1])
                if (trashbusy[0] == 2 and trashbusy[1] == 2) or self.timeStamp==50: ## wait until agent finished but ignore the changed env after 35 time step.
                    self.idNumber = 3
                    break
            self.done=True
        info = {}
        firenumber = 0
        isonfire = np.array(self.obs[0:-(4*len(self.agentList)-1):2])
        for i in isonfire:
            if i==1:
                firenumber +=1
        info['fireNumber'] =  firenumber
        if self.done:
            info['finalFireNumber'] =  firenumber
            info['totalFieryness'] = self.getTotalFieryness()
            if firenumber==0:
                self.reward[0] +=25
                self.reward[1] +=25
            else:
                self.reward[0] -=firenumber
                self.reward[1] -=firenumber
        if firenumber==0: # about remaining fire
            info['is_success']=True   
        else:
            info['is_success']=False
        info['idNumber'] = self.idNumber
        final_obs = self.normalization(self.obs)
        returnReward = self.reward.copy()
        returnReward[0] = returnReward[0]/50
        returnReward[1] = returnReward[1]/50
        if self.idNumber == 3:
            self.reward = [0,0]
        elif self.idNumber == 1:
            self.reward[0] = 0
        elif self.idNumber == 2:
            self.reward[1] = 0
        return [final_obs], [returnReward], [self.done], [info]

    def getTotalFieryness(self):
        total_fieryness=0
        for i in range(self.buildingNo):
            if self.obs[2*i+1] < 5:
                total_fieryness += self.obs[2*i+1]
        return total_fieryness
    def run_action(self):
        action_list = []
        for i in self.action:
            if i == self.buildingNo:
                action_list.append("R")
            elif i == self.buildingNo+1:
                action_list.append("C")
            # elif i == self.buildingNo+2:
            #     action_list.append("C")
            elif i!=None:
                action_list.append("S "+ str(self.buildingIdList[i])) 
            else:
                print("run action error")
                exit(1) 
        self.connection.inputAction(action_list)

    def reset(self):
        self.action = [None,None]
        self.busy = [2,2]
        self.reward = [0,0]
        self.idNumber=3
        gc.collect()
        if self.kernel:
            self.kernel.terminate()
            self.kernel.wait()
        if self.agent:
            self.agent.terminate()
            self.agent.wait()
        if self.server:
            self.server.stop(5).wait()
            self.connection.setClose()
        self.server = None
        self.connection = None
        self.timeStamp = -1
        time.sleep(5)
        self.server, self.connection  = self.serve(self.grpcNo)
        time.sleep(5)
        logName = self.mapName
        
        origin_map_path = './../rcrs-server/maps/gml/bigTest3'
        map_path = './../rcrs-server/maps/gml/' + logName
        try:
            shutil.copytree(origin_map_path, map_path)
        except:
            pass
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
        time.sleep(15)
        self.connection.set_step_finished() 
        self.agent = subprocess.Popen("./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo).split(),shell=False, cwd = "./../rcrs-grpc-demo")
        # for _ in range(1):
            # for _ in range(20):
        x = self.wait_request(900,0.1) ## wait until action request
                # if x:
                #     break
                # self.agent.terminate()
                # self.agent.wait()
                # self.agent = subprocess.Popen("./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo).split(),shell=False, cwd = "./../rcrs-grpc-demo")
        self.obs = self.connection.getObs(900,0.1)
        self.prevObs1 = self.obs.copy()
        self.prevObs2 = self.obs.copy()
        self.busy = self.connection.getBusy(900,0.1)
        self.obs=np.append(self.obs,self.busy[0])
        self.input(self.buildingNo)
        self.input(self.buildingNo)
        self.step()
        self.input(self.buildingNo)
        self.input(self.buildingNo)
        self.step()
        return self.normalization(self.obs)
    def input(self,action):
        if self.action[0] == None:
            self.action[0] = action
            return True
        elif self.action[1] == None:
            self.action[1] = action
            return True
        else:
            print("No empty input")
            return False
    
    def wait_busy(self, timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end and not self.closed:
            if np.isin(2,self.connection.copyBusy(timeout, period)):
                return self.connection.copyBusy(timeout, period)
            time.sleep(period)
        if self.closed:
            exit(1)
        else:
            print("aget wait busy error")
            exit(1)

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
        time.sleep(15)
        self.agent = subprocess.Popen(["xterm","-e","./launch.sh -all -s localhost:{} -r {}".format(self.portNo, self.grpcNo)],shell=False, cwd = "./../rcrs-grpc-demo")
        self.connection.set_step_finished()
        self.wait_request(900,0.1) ## wait until action request
        self.obs = self.connection.getObs(900,0.1)
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
        while time.time() < must_end and not self.closed:
            if self.timeStamp+1 == self.connection.getTimeStamp():
                self.timeStamp = self.connection.getTimeStamp()
                return True
            time.sleep(period)
        if self.closed:
            return True
        else:
            print("No action request error with kernel {}, env {}".format(self.connection.getTimeStamp(), self.timeStamp))
            return False
    def getReward(self,obs):
        reward = 0
        for i in range(self.buildingNo):
            if obs[i*2+1]==4:
                reward += 11
            elif obs[i*2+1]==3:
                reward +=10
            elif obs[i*2+1]==2:
                reward+=8
            elif obs[i*2+1]==1:
                reward+=5
            else:
                reward+=1

        # isonfire = np.array(self.obs[0:-len(self.agentList)])
        # wasonfire = np.array(self.prevObs[0:-len(self.agentList)])  ## get the building information part
        # for i in range(self.buildingNo):
        #     if (wasonfire[i*2+1] < 5 and isonfire[i*2+1] >= 5) or wasonfire[i*2+1] > isonfire[i*2+1]:
        #         if isonfire[i*2+1] >=5 or isonfire[i*2+1]== 0:
        #             self.reward[0] +=4
        #             self.reward[1] +=4
        #         elif isonfire[i*2+1] ==1:
        #             self.reward[0] +=3
        #             self.reward[1] +=3
        #         elif isonfire[i*2+1]==2:
        #             self.reward[0] +=2
        #             self.reward[1] +=2
        #         elif isonfire[i*2+1]==3:
        #             self.reward[0] +=1
        #             self.reward[1] +=1
        #     elif (isonfire[i*2+1] < 5) and (wasonfire[i*2+1] < isonfire[i*2+1] or wasonfire[i*2+1] >= 5):
        #         if wasonfire[i*2+1]>=5 or wasonfire[i*2+1]==0:
        #             self.reward[0] -=4
        #             self.reward[1] -=4
        #         elif wasonfire[i*2+1]==1:
        #             self.reward[0] -=3
        #             self.reward[1] -=3
        #         elif wasonfire[i*2+1]==2:
        #             self.reward[0] -=2
        #             self.reward[1] -=2
        #         elif wasonfire[i*2+1]==3:
        #             self.reward[0] -=1
        #             self.reward[1] -=1
        # if (self.prevObs[-2]<5000 and self.obs[-2]>5000) or (self.prevObs[-1]<5000 and self.obs[-1]>5000):
        #       self.reward[0]+=1
        #       self.reward[1]+=1
        return -reward
    def getObs(self):
        return self.obs
    def convertObs(self):
        tempobs = self.obs[:-1]
        tempobs[-3:], tempobs[-6:-3] = tempobs[-6:-3],tempobs[-3:]
        tempobs = np.append(tempobs,self.busy[0])
        return self.normalization(tempobs)
    def killprocess(self):
        print("start kill process")
        self.closed = True
        if self.kernel:
            self.kernel.terminate()
            self.kernel.wait()
        if self.agent:
            self.agent.terminate()
            self.agent.wait()
        self.server.stop(0).wait()
        self.connection.setClose()
        self.connection = None
    def normalization(self, obs):
        final_obs = []
        for i in range(len(obs)):
            if i< 2*self.buildingNo:
                if i%2 == 0:
                    final_obs.append(obs[i])
                else:
                    final_obs.append(obs[i]/8)
            else:
                if (i-2*self.buildingNo)%3==0:
                    final_obs.append(obs[i]/self.maxWater)
                elif i==len(obs)-1:
                    final_obs.append(obs[i]-1)
                else:
                    final_obs.append(obs[i]/self.maxMapSize)
        return final_obs
            





class SimpleConnection(RCRS_pb2_grpc.SimpleConnectionServicer):

    def __init__(self,agentList,buildingIdList):
        self.locker = 0
        self.timestamp = -1
        self.obs = None
        self.action = [None] * len(agentList)
        self.busy = [0]*len(agentList)
        self.busycheck = np.array([0]*len(agentList))
        self.agentList = agentList
        self.buildingIdList = buildingIdList
        self.stepfinished = False
        self.closed = False
        
    def AskBusy(self, request, context):
        for i in range(len(self.agentList)):
            if request.AgentID == self.agentList[i]:
                self.busycheck[i] = 1
                self.busy[i] = request.Busy
                if self.busy[i] == 2:
                    self.action[i] = None
                break
        return RCRS_pb2.Check(check=1)

    def SetActionType(self, request, context):
        if request.AgentType == 1:
            time.sleep(0.5)
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        # if request.AgentType == 2:
        if request.AgentType == 3:
            time.sleep(0.5)
            return RCRS_pb2.ActionType(actionType= 4, x=float(0), y=float(0))
        # time.sleep(10)
        for i in range(len(self.agentList)):
            if request.AgentID == self.agentList[i]:
                idNumber = i
                if self.busy[i] == 1:
                    x="0"
                    y="0"
                    return RCRS_pb2.ActionType(actionType= 4, x=float(x), y=float(y))
        check = self.wait_action(900,0.1, idNumber)
        if not check:
            print("no action input")
            exit()
        templist = self.action[idNumber].split(" ")
        self.action[idNumber] = None
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
        check = self.wait_step_finish(900,0.1)
        if not check:
            print("previous step error")
            exit(1)
        self.request = request
        self.timestamp = request.time
        obs=np.zeros(2*len(self.buildingIdList),dtype=np.int)
        temp = np.zeros(3*len(self.agentList),dtype=np.int)
        for i in request.areas:
            if i.uRN=="Building" or i.uRN=="AmbulanceCentre" or i.uRN=="FireStation" or i.uRN=="PoliceOffice" or i.uRN=="GasStation" or i.uRN=="Refuge":
                for j in range(len(self.buildingIdList)):
                    if i.iD == self.buildingIdList[j]:
                        if i.isOnFire:
                            obs[2*j]=1
                        else:
                            obs[2*j]=0
                        obs[2*j+1]=i.fieryness
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
        return RCRS_pb2.ActionType(actionType=0, x=float(0), y=float(0))

    def getTimeStamp(self):
        return self.timestamp
    def inputAction(self, action):
        for i in range(len(self.action)):
            if self.action[i] == None:
                self.action[i] = action[i]
        return True
    def wait_action(self,timeout, period, idNumber):
        must_end = time.time() + timeout
        while time.time() < must_end and not self.closed:
            if self.action[idNumber] != None:
                return True
            time.sleep(period)
        if self.closed:
            exit(1)
            return True
        else:
            print("action get error")
            exit(1)
            return False
    def getObs(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end and not self.closed:
            if not (self.obs is None):
                obs = self.obs.copy()
                self.obs= None
                return obs
            time.sleep(period)
        if self.closed:
            exit(1)
            return True
        else:
            print("obs get error")
            exit()
            return False
    def getBusy(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end and not self.closed:
            if not np.isin(0,self.busycheck):
                busy = self.busy.copy()
                self.busycheck = np.zeros(len(self.agentList),dtype=np.int)
                return busy
            time.sleep(period)
        if self.closed:
            exit(1)
        else:
            print("busy get error")
            exit()
            return False
    def copyBusy(self,timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end and not self.closed:
            if not np.isin(0,self.busycheck):
                busy = self.busy.copy()
                return busy
            time.sleep(period)
        if self.closed:
            exit(1)
        else:
            print("busy copy error")
            exit()
            return False
    def wait_step_finish(self, timeout, period):
        must_end = time.time() + timeout
        while time.time() < must_end and not self.closed:
            if self.stepfinished:
                self.stepfinished = False
                return True
            time.sleep(period)
        if self.closed:
            exit(1)
        else:
            print("previous step didn't finsihed")
            exit()
            return False
    def set_step_finished(self):
        self.stepfinished = True
        return True
    def setClose(self):
        self.closed = True
        return True
