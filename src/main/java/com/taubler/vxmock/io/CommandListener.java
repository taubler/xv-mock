package com.taubler.vxmock.io;

import io.vertx.core.AbstractVerticle;

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

public class CommandListener extends AbstractVerticle {
	
	private Mock mock;
	private boolean listening;
	
	private Map<String, Command> commandMap = new HashMap<>();
	private List<? extends Command> commands = Arrays.asList(
			new ExitCommand(), new HelpCommand(), new RoutesCommand(), new RefreshRoutesCommand()
			);
	
	public CommandListener(Mock mock) {
		this.mock = mock;
		for (Command command : commands) {
			commandMap.put(command.command(), command);
		}
	}
	
	public void start() {
		if (listening) throw new IllegalStateException("CommandListener is already listening");
		listening = true;
		
		vertx.executeBlocking(future -> {
			while (true) {
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    try {
					String s = bufferRead.readLine();
					if (s != null && !"".equals(s)) {
					    Command command = commandMap.get(s);
					    if (command != null) {
					    	command.execute(this);
					    } else {
					    	RuntimeMessager.output("Unknown command: " + command);
					    }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, rh -> {});
		
	}
	
	public Mock getMock() {
		return this.mock;
	}
	
	public Map<String, Command> getCommandMap() {
		return commandMap;
	}

}
