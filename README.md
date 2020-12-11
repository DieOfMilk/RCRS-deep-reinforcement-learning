# RCRS-deep-reinforcement-learning
This project is multi-agent deep reinforcement learning project on RCRS(robocup-rescue-simulator).
Now, the reward of deep reinforcement learning is relatied to sum of fire level of map, thus model only learn how to control fire brigade agents. After change reward function, police force and ambulence team will be added.
Now, the agent id and agent number are constant value. You need to change these value if you change the agent setting. Later, I will modify the codes to get these information automatically. 


## 1. Prerequisites
* Git
* OpenJDK Java 11+
* Python 3.5+
* Openai Gym
* Stable Baselines
* Tensorflow 1.15 (Note: Stable baselines is not compatible with Tensorflow 2. If you want to use other deep learning models rather than stable baselines, you can use tensorflow 2)
```
sudo apt-get update && sudo apt-get install cmake libopenmpi-dev python3-dev zlib1g-dev
python3 -m pip install scikit-build cmake gym stable_baselines tensorflow==1.15 
```
## 2. Compile
First, you need to compile rcrs-server and rcrs-adf files. Open `rcrs-server` and compile
```
./gradlew clean
./gradlew completeBuild
```
Then, open `rcrs-grpc-demo` and compile
```
./gradlew clean
./gradlew installDist
```
Then, register RCRS environment as python package. Open `script/gym-RCRS` and compile
```
python3 -m pip install -e .
```
## 3. Structure
```
.
├── rcrs-grpc-demo            // RCRS agent part
│   ├── build.gradle         // gradle setting to build RCRS agent
│   ├── ... 
│   ├── ...
│   ├── src                  //source code
│   └── WORKSPACE
├── rcrs-server              // RCRS server part
│   ├── boot
│   ├── build.gradle         // gradle setting to build RCRS server
│   ├── ...
│   ├── modules              // source code
│   └── supplement
├── README.md
└── script
    ├── condor_scripts               //simple condor examples
    ├── gym-RCRS                     // gym environment part
    ├── log                          // autometically the save folder will generate
    ├── performance_test.py         // generate test data of each time step policy
    └── modify_PPO2_v2.py           // learning codes with PPO algorithm
```

## 4. How to run
You should run your codes on `script` folder as root terminal. 
You can test the codes with PPO model of stable baselines using `modify_PPO2.py`.
```
python3 modify_PPO2.py
```
There are three general inputs to run `modify_PPO2.py`.
```
portNo ## the port number of RCRS. It is used to connect agents and RCRS server. 
grpcNo ## the grpc port number. It is used to connect gym environment with RCRS server and agents.
mapName ## any map name what you want. Automatically folder with the name is generated.  
learningrate ## learning rate of model. Please input under 1.0 
```
After choice the map name, automatically forder is generated in `script/log` directory. 
In that directory, `data.pkl` and `model.zip` will be saved. `data.pkl` is training data and `model.zip` is saved model. 
If you want to see how simulator works, set `verbose=True` when you call env.
```
env = gym.make("RCRS-v0", portNo=portNo, grpcNo=grpcNo, buildingNo=36, maxTimeStamp=99,mapName=mapName,verbose=True) (line 182)
```
`script/compare_result.py` will compare 'model.zip' performance in the `script/log/RCRS_*` files, and print the result. Thus, you can find the best performance log using it.
After compare the result, please modify `script/performance_test.py` to see the running curve. You should change the range at line 48, `for j in range(50):`. After fix the range, run the `script/performance_test.py` and input log file name and gamma value of the log file. Then, it will run temperal policy per 50 episodes and generate `result.pkl` files.
You can check the `result.pkl` through `script/graph_*.py`.
`script/graph_reward.py` will show sum of reward per one episode(35 steps), `script/graph_fieryness.py` will show sum of fieryness of all buildings at the end of episodes, and `script/graph_fired_building.py` will show the number of remaining fired buildings at the end of episodes.


## 5. How to run on condor
You may need to use virtual environment to control your python envrionment in conor. Below is what should you do to use virtual envrionment in condor.
* Install miniconda on your condor account. (download from https://docs.conda.io/en/latest/miniconda.html)
* Make your own virtual environment `conda create -n 'name what you want (ex: condor)`
* call your virtual environment `conda activate 'name what you choose'` and install all python packages on that environment include gym-RCRS on `script/gym-RCRS`.

You can see the sample files at `script/condor_scripts/`. 
```
cpu_test.condor : condor script to run on cpu (If you want to remove gpu perfectly, use can add condition 'GPUs==0' or 'GPU == false'
cpu_test_nooutput.condor : This condor script doesn't generate output and error files. 
gpu_test.condor : condor script to run on GPU
start.sh : bash script to call modify_PPO2.py. *If should be same directory with modify_PPO2.py*
generate.py : simple script to generate input files for condor.
```

However, you should change the `Executable` path of condor files. I suggest you to put the `.condor` files outside of `script` folder. (eg, `parentDir/script` and `parentDir/~~.condor`
Additionally, we need to call virtual environment in bash scipt, thus you should change anaconda envrionment name on `script/condor_scripts/start.sh`
```
conda activate [name of envrionment] (ex: conda activate condor)
```

Also, you should add below path as envrionment path on `~/.bashrc` becasue some condor nodes can not find `bin` and `condor` setup.
```
export PATH=/lusr/opt/condor/bin${PATH:+:${PATH}}$
export PATH=/bin${PATH:+:${PATH}}$
```


Below is example sturucture on condor.
```
./
├── condor                                  // condor log, input output locations
├── cpu_test.condor                         // condor files
├── cpu_test_nooutput.condor                // This file doesn't generate output, err files. 
├── gpu_test.condor
├── RCRS-deep-reinforcement-learning        //github files
│   ├── rcrs-grpc-demo 
│   ├── rcrs-server
│   ├── README.md
│   └── script                              //start.sh should be on ./RCRS-deep-reinforcement-learing/script/

```

Before submit condor files, we should generate "in." files, which are input files for condor nodes. You can geneate example input files using `script/condor_scripts/generate.py`. Please generate "in." files and move it to `condor` folder.

## warning
* If you want to change some bash script, generate bash script, please set up `#!/bin/bash`, not `#!/bin/sh`. It may not works for RCRS.
* When your codes failed on condor, please change port numbers. Sometimes, port number duplication was happended. 
* Be sure that every nodes should use different map names with each others. If duplication happended, generated log files will be crashed and RCRS server will terminated. 
* Be careful that in this version, if the condor job is suspended over 5 mintues, it will terminate automatically even in the gpu test. Next version I will fix that limitation. 
* This model save temperal policy after every 50 episode. If you want to change this frequency, please modify `script/models/myPPO2.py` line 546, `if self.episode_num%50==0:`.
* Every temperal policy take some disk, and it may cause disk capacity problem if you don't have enough space. 
* Output and error files take much disk. Please run cpu_test_noputput.condor to run multi condor execution.


