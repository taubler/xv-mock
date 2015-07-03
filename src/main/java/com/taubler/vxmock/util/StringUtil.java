package com.taubler.vxmock.util;


public class StringUtil {
    
    public static String makeFullname(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        } else if (firstName == null) {
            return lastName;
        } else if (lastName == null) {
            return firstName;
        } else {
            return String.format("%s %s", firstName, lastName);
        }
    }
    
    public static enum Elipses {
        NONE, APPEND, INSERT
    }

    /**
     * Takes a typical database entity name (e.g. separated by underscores)
     * and converts it to its camelCase form (with the initial character lowercased).
     * @param s underscore-separated string
     * @return the camelCase version of s
     */
    public static String toCamelCase(String s) {
        s = s.toLowerCase();
        String[] parts = s.split("_");
        StringBuilder sb = new StringBuilder();
        int partNum = 0;
        for (String part : parts){
            sb.append( (partNum++ == 0) ? part : toInitialCaps(part) );
        }
        return sb.toString();
    }

    public static String toInitialCaps(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
    
    public static boolean boolVal(String s) {
        if (s == null || "".equals(s)) {
            return false;
        }
        if (org.apache.commons.lang.StringUtils.isNumeric(s)) {
            return !org.apache.commons.lang.StringUtils.containsOnly(s, "0");
        }
        String ls = s.toLowerCase();
        return ("true".equals(ls) || "t".equals(ls) || "yes".equals(ls) || "y".equals(ls));
    }
    
    public static String truncate(String longText, int maxChars) {
        return truncate(longText, maxChars, Elipses.NONE);
    }
    
    public static String truncate(String longText, int maxChars, Elipses elipses) {
        if (longText == null) {
            return null;
        }
        int textLen = longText.length();
        if (textLen <= maxChars) {
            return longText;
        }
        int end = (elipses == Elipses.INSERT) ? maxChars - 3 : maxChars;
        if (end < 0) end = 0;
        if (elipses == Elipses.NONE) {
            return longText.substring(0, end);
        } else {
            return longText.substring(0, end) + "...";
        }
    }
    
    /**
     * Returns an empty String if <i>s</i> is <i>null</i>; otherwise
     * simply returns <i>s</i>
     * @param s A String that may or may not be null
     * @return A non-null representation of <i>s</i>.
     */
    public static String nn(String s) {
        return (s == null) ? "" : s;
    }
    
    /**
     * Ensures that the given String represents a valid hexidecimal RGB color value
     * (e.g. <i>003cef</i> or <i>#AA3FDD</i>).
     * If it is, it returns the same String, prepending a <i>#</i> symbol if necessary.
     * Otherwise returns null.
     * @param s A String that may represent a hex color value.
     * @return If <i>s</i> is a hex color string, then <i>s</i> itself is returned
     * with any necessary "fixing up" performed. Otherwise, <i>null</i> is returned.
     */
    public static String ensureHexColor(String s) {
        if (s == null) return null;
        int len = s.length();
        boolean isHex = false;
        if (len == 6) {
            isHex = s.matches("[0-9A-Fa-f]+");
            s = "#" + s;
        } else if (len == 7) {
            isHex = s.charAt(0) == '#';
            isHex &= s.substring(1).matches("[0-9A-Fa-f]+");
        }
        return (isHex) ? s : null;
    }

}