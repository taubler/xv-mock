package com.taubler.vxmock.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taubler.vxmock.Mock;
import com.taubler.vxmock.io.commands.ExitCommand;
import com.taubler.vxmock.io.commands.HelpCommand;
import com.taubler.vxmock.io.commands.RefreshRoutesCommand;
import com.taubler.vxmock.io.commands.RoutesCommand;

public class CommandListener {
	
	private Mock mock;
	private boolean listening;
	
	private Map<String, Command> commandMap = new HashMap<>();
	private List<? extends Command> commands = Arrays.asList(
			new ExitCommand(), new HelpCommand(), new RoutesCommand(), new RefreshRoutesCommand()
			);
	
	public CommandListener() {
		for (Command command : commands) {
			commandMap.put(command.command(), command);
		}
	}
	
	public void listen(Mock mock) throws Exception {
		if (listening) throw new IllegalStateException("CommandListener is already listening");
		listening = true;
		
		this.mock = mock;
		while (true) {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s = bufferRead.readLine();
		    Command command = commandMap.get(s);
		    if (command != null) {
		    	command.execute(this);
		    } else {
		    	RuntimeMessager.output("Unknown command: " + command);
		    }
		}
	}
	
	public Mock getMock() {
		return this.mock;
	}
	
	public Map<String, Command> getCommandMap() {
		return commandMap;
	}

}
