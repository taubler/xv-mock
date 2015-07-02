package com.taubler.vxmock.io.commands;

import com.taubler.vxmock.io.Command;
import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;

public class HelpCommand implements Command {

	@Override
	public String command() {
		return "help";
	}

	@Override
	public String helpText() {
		return "Prints the available list of commands";
	}

	@Override
	public void execute(CommandListener commandListener) {
		for (String cmn : commandListener.getCommandMap().keySet()) {
			String desc = commandListener.getCommandMap().get(cmn).helpText();
			RuntimeMessager.output(cmn + ":\t\t" + desc);
		}
	}

}
