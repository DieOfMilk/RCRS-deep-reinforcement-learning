package adf.sample.tactics;

import adf.agent.action.Action;
import adf.agent.action.common.ActionMove;
import adf.agent.action.common.ActionRest;
import adf.agent.action.police.ActionClear;
import adf.agent.communication.MessageManager;
import adf.agent.communication.standard.bundle.centralized.CommandPolice;
import adf.agent.communication.standard.bundle.centralized.CommandScout;
import adf.agent.communication.standard.bundle.information.MessagePoliceForce;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.agent.precompute.PrecomputeData;
import adf.debug.WorldViewLauncher;
import adf.component.centralized.CommandExecutor;
import adf.component.communication.CommunicationMessage;
import adf.component.extaction.ExtAction;
import adf.component.module.complex.RoadDetector;
import adf.component.module.complex.Search;
import adf.component.tactics.TacticsPoliceForce;
import adf.sample.tactics.utils.MessageTool;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.config.Config;
import rescuecore2.Constants;

import java.util.List;
import java.util.Objects;
import java.util.Collection;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import adf.debug.TestLogger;
import org.apache.log4j.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import adf.sample.extaction.ActionExtClear;
import adf.sample.module.complex.SampleRoadDetector;
import test_team.module.complex.self.TestRoadDetector;
import java.lang.Thread;


public class SampleTacticsPoliceForce extends TacticsPoliceForce
{
    private int clearDistance;

    private RoadDetector roadDetector;
    private Search search;

    private ExtAction actionExtClear;
    private ExtAction actionExtMove;

    private CommandExecutor<CommandPolice> commandExecutorPolice;
    private CommandExecutor<CommandScout> commandExecutorScout;

    private MessageTool messageTool;

    private CommunicationMessage recentCommand;

	private Boolean isVisualDebug;

//	private WorldViewer worldViewer;

    private SimpleConnectionGrpc.SimpleConnectionBlockingStub blockingStub;
    private static Logger logger;
    private FileHandler handler;
    

    @Override
    public void initialize(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, MessageManager messageManager, DevelopData developData)
    {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        

        for(StackTraceElement e:stacktrace) {
            String methodName = e.getClassName() + " "+ e.getMethodName();
        }
        
        logger = TestLogger.getLogger(agentInfo.me());

        messageManager.setChannelSubscriber(moduleManager.getChannelSubscriber("MessageManager.PlatoonChannelSubscriber", "adf.sample.module.comm.SampleChannelSubscriber"));
        messageManager.setMessageCoordinator(moduleManager.getMessageCoordinator("MessageManager.PlatoonMessageCoordinator", "adf.sample.module.comm.SampleMessageCoordinator"));

        worldInfo.indexClass(
                StandardEntityURN.ROAD,
                StandardEntityURN.HYDRANT,
                StandardEntityURN.BUILDING,
                StandardEntityURN.REFUGE,
                StandardEntityURN.BLOCKADE
        );
        // this.setgRPC(Integer.toString(config.getIntValue(Constants.GRPC_PORT_NUMBER_KEY)));

        this.messageTool = new MessageTool(scenarioInfo, developData);

        this.isVisualDebug = (scenarioInfo.isDebugMode()
                && moduleManager.getModuleConfig().getBooleanValue("VisualDebug", false));
        // init value
        this.clearDistance = scenarioInfo.getClearRepairDistance();
        this.recentCommand = null;
        // init Algorithm Module & ExtAction
        
        switch  (scenarioInfo.getMode())
        {
            case PRECOMPUTATION_PHASE:
            case PRECOMPUTED:
                this.search = moduleManager.getModule("TacticsPoliceForce.Search", "adf.sample.module.complex.SampleSearch");
                this.roadDetector = moduleManager.getModule("TacticsPoliceForce.RoadDetector", "adf.sample.module.complex.SampleRoadDetector");
                this.actionExtClear = moduleManager.getExtAction("TacticsPoliceForce.ActionExtClear", "adf.sample.extaction.ActionExtClear");
                this.actionExtMove = moduleManager.getExtAction("TacticsPoliceForce.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.commandExecutorPolice = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorPolice", "adf.sample.centralized.CommandExecutorPolice");
                this.commandExecutorScout = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorScout", "adf.sample.centralized.CommandExecutorScoutPolice");
                break;
            case NON_PRECOMPUTE:
                this.search = moduleManager.getModule("TacticsPoliceForce.Search", "adf.sample.module.complex.SampleSearch");
                this.roadDetector = moduleManager.getModule("TacticsPoliceForce.RoadDetector", "adf.sample.module.complex.SampleRoadDetector");
                this.actionExtClear = moduleManager.getExtAction("TacticsPoliceForce.ActionExtClear", "adf.sample.extaction.ActionExtClear");
                this.actionExtMove = moduleManager.getExtAction("TacticsPoliceForce.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.commandExecutorPolice = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorPolice", "adf.sample.centralized.CommandExecutorPolice");
                this.commandExecutorScout = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorScout", "adf.sample.centralized.CommandExecutorScoutPolice");
                break;
        }
        registerModule(this.search);
        registerModule(this.roadDetector);
        registerModule(this.actionExtClear);
        registerModule(this.actionExtMove);
        registerModule(this.commandExecutorPolice);
        registerModule(this.commandExecutorScout);
    }

    @Override
    public void setgRPC(String gRPCNo){
        String user = "3";
        // Access a service running on the local machine on port 50051
        String target = "localhost:" + gRPCNo;
        // String target = "localhost:50052";
        // Allow passing in the user and target strings as command line arguments
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        .usePlaintext()
        .build();
        this.blockingStub = SimpleConnectionGrpc.newBlockingStub(channel);
    }

    @Override
    public void precompute(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, PrecomputeData precomputeData, DevelopData developData)
    {
        modulesPrecompute(precomputeData);
    }

    @Override
    public void resume(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, PrecomputeData precomputeData, DevelopData developData)
    {
        modulesResume(precomputeData);

        if (isVisualDebug)
        {
            WorldViewLauncher.getInstance().showTimeStep(agentInfo, worldInfo, scenarioInfo);
        }
    }

    @Override
    public void preparate(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, DevelopData developData)
    {
        modulesPreparate();

        if (isVisualDebug)
        {
            WorldViewLauncher.getInstance().showTimeStep(agentInfo, worldInfo, scenarioInfo);
        }
    }

    @Override
    public Action think(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, MessageManager messageManager, DevelopData developData)
    {
        logger.info("Start thinking");
        this.messageTool.reflectMessage(agentInfo, worldInfo, scenarioInfo, messageManager);
        this.messageTool.sendRequestMessages(agentInfo, worldInfo, scenarioInfo, messageManager);
        this.messageTool.sendInformationMessages(agentInfo, worldInfo, scenarioInfo, messageManager);

        modulesUpdateInfo(messageManager);

        if (isVisualDebug)
        {
            WorldViewLauncher.getInstance().showTimeStep(agentInfo, worldInfo, scenarioInfo);
        }
        PoliceForce agent = (PoliceForce) agentInfo.me();
        EntityID agentID = agent.getID();
        // command
        for (CommunicationMessage message : messageManager.getReceivedMessageList(CommandScout.class))
        {
            CommandScout command = (CommandScout) message;
            if (command.isToIDDefined() && Objects.requireNonNull(command.getToID()).getValue() == agentID.getValue())
            {
                this.recentCommand = command;
                this.commandExecutorScout.setCommand(command);
            }
        }
        for (CommunicationMessage message : messageManager.getReceivedMessageList(CommandPolice.class))
        {
            CommandPolice command = (CommandPolice) message;
            if (command.isToIDDefined() && Objects.requireNonNull(command.getToID()).getValue() == agentID.getValue())
            {
                this.recentCommand = command;
                this.commandExecutorPolice.setCommand(command);
            }
        }
//        worldViewer.showTimestep(0);
        
        // autonomous
        int busy = 0;
        EntityID target = this.roadDetector.calc().getTarget();
        Action action = null;
        AgentProto agentproto;
        // ((ActionFireFighting)this.actionExtClear).setTarget(previousTarget);
        // if(((ActionFireFighting)this.actionExtClear).isBusy()){
        //     busy = 1;
        // }
        // else{
        //     busy = 0;
        // }
        if(busy==0){
            agentproto = AgentProto.newBuilder().setAgentType(1).setAgentID(agentID.getValue()).build();
        }
        else{
            agentproto = AgentProto.newBuilder().setAgentType(4).setAgentID(agentID.getValue()).build();
        }
        ActionType actionType = null;
        try {
            actionType = this.blockingStub.setActionType(agentproto);
        } catch (StatusRuntimeException e) {
        logger.warn("line 246");
        }
        logger.info("line 251 "+ Integer.toString(actionType.getActionType()));
        if (busy==0){
            switch(actionType.getActionType()) {
                case 1:
                    logger.info("Case move with target setting");
                    ((ActionExtClear)this.actionExtClear).setTarget(new EntityID((int)actionType.getX()));
                    action = ((ActionExtClear)this.actionExtClear).myMove().getAction();
                    break;
                case 2:
                    logger.info("Case move");
                    action = ((ActionExtClear)this.actionExtClear).myMove().getAction();
                    break;
                case 3:
                    logger.info("Case clear");
                    action = ((ActionExtClear)this.actionExtClear).myClear().getAction();
                    break;
                case 4:
                    logger.info("Case take a rest");
                    action = new ActionRest(); 
                    break;
                default:
                    logger.info("Case default");
                    break;
            }
        }
        if (action != null)
        {
            this.sendActionMessage(worldInfo, messageManager, agent, action);
            return action;
        }

        target = this.search.calc().getTarget();
        action = this.actionExtClear.setTarget(target).calc().getAction();
        if(action != null)
        {
            this.sendActionMessage(worldInfo, messageManager, agent, action);
            return action;
        }

        messageManager.addMessage(
                new MessagePoliceForce(true, agent, MessagePoliceForce.ACTION_REST, agent.getPosition())
        );
        return new ActionRest();
    }

    private void sendActionMessage(WorldInfo worldInfo, MessageManager messageManager, PoliceForce policeForce, Action action)
    {
        Class<? extends Action> actionClass = action.getClass();
        int actionIndex = -1;
        EntityID target = null;
        if (actionClass == ActionMove.class)
        {
            List<EntityID> path = ((ActionMove)action).getPath();
            actionIndex = MessagePoliceForce.ACTION_MOVE;
            if (path.size() > 0)
            {
                target = path.get(path.size() - 1);
            }
        }
        else if (actionClass == ActionClear.class)
        {
            actionIndex = MessagePoliceForce.ACTION_CLEAR;
            ActionClear ac = (ActionClear)action;
            target = ac.getTarget();
            if (target == null)
            {
                for (StandardEntity entity : worldInfo.getObjectsInRange(ac.getPosX(), ac.getPosY(), this.clearDistance))
                {
                    if (entity.getStandardURN() == StandardEntityURN.BLOCKADE)
                    {
                        target = entity.getID();
                        break;
                    }
                }
            }
        }
        else if (actionClass == ActionRest.class)
        {
            actionIndex = MessagePoliceForce.ACTION_REST;
            target = policeForce.getPosition();
        }

        if (actionIndex != -1)
        {
            messageManager.addMessage(new MessagePoliceForce(true, policeForce, actionIndex, target));
        }
    }

}
