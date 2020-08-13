package adf.launcher.option;

import rescuecore2.Constants;
import rescuecore2.config.Config;

public class OptionGRPC extends Option {
	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getKey() {
		return "-r";
	}

	@Override
	public void setValue(Config config, String data) {
		config.setValue(Constants.GRPC_PORT_NUMBER_KEY, data);
	}
}
