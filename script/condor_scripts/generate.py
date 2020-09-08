import os
import sys

if __name__ == "__main__":
    # learning_rate = [1e-3,5e-4,1e-4,5e-5]
    # gamma = [0.99, 0.9, 0.8]
    # batch_size = [16,32,64]
    # Memory_size = [2000,6000,10000]
    # epsilon_decay = [0.99, 0.9, 0.8]
    # i=0
    # for lr in learning_rate:
    #     for gm in gamma:
    #         for bs in batch_size:
    #             for ms in Memory_size:
    #                 for ed in epsilon_decay:
    #                     path='./in.{}'.format(i)
    #                     with open(path,'w') as f:
    #                         f.write(str(i+50500)+'\n')
    #                         f.write(str(lr)+'\n')
    #                         f.write(str(gm)+'\n')
    #                         f.write(str(bs)+'\n')
    #                         f.write(str(ms)+'\n')
    #                         f.write(str(ed))
    #                         f.close()
    #                         i+=1
    learning_rate = [1e-2,9e-3,8e-3,7e-3,6e-3,5e-3,4e-3,3e-3,2e-3,1e-3,
    9e-4,8e-4,7e-4,6e-4,5e-4,4e-4,3e-4,2e-4,1e-4]
    gamma = [0.99, 0.95, 0.9]
    i=0
    for lr in learning_rate:
        for gm in gamma:
            path='./in.{}'.format(i)
            with open(path,'w') as f:
                f.write(str(i+7000)+'\n')
                f.write(str(i+40000)+'\n')
                f.write("RCRS"+str(i)+'\n')
                f.write(str(lr)+'\n')
                f.write(str(gm)+'\n')
                f.close()
                i+=1