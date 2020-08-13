package kernel;


import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;

import rescuecore2.config.Config;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.WorldModel;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.messages.Command;
import rescuecore2.Constants;
import rescuecore2.Timestep;
import rescuecore2.score.ScoreFunction;
import rescuecore2.standard.entities.*;
//import rescuecore2.misc.gui.ChangeSetComponent;

import rescuecore2.log.LogWriter;
import rescuecore2.log.FileLogWriter;
import rescuecore2.log.InitialConditionsRecord;
import rescuecore2.log.StartLogRecord;
import rescuecore2.log.EndLogRecord;
import rescuecore2.log.ConfigRecord;
import rescuecore2.log.PerceptionRecord;
import rescuecore2.log.CommandsRecord;
import rescuecore2.log.UpdatesRecord;
import rescuecore2.log.LogException;
import rescuecore2.log.Logger;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
/**
   The Robocup Rescue kernel.
 */
public class Kernel {
    /** The log context for kernel log messages. */
    public static final String KERNEL_LOG_CONTEXT = "kernel";

    private Config config;
    private Perception perception;
    private CommunicationModel communicationModel;
    private WorldModel<? extends Entity> worldModel;
    private LogWriter log;

    private Set<KernelListener> listeners;

    private Collection<AgentProxy> agents;
    private Collection<SimulatorProxy> sims;
    private Collection<ViewerProxy> viewers;
    private int time;
    private Timestep previousTimestep;

    private EntityIDGenerator idGenerator;
    private CommandFilter commandFilter;

    private TerminationCondition termination;
    private ScoreFunction score;
    private CommandCollector commandCollector;

    private boolean isShutdown;

    private WorldModel<? extends Entity> firstWorldModel;

    private SimpleConnectionGrpc.SimpleConnectionBlockingStub blockingStub;

    //    private ChangeSetComponent simulatorChanges;

    /**
       Construct a kernel.
       @param config The configuration to use.
       @param perception A perception calculator.
       @param communicationModel A communication model.
       @param worldModel The world model.
       @param idGenerator An EntityIDGenerator.
       @param commandFilter An optional command filter. This may be null.
       @param termination The termination condition.
       @param score The score function.
       @param collector The CommandCollector to use.
       @throws KernelException If there is a problem constructing the kernel.
    */
    public Kernel(Config config,
                  Perception perception,
                  CommunicationModel communicationModel,
                  WorldModel<? extends Entity> worldModel,
                  EntityIDGenerator idGenerator,
                  CommandFilter commandFilter,
                  TerminationCondition termination,
                  ScoreFunction score,
                  CommandCollector collector) throws KernelException {
        try {
            String myport = "localhost:" + Integer.toString(config.getIntValue(Constants.GRPC_PORT_NUMBER_KEY));
            ManagedChannel channel = ManagedChannelBuilder.forTarget(myport)
            .usePlaintext()
            .build();
            blockingStub = SimpleConnectionGrpc.newBlockingStub(channel);

            Logger.pushLogContext(KERNEL_LOG_CONTEXT);
            this.config = config;
            this.perception = perception;
            this.communicationModel = communicationModel;
            this.worldModel = worldModel;
            this.commandFilter = commandFilter;
            this.score = score;
            this.termination = termination;
            this.commandCollector = collector;
            this.idGenerator = idGenerator;
            listeners = new HashSet<KernelListener>();
            agents = new HashSet<AgentProxy>();
            sims = new HashSet<SimulatorProxy>();
            viewers = new HashSet<ViewerProxy>();
            time = 0;
            try {
                String logName = config.getValue("kernel.logname");
                Logger.info("Logging to " + logName);
                File logFile = new File(logName);
                if (logFile.getParentFile().mkdirs()) {
                    Logger.info("Created log directory: " + logFile.getParentFile().getAbsolutePath());
                }
                if (logFile.createNewFile()) {
                    Logger.info("Created log file: " + logFile.getAbsolutePath());
                }
                log = new FileLogWriter(logFile);
                log.writeRecord(new StartLogRecord());
                log.writeRecord(new InitialConditionsRecord(worldModel));
                log.writeRecord(new ConfigRecord(config));
            }
            catch (IOException e) {
                throw new KernelException("Couldn't open log file for writing", e);
            }
            catch (LogException e) {
                throw new KernelException("Couldn't open log file for writing", e);
            }
            config.setValue(Constants.COMMUNICATION_MODEL_KEY, communicationModel.getClass().getName());
            config.setValue(Constants.PERCEPTION_KEY, perception.getClass().getName());

            //            simulatorChanges = new ChangeSetComponent();

            // Initialise
            perception.initialise(config, worldModel);
            communicationModel.initialise(config, worldModel);
            commandFilter.initialise(config);
            score.initialise(worldModel, config);
            termination.initialise(config);
            commandCollector.initialise(config);

            isShutdown = false;

            Logger.info("Kernel initialised");
            Logger.info("Perception module: " + perception);
            Logger.info("Communication module: " + communicationModel);
            Logger.info("Command filter: " + commandFilter);
            Logger.info("Score function: " + score);
            Logger.info("Termination condition: " + termination);
            Logger.info("Command collector: " + collector);
        }
        finally {
            Logger.popLogContext();
        }
    }

    /**
       Get the kernel's configuration.
       @return The configuration.
    */
    public Config getConfig() {
        return config;
    }

    /**
       Get a snapshot of the kernel's state.
       @return A new KernelState snapshot.
    */
    public KernelState getState() {
        return new KernelState(getTime(), getWorldModel());
    }

    /**
       Add an agent to the system.
       @param agent The agent to add.
    */
    public void addAgent(AgentProxy agent) {
        synchronized (this) {
            agents.add(agent);
        }
        fireAgentAdded(agent);
    }

    /**
       Remove an agent from the system.
       @param agent The agent to remove.
    */
    public void removeAgent(AgentProxy agent) {
        synchronized (this) {
            agents.remove(agent);
        }
        fireAgentRemoved(agent);
    }

    /**
       Get all agents in the system.
       @return An unmodifiable view of all agents.
    */
    public Collection<AgentProxy> getAllAgents() {
        synchronized (this) {
            return Collections.unmodifiableCollection(agents);
        }
    }

    /**
       Add a simulator to the system.
       @param sim The simulator to add.
    */
    public void addSimulator(SimulatorProxy sim) {
        synchronized (this) {
            sims.add(sim);
            sim.setEntityIDGenerator(idGenerator);
        }
        fireSimulatorAdded(sim);
    }

    /**
       Remove a simulator from the system.
       @param sim The simulator to remove.
    */
    public void removeSimulator(SimulatorProxy sim) {
        synchronized (this) {
            sims.remove(sim);
        }
        fireSimulatorRemoved(sim);
    }

    /**
       Get all simulators in the system.
       @return An unmodifiable view of all simulators.
    */
    public Collection<SimulatorProxy> getAllSimulators() {
        synchronized (this) {
            return Collections.unmodifiableCollection(sims);
        }
    }

    /**
       Add a viewer to the system.
       @param viewer The viewer to add.
    */
    public void addViewer(ViewerProxy viewer) {
        synchronized (this) {
            viewers.add(viewer);
        }
        fireViewerAdded(viewer);
    }

    /**
       Remove a viewer from the system.
       @param viewer The viewer to remove.
    */
    public void removeViewer(ViewerProxy viewer) {
        synchronized (this) {
            viewers.remove(viewer);
        }
        fireViewerRemoved(viewer);
    }

    /**
       Get all viewers in the system.
       @return An unmodifiable view of all viewers.
    */
    public Collection<ViewerProxy> getAllViewers() {
        synchronized (this) {
            return Collections.unmodifiableCollection(viewers);
        }
    }

    /**
       Add a KernelListener.
       @param l The listener to add.
    */
    public void addKernelListener(KernelListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
       Remove a KernelListener.
       @param l The listener to remove.
    */
    public void removeKernelListener(KernelListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    /**
       Get the current time.
       @return The current time.
    */
    public int getTime() {
        synchronized (this) {
            return time;
        }
    }

    /**
       Get the world model.
       @return The world model.
    */
    public WorldModel<? extends Entity> getWorldModel() {
        return worldModel;
    }

    /**
       Find out if the kernel has terminated.
       @return True if the kernel has terminated, false otherwise.
    */
    public boolean hasTerminated() {
        synchronized (this) {
            return isShutdown || termination.shouldStop(getState());
        }
    }

    /**
       Run a single timestep.
       @throws InterruptedException If this thread is interrupted during the timestep.
       @throws KernelException If there is a problem executing the timestep.
       @throws LogException If there is a problem writing the log.
    */
    public void timestep() throws InterruptedException, KernelException, LogException {
        System.out.println("start timestep");
        System.out.println("Now the timestep is"+time);
        try {
            Logger.pushLogContext(KERNEL_LOG_CONTEXT);
            synchronized (this) {
                while(true) {
                    try{
                        //bilding -> Area, road-> Area, 
                        Collection<? extends Entity> allEntity = this.worldModel.getAllEntities();
                        WorldInfoProto worldInfoProto = WorldInfoProto.newBuilder().build();
                        for( Entity entity : allEntity) {
                            if(entity instanceof Area) {
                                // System.out.println("Area start");
                                AreaProto areaProto = AreaProto.newBuilder().build();
                                if(entity instanceof Road) {
                                    // System.out.println("Road start");
                                    if(entity instanceof Hydrant){
                                        // System.out.println("1");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("Hydrant").build();
                                    }
                                    else{
                                        // System.out.println("2");
                                        // System.out.println(((Road)entity).getStandardURN().toString());
                                        // System.out.println("2-2");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("Road").build();
                                    }
                                    // System.out.println("Raod End");
                                }
                                else if(entity instanceof Building) {
                                    // System.out.println("building start");
                                    if(entity instanceof AmbulanceCentre) {
                                        // System.out.println("3");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("AmbulanceCentre").build();
                                    }
                                    else if(entity instanceof FireStation) {
                                        // System.out.println("4");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("FireStation").build();
                                    }
                                    else if(entity instanceof PoliceOffice) {
                                        // System.out.println("5");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("PoliceOffice").build();
                                    }
                                    else if(entity instanceof GasStation) {
                                        // System.out.println("6");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("GasStation").build();
                                    }
                                    else if(entity instanceof Refuge) {
                                        // System.out.println("7");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("Refuge").build();
                                    }
                                    else{
                                        // System.out.println("8");
                                        areaProto = AreaProto.newBuilder(areaProto).setURN("Building").build();
                                    }
                                    // System.out.println("temp");
                                    if(((Building)entity).isFloorsDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setFloors(((Building)entity).getFloors()).build();//int
                                    }
                                    if(((Building)entity).isIgnitionDefined()){
                                        areaProto = AreaProto.newBuilder(areaProto).setIgnition(((Building)entity).getIgnition()).build();//boolean
                                    }
                                    if(((Building)entity).isFierynessDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setFieryness(((Building)entity).getFieryness()).build();//int
                                    }
                                    if(((Building)entity).isBrokennessDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setBrokenness(((Building)entity).getBrokenness()).build();//int
                                    }
                                    if(((Building)entity).isBuildingCodeDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setBuildingCode(((Building)entity).getBuildingCode()).build();//int
                                    }
                                    if(((Building)entity).isBuildingAttributesDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setBuildingAttributes(((Building)entity).getBuildingAttributes()).build();//int
                                    }
                                    if(((Building)entity).isGroundAreaDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setGroundArea(((Building)entity).getGroundArea()).build();//int
                                    }
                                    if(((Building)entity).isTotalAreaDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setTotalArea(((Building)entity).getTotalArea()).build();//int
                                    }
                                    if(((Building)entity).isTemperatureDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setTemperature(((Building)entity).getTemperature()).build();//int
                                    }
                                    if(((Building)entity).isImportanceDefined()) {
                                        areaProto = AreaProto.newBuilder(areaProto).setImportance(((Building)entity).getImportance()).build();//int
                                    }
                                    areaProto = AreaProto.newBuilder(areaProto).setIsOnFire(((Building)entity).isOnFire()).build();//boolean
                                    // System.out.println("building done");
                                }
                                else {
                                    areaProto = AreaProto.newBuilder(areaProto).setURN("Area").build();
                                }
                                if(((Area)entity).isXDefined()) {
                                    // System.out.println("x");
                                    areaProto = AreaProto.newBuilder(areaProto).setX(((Area)entity).getX()).build();//int
                                }
                                if(((Area)entity).isYDefined()) {
                                    // System.out.println("y");
                                    areaProto = AreaProto.newBuilder(areaProto).setY(((Area)entity).getY()).build();//int
                                }
                                if(((Area)entity).isEdgesDefined()) {
                                    // System.out.println("edges");
                                    List<Edge> edges = ((Area)entity).getEdges();//list<Edge>
                                    for(Edge edge : edges) {
                                        EdgeProto edgeProto =EdgeProto.newBuilder().build(); 
                                        if(edge.getStartX() !=0){
                                            edgeProto = EdgeProto.newBuilder(edgeProto).setStartX(edge.getStartX()).build();//int
                                        }
                                        if(edge.getStartY() !=0){
                                            edgeProto = EdgeProto.newBuilder(edgeProto).setStartY(edge.getStartY()).build();//int
                                        }
                                        if(edge.getEndX() !=0){
                                            edgeProto = EdgeProto.newBuilder(edgeProto).setEndX(edge.getEndX()).build();//int
                                        }
                                        if(edge.getEndY()!=0) {
                                            edgeProto = EdgeProto.newBuilder(edgeProto).setEndY(edge.getEndY()).build();//int
                                        }
                                        if(edge.getNeighbour()!=null) {
                                            edgeProto = EdgeProto.newBuilder(edgeProto).setNeighbour(edge.getNeighbour().getValue()).build();//int(ID)
                                        }
                                        areaProto = AreaProto.newBuilder(areaProto).addEdges(edgeProto).build();
                                    }
                                    // System.out.println("edges done");
                                }
                                // System.out.println("temp3");
                                if(((Area)entity).isBlockadesDefined()) {
                                    List<EntityID> blockades = ((Area)entity).getBlockades();//list{entityID} -> get int value using getValue()
                                    for(EntityID blockade : blockades) {
                                        areaProto = AreaProto.newBuilder(areaProto).addBlockades(blockade.getValue()).build();
                                    }
                                }
                                // System.out.println("temp2");
                                int[] apexList = ((Area)entity).getApexList();//int[]
                                for(int apex : apexList) {
                                    areaProto = AreaProto.newBuilder(areaProto).addApexList(apex).build();
                                }
                                List<EntityID> neighbours = ((Area)entity).getNeighbours();
                                for(EntityID neighbour : neighbours) {
                                    areaProto = AreaProto.newBuilder(areaProto).addNeighbours(neighbour.getValue()).build();
                                }
                                areaProto = AreaProto.newBuilder(areaProto).setID(entity.getID().getValue()).build();
                                worldInfoProto = WorldInfoProto.newBuilder(worldInfoProto).addAreas(areaProto).build();
                                // System.out.println("Area end");
                            }
                            
                            else if(entity instanceof Human) {
                                // System.out.println("Human start");
                                HumanProto humanProto = HumanProto.newBuilder().build();
                                if(entity instanceof FireBrigade) {
                                    humanProto = HumanProto.newBuilder(humanProto).setURN("FireBrigade").build();
                                    if(((FireBrigade)entity).isWaterDefined()) {
                                        humanProto = HumanProto.newBuilder(humanProto).setWater(((FireBrigade)entity).getWater()).build();//int
                                    }
                                }
                                else if(entity instanceof PoliceForce) {
                                    humanProto = HumanProto.newBuilder(humanProto).setURN("PoliceForce").build();
                                }
                                else if(entity instanceof AmbulanceTeam) {
                                    humanProto = HumanProto.newBuilder(humanProto).setURN("AmbulanceTeam").build();
                                }
                                else if(entity instanceof Civilian) {
                                    humanProto = HumanProto.newBuilder(humanProto).setURN("Civilian").build();
                                }
                                else {
                                    humanProto = HumanProto.newBuilder(humanProto).setURN("Human").build();
                                }
                                if(((Human)entity).isPositionDefined()){
                                    humanProto = HumanProto.newBuilder(humanProto).setPositionID(((Human)entity).getPosition().getValue()).build();//position of entity ID
                                }
                                if(((Human)entity).isPositionHistoryDefined()) {
                                    int[] positionHistory = ((Human)entity).getPositionHistory();//int[]
                                    for(int position : positionHistory) {
                                        humanProto = HumanProto.newBuilder(humanProto).addPositionHistory(position).build();
                                    }
                                }
                                if(((Human)entity).isTravelDistanceDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setTravelDistance(((Human)entity).getTravelDistance()).build();//int
                                }
                                if(((Human)entity).isXDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setX(((Human)entity).getX()).build();//int
                                }
                                if(((Human)entity).isYDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setY(((Human)entity).getY()).build();//int
                                }
                                if(((Human)entity).isBuriednessDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setBuriedness(((Human)entity).getBuriedness()).build();//int
                                }
                                if(((Human)entity).isDamageDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setDamage(((Human)entity).getDamage()).build();//int
                                }
                                if(((Human)entity).isHPDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setHP(((Human)entity).getHP()).build();//int
                                }
                                if(((Human)entity).isStaminaDefined()) {
                                    humanProto = HumanProto.newBuilder(humanProto).setStamina(((Human)entity).getStamina()).build();//int
                                }
                                if(((Human)entity).isDirectionDefined()){
                                    humanProto = HumanProto.newBuilder(humanProto).setDirection(((Human)entity).getDirection()).build();//int
                                }
                                humanProto = HumanProto.newBuilder(humanProto).setID(entity.getID().getValue()).build();
                                worldInfoProto = WorldInfoProto.newBuilder(worldInfoProto).addHumans(humanProto).build();
                                // System.out.println("Human end");
                            }
                            else if(entity instanceof Blockade) {
                                // System.out.println("Blockade start");
                                BlockadeProto blockadeproto = BlockadeProto.newBuilder().build();
                                if(((Blockade)entity).isXDefined()) {
                                    blockadeproto = BlockadeProto.newBuilder(blockadeproto).setX(((Blockade)entity).getX()).build();
                                }
                                if(((Blockade)entity).isYDefined()) {
                                    blockadeproto = BlockadeProto.newBuilder(blockadeproto).setY(((Blockade)entity).getY()).build();
                                }
                                if(((Blockade)entity).isApexesDefined()) {
                                    int[] apexList = ((Blockade)entity).getApexes();
                                    for(int apex : apexList) {
                                        blockadeproto = BlockadeProto.newBuilder(blockadeproto).addApexList(apex).build();
                                    }
                                }
                                if(((Blockade)entity).isPositionDefined()) {
                                    blockadeproto = BlockadeProto.newBuilder(blockadeproto).setPositionID(((Blockade)entity).getPosition().getValue()).build();
                                }
                                if(((Blockade)entity).isRepairCostDefined()) {
                                    blockadeproto = BlockadeProto.newBuilder(blockadeproto).setCost(((Blockade)entity).getRepairCost()).build();
                                }
                                blockadeproto = BlockadeProto.newBuilder(blockadeproto).setURN("Blockade").build();
                                blockadeproto = BlockadeProto.newBuilder(blockadeproto).setID(entity.getID().getValue()).build();
                                worldInfoProto = WorldInfoProto.newBuilder(worldInfoProto).addBlockades(blockadeproto).build();
                                // System.out.println("Blockade end");
                            }
                            else{
                                // System.out.println("Else start");
                                ElseProto elseproto = ElseProto.newBuilder().build();
                                elseproto = ElseProto.newBuilder(elseproto).setURN("Else").build();
                                elseproto = ElseProto.newBuilder(elseproto).setID(entity.getID().getValue()).build();
                                worldInfoProto = WorldInfoProto.newBuilder(worldInfoProto).addElses(elseproto).build();
                                // System.out.println("Else end");
                            }
                        }
                        worldInfoProto = WorldInfoProto.newBuilder(worldInfoProto).setTime(time).build();
                        // for (AgentProxy next : agents) {
                        //     Entity tempAgent = next.getControlledEntity;
                        // }
                        // System.out.println("Generate worldproto successfully");
                        ActionType actionType;
                        actionType = blockingStub.runTimestep(worldInfoProto);
                        if (actionType.getActionType()==0) {
                            break;
                        }
                    }
                    catch(Exception e) {
                        System.out.println("grpc error");
                        Logger.warn(e.getMessage());
                        break;
                    }
                }
                if (time == 0) {
                    fireStarted();
                }
                if (isShutdown) {
                    return;
                }
                ++time;
                // Work out what the agents can see and hear (using the commands from the previous timestep).
                // Wait for new commands
                // Send commands to simulators and wait for updates
                // Collate updates and broadcast to simulators
                // Send perception, commands and updates to viewers
                Timestep nextTimestep = new Timestep(time);
                Logger.info("Timestep " + time);
                Logger.debug("Sending agent updates");
                long start = System.currentTimeMillis();
                sendAgentUpdates(nextTimestep, previousTimestep == null ? new HashSet<Command>() : previousTimestep.getCommands());
                long perceptionTime = System.currentTimeMillis();
                Logger.debug("Waiting for commands");
                Collection<Command> commands = waitForCommands(time);
                nextTimestep.setCommands(commands);
                log.writeRecord(new CommandsRecord(time, commands));
                long commandsTime = System.currentTimeMillis();
                Logger.debug("Broadcasting commands");
                ChangeSet changes = sendCommandsToSimulators(time, commands);
                //                simulatorUpdates.show(changes);
                nextTimestep.setChangeSet(changes);
                log.writeRecord(new UpdatesRecord(time, changes));
                long updatesTime = System.currentTimeMillis();
                // Merge updates into world model
                worldModel.merge(changes);
                long mergeTime = System.currentTimeMillis();
                Logger.debug("Broadcasting updates");
                sendUpdatesToSimulators(time, changes);
                sendToViewers(nextTimestep);
                long broadcastTime = System.currentTimeMillis();
                Logger.debug("Computing score");
                double s = score.score(worldModel, nextTimestep);
                long scoreTime = System.currentTimeMillis();
                nextTimestep.setScore(s);
                Logger.info("Timestep " + time + " complete");
                Logger.debug("Score: " + s);
                Logger.debug("Perception took        : " + (perceptionTime - start) + "ms");
                Logger.debug("Agent commands took    : " + (commandsTime - perceptionTime) + "ms");
                Logger.debug("Simulator updates took : " + (updatesTime - commandsTime) + "ms");
                Logger.debug("World model merge took : " + (mergeTime - updatesTime) + "ms");
                Logger.debug("Update broadcast took  : " + (broadcastTime - mergeTime) + "ms");
                Logger.debug("Score calculation took : " + (scoreTime - broadcastTime) + "ms");
                Logger.debug("Total time             : " + (scoreTime - start) + "ms");
                fireTimestepCompleted(nextTimestep);
                previousTimestep = nextTimestep;
                Logger.debug("Commands: " + commands);
                Logger.debug("Timestep commands: " + previousTimestep.getCommands());
            }
        }
        finally {
            Logger.popLogContext();
        }
    }

    /**
       Shut down the kernel. This method will notify all agents/simulators/viewers of the shutdown.
    */
    public void shutdown() {
        synchronized (this) {
            if (isShutdown) {
                return;
            }
            Logger.info("Kernel is shutting down");
            ExecutorService service = Executors.newFixedThreadPool(agents.size() + sims.size() + viewers.size());
            List<Callable<Object>> callables = new ArrayList<Callable<Object>>();
            for (AgentProxy next : agents) {
                final AgentProxy proxy = next;
                callables.add(Executors.callable(new Runnable() {
                        @Override
                        public void run() {
                            proxy.shutdown();
                        }
                    }));
            }
            for (SimulatorProxy next : sims) {
                final SimulatorProxy proxy = next;
                callables.add(Executors.callable(new Runnable() {
                        @Override
                        public void run() {
                            proxy.shutdown();
                        }
                    }));
            }
            for (ViewerProxy next : viewers) {
                final ViewerProxy proxy = next;
                callables.add(Executors.callable(new Runnable() {
                        @Override
                        public void run() {
                            proxy.shutdown();
                        }
                    }));
            }
            try {
                service.invokeAll(callables);
            }
            catch (InterruptedException e) {
                Logger.warn("Interrupted during shutdown");
            }
            try {
                log.writeRecord(new EndLogRecord());
                log.close();
            }
            catch (LogException e) {
                Logger.error("Error closing log", e);
            }
            Logger.info("Kernel has shut down");
            isShutdown = true;
            fireShutdown();
        }
    }

    private void sendAgentUpdates(Timestep timestep, Collection<Command> commandsLastTimestep) throws InterruptedException, KernelException, LogException {
        perception.setTime(time);
        communicationModel.process(time, commandsLastTimestep);
        for (AgentProxy next : agents) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            ChangeSet visible = perception.getVisibleEntities(next);
            Collection<Command> heard = communicationModel.getHearing(next.getControlledEntity());
            EntityID id = next.getControlledEntity().getID();
            timestep.registerPerception(id, visible, heard);
            log.writeRecord(new PerceptionRecord(time, id, visible, heard));
            next.sendPerceptionUpdate(time, visible, heard);
        }
    }

    private Collection<Command> waitForCommands(int timestep) throws InterruptedException {
        Collection<Command> commands = commandCollector.getAgentCommands(agents, timestep);
        Logger.debug("Raw commands: " + commands);
        commandFilter.filter(commands, getState());
        Logger.debug("Filtered commands: " + commands);
        return commands;
    }

    /**
       Send commands to all simulators and return which entities have been updated by the simulators.
    */
    private ChangeSet sendCommandsToSimulators(int timestep, Collection<Command> commands) throws InterruptedException {
        for (SimulatorProxy next : sims) {
            next.sendAgentCommands(timestep, commands);
        }
        // Wait until all simulators have sent updates
        ChangeSet result = new ChangeSet();
        for (SimulatorProxy next : sims) {
            Logger.debug("Fetching updates from " + next);
            result.merge(next.getUpdates(timestep));
        }
        return result;
    }

    private void sendUpdatesToSimulators(int timestep, ChangeSet updates) throws InterruptedException {
        for (SimulatorProxy next : sims) {
            next.sendUpdate(timestep, updates);
        }
    }

    private void sendToViewers(Timestep timestep) {
        for (ViewerProxy next : viewers) {
            next.sendTimestep(timestep);
        }
    }

    private Set<KernelListener> getListeners() {
        Set<KernelListener> result;
        synchronized (listeners) {
            result = new HashSet<KernelListener>(listeners);
        }
        return result;
    }

    private void fireStarted() {
        for (KernelListener next : getListeners()) {
            next.simulationStarted(this);
        }
    }

    private void fireShutdown() {
        for (KernelListener next : getListeners()) {
            next.simulationEnded(this);
        }
    }

    private void fireTimestepCompleted(Timestep timestep) {
        for (KernelListener next : getListeners()) {
            next.timestepCompleted(this, timestep);
        }
    }

    private void fireAgentAdded(AgentProxy agent) {
        for (KernelListener next : getListeners()) {
            next.agentAdded(this, agent);
        }
    }

    private void fireAgentRemoved(AgentProxy agent) {
        for (KernelListener next : getListeners()) {
            next.agentRemoved(this, agent);
        }
    }

    private void fireSimulatorAdded(SimulatorProxy sim) {
        for (KernelListener next : getListeners()) {
            next.simulatorAdded(this, sim);
        }
    }

    private void fireSimulatorRemoved(SimulatorProxy sim) {
        for (KernelListener next : getListeners()) {
            next.simulatorRemoved(this, sim);
        }
    }

    private void fireViewerAdded(ViewerProxy viewer) {
        for (KernelListener next : getListeners()) {
            next.viewerAdded(this, viewer);
        }
    }

    private void fireViewerRemoved(ViewerProxy viewer) {
        for (KernelListener next : getListeners()) {
            next.viewerRemoved(this, viewer);
        }
    }

    public void reset() {
        this.previousTimestep = null;
        this.time = 0;
    }
}
