package adf.sample.tactics;

import adf.agent.action.Action;
import adf.agent.action.common.ActionMove;
import adf.agent.action.common.ActionRest;
import adf.agent.communication.MessageManager;
import adf.agent.communication.standard.bundle.centralized.CommandScout;
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
import adf.component.module.complex.Search;
import adf.sample.tactics.utils.MessageTool;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.config.Config;
import rescuecore2.Constants;

import java.util.List;
import java.util.Objects;

import adf.agent.action.fire.ActionExtinguish;
import adf.agent.action.fire.ActionRefill;
import adf.agent.communication.standard.bundle.centralized.CommandFire;
import adf.agent.communication.standard.bundle.information.MessageFireBrigade;
import adf.component.module.complex.BuildingDetector;
import adf.component.tactics.TacticsFireBrigade;

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

import adf.sample.extaction.ActionFireFighting;
import adf.sample.module.complex.SampleRoadDetector;
import test_team.module.complex.self.TestRoadDetector;




public class SampleTacticsFireBrigade extends TacticsFireBrigade
{
    private BuildingDetector buildingDetector;
    private Search search;

    private ExtAction actionFireFighting;
    private ExtAction actionExtMove;

    private CommandExecutor<CommandFire> commandExecutorFire;
    private CommandExecutor<CommandScout> commandExecutorScout;

    private MessageTool messageTool;

    private CommunicationMessage recentCommand;

	private Boolean isVisualDebug;

    private SimpleConnectionGrpc.SimpleConnectionBlockingStub blockingStub;
    private static Logger logger;
    private FileHandler handler;

    private static int[] previousList = {1694383525,0,0,1762940799,0,0};
    // private static int[] previousList = {1962675462,0,0,210552869,0,0};
    private int agentNo;

    @Override
    public void initialize(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, MessageManager messageManager, DevelopData developData)
    {
        logger = TestLogger.getLogger(agentInfo.me());
        messageManager.setChannelSubscriber(moduleManager.getChannelSubscriber("MessageManager.PlatoonChannelSubscriber", "adf.sample.module.comm.SampleChannelSubscriber"));
        messageManager.setMessageCoordinator(moduleManager.getMessageCoordinator("MessageManager.PlatoonMessageCoordinator", "adf.sample.module.comm.SampleMessageCoordinator"));
        worldInfo.indexClass(
                StandardEntityURN.ROAD,
                StandardEntityURN.HYDRANT,
                StandardEntityURN.BUILDING,
                StandardEntityURN.REFUGE,
                StandardEntityURN.GAS_STATION,
                StandardEntityURN.AMBULANCE_CENTRE,
                StandardEntityURN.FIRE_STATION,
                StandardEntityURN.POLICE_OFFICE
        );
        agentNo=6;
        // this.setgRPC(Integer.toString(config.getIntValue(Constants.GRPC_PORT_NUMBER_KEY)));

        this.messageTool = new MessageTool(scenarioInfo, developData);

        this.isVisualDebug = (scenarioInfo.isDebugMode()
                            && moduleManager.getModuleConfig().getBooleanValue("VisualDebug", false));

        this.recentCommand = null;
        // init Algorithm Module & ExtAction

        

        switch  (scenarioInfo.getMode())
        {
            case PRECOMPUTATION_PHASE:
            case PRECOMPUTED:
                this.search = moduleManager.getModule("TacticsFireBrigade.Search", "adf.sample.module.complex.SampleSearch");
                this.buildingDetector = moduleManager.getModule("TacticsFireBrigade.BuildingDetector", "adf.sample.module.complex.SampleBuildingDetector");
                this.actionFireFighting = moduleManager.getExtAction("TacticsFireBrigade.ActionFireFighting", "adf.sample.extaction.ActionFireFighting");
                this.actionExtMove = moduleManager.getExtAction("TacticsFireBrigade.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.commandExecutorFire = moduleManager.getCommandExecutor("TacticsFireBrigade.CommandExecutorFire", "adf.sample.centralized.CommandExecutorFire");
                this.commandExecutorScout = moduleManager.getCommandExecutor("TacticsFireBrigade.CommandExecutorScout", "adf.sample.centralized.CommandExecutorScout");
                break;
            case NON_PRECOMPUTE:
                this.search = moduleManager.getModule("TacticsFireBrigade.Search", "adf.sample.module.complex.SampleSearch");
                this.buildingDetector = moduleManager.getModule("TacticsFireBrigade.BuildingDetector", "adf.sample.module.complex.SampleBuildingDetector");
                this.actionFireFighting = moduleManager.getExtAction("TacticsFireBrigade.ActionFireFighting", "adf.sample.extaction.ActionFireFighting");
                this.actionExtMove = moduleManager.getExtAction("TacticsFireBrigade.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.commandExecutorFire = moduleManager.getCommandExecutor("TacticsFireBrigade.CommandExecutorFire", "adf.sample.centralized.CommandExecutorFire");
                this.commandExecutorScout = moduleManager.getCommandExecutor("TacticsFireBrigade.CommandExecutorScout", "adf.sample.centralized.CommandExecutorScout");
                break;
        }

        registerModule(this.buildingDetector);
        registerModule(this.search);
        registerModule(this.actionFireFighting);
        registerModule(this.actionExtMove);
        registerModule(this.commandExecutorFire);
        registerModule(this.commandExecutorScout);
    }

    @Override
    public void setgRPC(String gRPCNo){
        String user = "10";
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
        FireBrigade agent = (FireBrigade) agentInfo.me();
        EntityID agentID = agentInfo.getID();
        
        // command
        
        // autonomous
        EntityID previousTarget = null;
        int previousAction = -1;
        int previousLoc = -1;
        for (int i=0;i<agentNo;i++) {
            if (previousList[i] == agentID.getValue()) {
                previousLoc = i;
                previousTarget = new EntityID(previousList[i+1]);
                previousAction = previousList[i+2];
            }
        }
        
        int busy;
        EntityID target;
        Action action;
        AgentProto agentproto;
        this.buildingDetector.calc();
        // this.search.calc();
        ((ActionFireFighting)this.actionFireFighting).setTarget(previousTarget);
        if(((ActionFireFighting)this.actionFireFighting).isBusy()){
            busy = 1;
        }
        else{
            busy = 2;
        }
        Check check;
        BusyProto busyproto = BusyProto.newBuilder().setAgentID(agentID.getValue()).setBusy(busy).build();
        while(true){
            try {
                // System.out.println(busyproto.getAgentID());
                check = this.blockingStub.withDeadlineAfter(60, TimeUnit.SECONDS).askBusy(busyproto);
                if(check.getCheck()==1){
                    break;
                }
                // System.out.println(check.getCheck());
            } catch (StatusRuntimeException e) {
                System.out.println("line 269 busy ask error");
                System.out.println(e.getMessage());
            }
        }
        agentproto = AgentProto.newBuilder().setAgentType(2).setAgentID(agentID.getValue()).build();
        ActionType actionType = null;
        while(true){
            try {
                actionType = this.blockingStub.withDeadlineAfter(60, TimeUnit.SECONDS).setActionType(agentproto);
                if (actionType.getActionType()>0) {
                    break;
                }
            } catch (StatusRuntimeException e) {
                System.out.println("line 282 setactiontype error");
                break;
            }
        }
        
        if (busy==2){
            switch(actionType.getActionType()) {
                case 1:
                    System.out.println("Case move with target setting");
                    previousTarget = new EntityID((int)actionType.getX());
                    ((ActionFireFighting)this.actionFireFighting).setTarget(new EntityID((int)actionType.getX()));
                    action = ((ActionFireFighting)this.actionFireFighting).mycalc().getAction();
                    previousAction = 1;
                    break;
                case 2:
                    System.out.println("Case move");
                    ((ActionFireFighting)this.actionFireFighting).setTarget(previousTarget);
                    action = ((ActionFireFighting)this.actionFireFighting).mycalc().getAction();
                    previousAction = 2;
                    break;
                case 3:
                    System.out.println("Case water refill");
                    action = ((ActionFireFighting)this.actionFireFighting).callRefill().getAction();
                    previousAction = 3;
                    break;
                case 4:
                    System.out.println("Case take a rest");
                    action = new ActionRest(); 
                    previousAction = 4;
                    break;
                default:
                    System.out.println("Case default");
                    action = new ActionRest();
                    break;
            }
            previousList[previousLoc+1] = (int)actionType.getX();
            previousList[previousLoc+2] = previousAction;
        }
        else {
            switch(previousAction) {
                case 1:
                    System.out.println("Case move with busy");
                    ((ActionFireFighting)this.actionFireFighting).setTarget(previousTarget);
                    action = ((ActionFireFighting)this.actionFireFighting).mycalc().getAction();
                    break;
                case 2:
                    System.out.println("Case move with busy");
                    ((ActionFireFighting)this.actionFireFighting).setTarget(previousTarget);
                    action = ((ActionFireFighting)this.actionFireFighting).mycalc().getAction();
                    break;
                case 3:
                    System.out.println("Case water refill with busy");
                    action = ((ActionFireFighting)this.actionFireFighting).callRefill().getAction();
                    break;
                case 4:
                    System.out.println("Case take a rest with busy");
                    action = new ActionRest(); 
                    break;
                default:
                    System.out.println("Case default with busy");
                    action = new ActionRest();
                    break;
            }
        }
        if (action != null)
        {
            this.sendActionMessage(messageManager, agent, action);
            return action;
        }
        System.out.println("Why there is no action result?");
        target = this.buildingDetector.calc().getTarget();
        action = this.actionExtMove.setTarget(target).calc().getAction();
        if (action != null)
        {
            this.sendActionMessage(messageManager, agent, action);
            return action;
        }
        messageManager.addMessage(
                new MessageFireBrigade(true, agent, MessageFireBrigade.ACTION_REST,  agent.getPosition())
        );
        return new ActionRest();
    }

    private void sendActionMessage(MessageManager messageManager, FireBrigade agent, Action action)
    {
        Class<? extends Action> actionClass = action.getClass();
        int actionIndex = -1;
        EntityID target = null;
        if (actionClass == ActionMove.class)
        {
            actionIndex = MessageFireBrigade.ACTION_MOVE;
            List<EntityID> path = ((ActionMove) action).getPath();
            if (path.size() > 0)
            {
                target = path.get(path.size() - 1);
            }
        }
        else if (actionClass == ActionExtinguish.class)
        {
            actionIndex = MessageFireBrigade.ACTION_EXTINGUISH;
            target = ((ActionExtinguish)action).getTarget();
        }
        else if (actionClass == ActionRefill.class)
        {
            actionIndex = MessageFireBrigade.ACTION_REFILL;
            target = agent.getPosition();
        }
        else if (actionClass == ActionRest.class)
        {
            actionIndex = MessageFireBrigade.ACTION_REST;
            target = agent.getPosition();
        }
        if (target == null)
        {target = agent.getPosition();}
        if (actionIndex != -1)
        {
            // System.out.println(target.getValue());
            messageManager.addMessage(new MessageFireBrigade(true, agent, actionIndex, target));
        }
    }
}
