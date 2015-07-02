package com.taubler.vxmock.io.commands;

import com.taubler.vxmock.io.Command;
import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;

public class RefreshRoutesCommand implements Command {

	@Override
	public String command() {
		return "refresh";
	}

	@Override
	public String helpText() {
		return "Refreshes the routes that the mock server handles";
	}

	@Override
	public void execute(CommandListener commandListener) throws Exception {
		RuntimeMessager.output("REFRESHING ROUTES...");
		commandListener.getMock().loadRoutes();
		RuntimeMessager.output("NEW ROUTES:");
		commandListener.getMock().getRouteMatcher().print();
	}

}
