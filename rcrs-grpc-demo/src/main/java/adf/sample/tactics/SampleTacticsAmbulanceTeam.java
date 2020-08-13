package adf.sample.tactics;

import adf.agent.action.Action;
import adf.agent.action.ambulance.ActionLoad;
import adf.agent.action.ambulance.ActionRescue;
import adf.agent.action.ambulance.ActionUnload;
import adf.agent.action.common.ActionMove;
import adf.agent.action.common.ActionRest;
import adf.agent.communication.MessageManager;
import adf.agent.communication.standard.bundle.centralized.CommandAmbulance;
import adf.agent.communication.standard.bundle.centralized.CommandScout;
import adf.agent.communication.standard.bundle.information.MessageAmbulanceTeam;
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
import adf.component.module.complex.HumanDetector;
import adf.component.module.complex.Search;
import adf.component.tactics.TacticsAmbulanceTeam;
import adf.sample.extaction.ActionTransport;
import adf.sample.tactics.utils.MessageTool;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.config.Config;
import rescuecore2.Constants;

import java.util.List;
import java.util.Objects;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;

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

public class SampleTacticsAmbulanceTeam extends TacticsAmbulanceTeam
{
    private HumanDetector humanDetector;
    private Search search;

    private ExtAction actionTransport;
    private ExtAction actionExtMove;

    private CommandExecutor<CommandAmbulance> commandExecutorAmbulance;
    private CommandExecutor<CommandScout> commandExecutorScout;

    private MessageTool messageTool;

    private CommunicationMessage recentCommand;
    private Boolean isVisualDebug;

    private SimpleConnectionGrpc.SimpleConnectionBlockingStub blockingStub;
    private static Logger logger;
    private FileHandler handler;

    @Override
    public void initialize(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, MessageManager messageManager, DevelopData developData)
    {
        logger = TestLogger.getLogger(agentInfo.me());
        messageManager.setChannelSubscriber(moduleManager.getChannelSubscriber("MessageManager.PlatoonChannelSubscriber", "adf.sample.module.comm.SampleChannelSubscriber"));
        messageManager.setMessageCoordinator(moduleManager.getMessageCoordinator("MessageManager.PlatoonMessageCoordinator", "adf.sample.module.comm.SampleMessageCoordinator"));

        worldInfo.indexClass(
                StandardEntityURN.CIVILIAN,
                StandardEntityURN.FIRE_BRIGADE,
                StandardEntityURN.POLICE_FORCE,
                StandardEntityURN.AMBULANCE_TEAM,
                StandardEntityURN.ROAD,
                StandardEntityURN.HYDRANT,
                StandardEntityURN.BUILDING,
                StandardEntityURN.REFUGE,
                StandardEntityURN.GAS_STATION,
                StandardEntityURN.AMBULANCE_CENTRE,
                StandardEntityURN.FIRE_STATION,
                StandardEntityURN.POLICE_OFFICE
        );
        // this.setgRPC(Integer.toString(config.getIntValue(Constants.GRPC_PORT_NUMBER_KEY)));

        this.messageTool = new MessageTool(scenarioInfo, developData);

        this.isVisualDebug = (scenarioInfo.isDebugMode()
                && moduleManager.getModuleConfig().getBooleanValue("VisualDebug", false));

        this.recentCommand = null;

        // init Algorithm Module & ExtAction

        switch (scenarioInfo.getMode())
        {
            case PRECOMPUTATION_PHASE:
            case PRECOMPUTED:
                this.humanDetector = moduleManager.getModule("TacticsAmbulanceTeam.HumanDetector", "adf.sample.module.complex.SampleHumanDetector");
                this.search = moduleManager.getModule("TacticsAmbulanceTeam.Search", "adf.sample.module.complex.SampleSearch");
                this.actionTransport = moduleManager.getExtAction("TacticsAmbulanceTeam.ActionTransport", "adf.sample.extaction.ActionTransport");
                this.actionExtMove = moduleManager.getExtAction("TacticsAmbulanceTeam.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.commandExecutorAmbulance = moduleManager.getCommandExecutor("TacticsAmbulanceTeam.CommandExecutorAmbulance", "adf.sample.centralized.CommandExecutorAmbulance");
                this.commandExecutorScout = moduleManager.getCommandExecutor("TacticsAmbulanceTeam.CommandExecutorScout", "adf.sample.centralized.CommandExecutorScout");
                break;
            case NON_PRECOMPUTE:
                this.humanDetector = moduleManager.getModule("TacticsAmbulanceTeam.HumanDetector", "adf.sample.module.complex.SampleHumanDetector");
                this.search = moduleManager.getModule("TacticsAmbulanceTeam.Search", "adf.sample.module.complex.SampleSearch");
                this.actionTransport = moduleManager.getExtAction("TacticsAmbulanceTeam.ActionTransport", "adf.sample.extaction.ActionTransport");
                this.actionExtMove = moduleManager.getExtAction("TacticsAmbulanceTeam.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.commandExecutorAmbulance = moduleManager.getCommandExecutor("TacticsAmbulanceTeam.CommandExecutorAmbulance", "adf.sample.centralized.CommandExecutorAmbulance");
                this.commandExecutorScout = moduleManager.getCommandExecutor("TacticsAmbulanceTeam.CommandExecutorScout", "adf.sample.centralized.CommandExecutorScout");
        }
        registerModule(this.humanDetector);
        registerModule(this.search);
        registerModule(this.actionTransport);
        registerModule(this.actionExtMove);
        registerModule(this.commandExecutorAmbulance);
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
        this.messageTool.reflectMessage(agentInfo, worldInfo, scenarioInfo, messageManager);
        this.messageTool.sendRequestMessages(agentInfo, worldInfo, scenarioInfo, messageManager);
        this.messageTool.sendInformationMessages(agentInfo, worldInfo, scenarioInfo, messageManager);

        modulesUpdateInfo(messageManager);

        if (isVisualDebug)
        {
            WorldViewLauncher.getInstance().showTimeStep(agentInfo, worldInfo, scenarioInfo);
        }
        AmbulanceTeam agent = (AmbulanceTeam) agentInfo.me();
        EntityID agentID = agentInfo.getID();
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
        for (CommunicationMessage message : messageManager.getReceivedMessageList(CommandAmbulance.class))
        {
            CommandAmbulance command = (CommandAmbulance) message;
            if (command.isToIDDefined() && Objects.requireNonNull(command.getToID()).getValue() == agentID.getValue())
            {
                this.recentCommand = command;
                this.commandExecutorAmbulance.setCommand(command);
            }
        } 
        // autonomous
        int busy = 0;
        EntityID target = this.humanDetector.calc().getTarget();
        Action action = this.actionTransport.setTarget(target).calc().getAction();
        AgentProto agentproto;
        if(busy==0){
            agentproto = AgentProto.newBuilder().setAgentType(3).setAgentID(agentID.getValue()).build();
        }
        else{
            agentproto = AgentProto.newBuilder().setAgentType(6).setAgentID(agentID.getValue()).build();
        }
        ActionType actionType = null;
        try {
            actionType = this.blockingStub.setActionType(agentproto);
        } catch (StatusRuntimeException e) {
        logger.warn("line 219");
        }
        logger.info("line 221 "+ Integer.toString(actionType.getActionType()));
        switch(actionType.getActionType()) {
            case 1:
                logger.info("Case move with target setting");
                ((ActionTransport)this.actionTransport).setTarget(new EntityID((int)actionType.getX()));
                action = ((ActionTransport)this.actionTransport).calc().getAction();
                break;
            case 2:
                logger.info("Case move");
                action = ((ActionTransport)this.actionTransport).calc().getAction();
                break;
            case 3:
                logger.info("Case clear");
                action = ((ActionTransport)this.actionTransport).myTrans().getAction();
                break;
            case 4:
                logger.info("Case take a rest");
                action = new ActionRest(); 
                break;
            default:
                logger.info("Case default");
                break;
        }
        if (action != null)
        {
            this.sendActionMessage(messageManager, agent, action);
            return action;
        }
        target = this.search.calc().getTarget();
        action = this.actionExtMove.setTarget(target).calc().getAction();
        if (action != null)
        {
            this.sendActionMessage(messageManager, agent, action);
            return action;
        }

        messageManager.addMessage(
                new MessageAmbulanceTeam(true, agent, MessageAmbulanceTeam.ACTION_REST, agent.getPosition())
        );
        return new ActionRest();
    }

    private void sendActionMessage(MessageManager messageManager, AmbulanceTeam ambulance, Action action)
    {
        Class<? extends Action> actionClass = action.getClass();
        int actionIndex = -1;
        EntityID target = null;
        if (actionClass == ActionMove.class)
        {
            actionIndex = MessageAmbulanceTeam.ACTION_MOVE;
            List<EntityID> path = ((ActionMove) action).getPath();
            if (path.size() > 0)
            {
                target = path.get(path.size() - 1);
            }
        }
        else if (actionClass == ActionRescue.class)
        {
            actionIndex = MessageAmbulanceTeam.ACTION_RESCUE;
            target = ((ActionRescue) action).getTarget();
        }
        else if (actionClass == ActionLoad.class)
        {
            actionIndex = MessageAmbulanceTeam.ACTION_LOAD;
            target = ((ActionLoad) action).getTarget();
        }
        else if (actionClass == ActionUnload.class)
        {
            actionIndex = MessageAmbulanceTeam.ACTION_UNLOAD;
            target = ambulance.getPosition();
        }
        else if (actionClass == ActionRest.class)
        {
            actionIndex = MessageAmbulanceTeam.ACTION_REST;
            target = ambulance.getPosition();
        }
        if (actionIndex != -1)
        {
            messageManager.addMessage(new MessageAmbulanceTeam(true, ambulance, actionIndex, target));
        }
    }
}
