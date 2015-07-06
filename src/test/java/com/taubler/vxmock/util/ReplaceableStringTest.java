package com.taubler.vxmock.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taubler.vxmock.util.ReplaceableString.StaticPart;
import com.taubler.vxmock.util.ReplaceableString.TokenPart;

public class ReplaceableStringTest {
	
	@Test
	public void testReplace() {
		ReplaceableString rs = new ReplaceableString();
		rs.parts = Arrays.asList(
				new ReplaceableString.StaticPart("Ayi"),
				new ReplaceableString.TokenPart("one"),
				new ReplaceableString.StaticPart("Aee"),
				new ReplaceableString.TokenPart("one"),
				new ReplaceableString.StaticPart("Bee"),
				new ReplaceableString.TokenPart("two"),
				new ReplaceableString.StaticPart("Cee"),
				new ReplaceableString.TokenPart("three")
				);
		
		Map<String, String> tokens = new HashMap<String, String>() {{
			put("one", "1"); put("two", "2"); put("three", "3");
		}};
		
		String result = rs.replace(tokens);
		assertNotNull(result);
		assertEquals("Ayi1Aee1Bee2Cee3", result);
	}
	
	@Test
	public void testFromString_twoTokens() {
		ReplaceableString rs = ReplaceableString.fromString("abc${one}defghi${two}");
		assertNotNull(rs);
		assertEquals(4, rs.parts.size());
		assertEquals("abc", rs.parts.get(0).string);
		assertEquals(StaticPart.class, rs.parts.get(0).getClass());
		assertEquals("one", rs.parts.get(1).string);
		assertEquals(TokenPart.class, rs.parts.get(1).getClass());
		assertEquals("defghi", rs.parts.get(2).string);
		assertEquals(StaticPart.class, rs.parts.get(2).getClass());
		assertEquals("two", rs.parts.get(3).string);
		assertEquals(TokenPart.class, rs.parts.get(3).getClass());
	}
	
	@Test
	public void testFromString_onlyOneToken() {
		ReplaceableString rs = ReplaceableString.fromString("${one}");
		assertNotNull(rs);
		assertEquals(1, rs.parts.size());
		assertEquals("one", rs.parts.get(0).string);
		assertEquals(TokenPart.class, rs.parts.get(0).getClass());
	}
	
	@Test
	public void testFromString_onlyTwoTokensInARow() {
		ReplaceableString rs = ReplaceableString.fromString("${one}${t}");
		assertNotNull(rs);
		assertEquals(2, rs.parts.size());
		assertEquals("one", rs.parts.get(0).string);
		assertEquals(TokenPart.class, rs.parts.get(0).getClass());
		assertEquals("t", rs.parts.get(1).string);
		assertEquals(TokenPart.class, rs.parts.get(1).getClass());
	}
	
	@Test
	public void testFromString_noTokens() {
		ReplaceableString rs = ReplaceableString.fromString("ayebee");
		assertNotNull(rs);
		assertEquals(1, rs.parts.size());
		assertEquals("ayebee", rs.parts.get(0).string);
		assertEquals(StaticPart.class, rs.parts.get(0).getClass());
	}
	
	@Test
	public void testFromString_empty() {
		ReplaceableString rs = ReplaceableString.fromString("");
		assertNotNull(rs);
		assertEquals(0, rs.parts.size());
	}
	
	@Test
	public void testFromString_emptyToken() {
		ReplaceableString rs = ReplaceableString.fromString("${}");
		assertNotNull(rs);
		assertEquals(0, rs.parts.size());
	}
	
	@Test
	public void testFromString_noTokens_specialChars() {
		ReplaceableString rs = ReplaceableString.fromString("%ay$ebe{e}");
		assertNotNull(rs);
		assertEquals(1, rs.parts.size());
		assertEquals("%ay$ebe{e}", rs.parts.get(0).string);
		assertEquals(StaticPart.class, rs.parts.get(0).getClass());
	}

}
