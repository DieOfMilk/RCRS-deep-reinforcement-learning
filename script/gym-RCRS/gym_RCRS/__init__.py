from gym.envs.registration import register

register(
    id='RCRS-v0',
    entry_point='gym_RCRS.envs:RCRSEnv',
    kwargs={'portNo':'7001', 'grpcNo':'50051', 'maxTimeStamp':10, 'buildingNo':37,'mapName':'EnvTest'}
)