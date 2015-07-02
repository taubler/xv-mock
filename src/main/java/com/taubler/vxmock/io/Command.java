package com.taubler.vxmock.io;



public interface Command {
	
	String command();
	String helpText();
	void execute(CommandListener commandListener) throws Exception;

}
