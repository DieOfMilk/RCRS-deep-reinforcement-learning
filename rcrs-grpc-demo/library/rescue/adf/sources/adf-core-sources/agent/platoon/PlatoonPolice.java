package adf.agent.platoon;

import adf.agent.config.ModuleConfig;
import adf.agent.develop.DevelopData;
import adf.component.tactics.TacticsPoliceForce;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.StandardEntityURN;

import java.util.EnumSet;

public class PlatoonPolice extends Platoon<PoliceForce> {
    private TacticsPoliceForce tactics;
	public PlatoonPolice(TacticsPoliceForce tactics, boolean isPrecompute, boolean isDebugMode, ModuleConfig moduleConfig, DevelopData developData) {
        super(tactics, isPrecompute, DATASTORAGE_FILE_NAME_POLICE, isDebugMode, moduleConfig, developData);
        this.tactics = tactics;
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.POLICE_FORCE);
	}

	@Override
	protected void postConnect() {
		super.postConnect();
    }

    public PlatoonPolice setgRPC(String gRPCNo) {
        this.tactics.setgRPC(gRPCNo);
        return this;
    }
    
}
