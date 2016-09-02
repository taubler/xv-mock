package com.taubler.vxmock.io.commands;

import com.taubler.vxmock.io.Command;
import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;

public class RoutesCommand implements Command {

	@Override
	public String command() {
		return "routes";
	}

	@Override
	public String helpText() {
		return "Prints out the routes that the server is currently configured to handle";
	}

	@Override
	public void execute(CommandListener commandListener) {
		RuntimeMessager.output("\nROUTES:");
        commandListener.getMock().printRouteMatchers();
	}

}
