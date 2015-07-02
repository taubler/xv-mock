package com.taubler.vxmock.io.commands;

import com.taubler.vxmock.io.Command;
import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;


public class ExitCommand implements Command {

	@Override
	public String command() {
		return "exit";
	}

	@Override
	public String helpText() {
		return "Shuts the server down";
	}

	@Override
	public void execute(CommandListener commandListener) {
		RuntimeMessager.output("Exiting");
    	System.exit(0);
	}

}
