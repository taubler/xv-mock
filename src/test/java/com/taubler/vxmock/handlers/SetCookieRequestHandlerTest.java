package com.taubler.vxmock.handlers;

import org.junit.Test;
import static org.junit.Assert.*;

public class SetCookieRequestHandlerTest {
	
	@Test
	public void testAppendAge() {
		SetCookieRequestHandler handler = new SetCookieRequestHandler();
		Long age = 123456L;
		handler.setAge(age);
		StringBuilder sb = new StringBuilder();
		handler.setAge(age);
		handler.appendAge(sb);
		assertTrue(sb.length() > 0);
	}

}
