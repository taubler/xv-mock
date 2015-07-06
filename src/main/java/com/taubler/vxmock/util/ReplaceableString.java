package com.taubler.vxmock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReplaceableString {
	
	protected List<Part> parts = new ArrayList<>();
	
	protected ReplaceableString() {}
	
	public String replace(Map<String, String> replacements) {
		StringBuilder sb = new StringBuilder();
		
		parts.forEach(p -> {
			sb.append(p.finalString(replacements));
		});
		
		return sb.toString();
	}
	
	public static ReplaceableString fromString(String s) {
		ReplaceableString rs = new ReplaceableString();
		Part currPart = null;
		
		if (rs != null) {
			char[] chs = s.toCharArray();
			for (int i=0; i<chs.length; i++) {
				char ch = chs[i];
				if (ch == '$' && i < chs.length && chs[i+1] == '{') {
					finalizePart(currPart, rs);
					currPart = new TokenPart();
					i += 1;
				} else if (ch == '}' && currPart != null && currPart.isToken()) {
					finalizePart(currPart, rs);
					currPart = new StaticPart();
				} else {
					if (currPart == null) {
						currPart = new StaticPart();
					}
					currPart.append(ch);
				}
			}
			finalizePart(currPart, rs);
		}
		
		return rs;
	}

	public static void finalizePart(Part part, ReplaceableString rs) {
		if (part != null) {
			part.finalize();
			if (part.hasFinalString()) {
				rs.parts.add(part);
			}
		}
	}
	
	protected static abstract class Part {
		
		public StringBuilder sb = new StringBuilder();
		public String string;
		
		protected Part() {}
		
		protected Part(String s) {
			sb.append(s);
			finalize();
		}
		
		public void append(char c) {
			sb.append(c);
		}
		
		public void finalize() {
			this.string = this.sb.toString();
			this.sb = null;
		}
		
		public boolean hasFinalString() {
			return string != null && !"".equals(string);
		}
		
		public abstract String finalString(Map<String, String> tokens);
		public abstract boolean isToken();

		@Override
		public String toString() {
			return "Part [string=" + string + "]";
		}
				
	}
	
	protected static class StaticPart extends Part {
		
		public StaticPart() {}
		public StaticPart(String s) {
			super(s);
		}
		
		public String finalString(Map<String, String> tokens) {
			return string;
		}

		public boolean isToken() {
			return false;
		}
		
	}
	
	protected static class TokenPart extends Part {
		
		public TokenPart() {}
		public TokenPart(String s) {
			super(s);
		}
		
		public String finalString(Map<String, String> tokens) {
			String tokenVal = tokens.get(string);
			return (tokenVal == null) ? "" : tokenVal;
		}

		public boolean isToken() {
			return true;
		}
		
	}

}
