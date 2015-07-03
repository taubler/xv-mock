package com.taubler.vxmock.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class DateUtil {

	public static final String ANSI_DATE_FORMAT = "yyyy-MM-dd";
	public static final String ANSI_TIME_FORMAT = "HH:mm:ss";
	public static final String ANSI_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final Pattern ymdRegexp = Pattern.compile ("\\d{4}-\\d{2}-\\d{2}");
	private static final Pattern ansiTimeRegexp = Pattern.compile ("\\d{2}:\\d{2}:\\d{2}");
	private static final Pattern ansiDateTimeRegexp = Pattern.compile ("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
	
	/**
	 * Determines whether the provided String represents an ANSI date (i.e. yyyy-MM-dd)
	 * @param s String that potentially represents an ANSI date
	 * @return <i>true</i> if <i>s</i> represents an ANSI date; <i>false</i> otherwise.
	 */
	public static boolean isAnsiDateOnly(String s) {
		Matcher m = ymdRegexp.matcher (s);
		return m.matches();
	}

	/**
	 * Determines whether the provided String represents an ANSI time (i.e. HH:mm:ss)
	 * @param s String that potentially represents an ANSI time
	 * @return <i>true</i> if <i>s</i> represents an ANSI time; <i>false</i> otherwise.
	 */
	public static boolean isAnsiTimeOnly(String s) {
		Matcher m = ansiTimeRegexp.matcher (s);
		return m.matches();
	}

	/**
	 * Determines whether the provided String represents an ANSI date/time (i.e. yyyy-MM-dd HH:mm:ss)
	 * @param s String that potentially represents an ANSI date/time
	 * @return <i>true</i> if <i>s</i> represents an ANSI date/time; <i>false</i> otherwise.
	 */
	public static boolean isAnsiDateTime(String s) {
		Matcher m = ansiDateTimeRegexp.matcher (s);
		return m.matches();
	}

	/**
	 * Simple utility that determines whether the combination of <i>start</i> and <i>end</i>
	 * represent a block of time. In other words, determines whether <i>start</i> &lt; <i>end</i>
	 * @param start
	 * @param end
	 * @return whether <i>start</i> &lt; <i>end</i>
	 */
    public static boolean isBlock (Date start, Date end) {
    	return end.getTime() > start.getTime();
    }
	
	public static int compareYmdParts(Date one, Date two) {
		one = clearTime(one);
		two = clearTime(two);
		return one.compareTo(two);
	}
	
	/**
	 * Return a Date (no time part - midnight) given a String of format "YYYY-MM-DD".
	 * N.B. This will throw an exception if the String includes extra characters
	 * (e.g. HH:mm:ss).
	 * 
	 * @param s String of format "YYYY-MM-DD"
	 * @return Date
	 */
	static public Date fromYmd (String s) {
		Matcher m = DateUtil.ymdRegexp.matcher (s);
		if ( ! m.matches ())
			throw new RuntimeException ("Pattern of argument s must match regexp '" + ymdRegexp.pattern () + "'.");
		
		try {
			SimpleDateFormat dt = new SimpleDateFormat ("yyyy-MM-dd"); 
			return dt.parse (s);
		}
		catch (ParseException e) {
			throw new RuntimeException ("ParseException in fromYmd. " + e.getMessage ());
		}
	}
	
	/**
	 * Return a Date, given a String of time format "HH:mm:ss", representing today at the given time.
	 * N.B. This will throw an exception if the String includes extra characters
	 * (e.g. yyyy-MM-dd).
	 * 
	 * @param s String of format "HH:mm:ss"
	 * @return Date
	 */
	static public Date fromTime (String s) {
		Matcher m = DateUtil.ansiTimeRegexp.matcher (s);
		if ( ! m.matches ())
			throw new RuntimeException ("Pattern of argument s must match regexp '" + ansiTimeRegexp.pattern () + "'.");
		
		try {
			SimpleDateFormat dt = new SimpleDateFormat (ANSI_TIME_FORMAT); 
			Date d = dt.parse (s);
			Calendar timeCal = Calendar.getInstance();
			timeCal.setTime(d);
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
			return cal.getTime();
		}
		catch (ParseException e) {
			throw new RuntimeException ("ParseException in fromTime for input " + s + "; " + e.getMessage ());
		}
	}
	
	/**
	 * Return a Date (with time part) given a String of format "YYYY-MM-DD HH:mm:ss".
	 * @param s String of format "YYYY-MM-DD HH:mm:ss"
	 * @return Date
	 * @throws ParseException
	 */
	static public Date fromAnsi (String s) {
		try {
			SimpleDateFormat dt = new SimpleDateFormat (ANSI_DATE_TIME_FORMAT); 
			return dt.parse (s);
		}
		catch (ParseException e) {
			throw new RuntimeException ("ParseException in fromAnsi. " + e.getMessage ());
		}
	}
	
	
	/**
	 * Sets the time on the Date as specified by the arguments.
	 * @param d Date
	 * @param hour (0-23)
	 * @param minute (0-59)
	 * @param second (0-59)
	 * @param milliseconds (0-9999)
	 * @return resulting Date with new time
	 */
	static public Date setTime (Date d, int hour, int minute, int second, int milliseconds) {
		Calendar c = Calendar.getInstance ();
		c.setTime (d);
		c.set (Calendar.HOUR_OF_DAY, hour);
		c.set (Calendar.MINUTE, minute);
		c.set (Calendar.SECOND, second);
		c.set (Calendar.MILLISECOND, milliseconds);

		Date rv = c.getTime ();
		return rv;
	}
	
	static public Date setTime (Date date, int hour, int minute, int second) { return setTime (date, hour, minute, second, 0); }
	static public Date setTime (Date date, int hour, int minute) { return setTime (date, hour, minute, 0, 0); }
	static public Date setTime (Date date, int hour) { return setTime (date, hour, 0, 0, 0); }
	
	
	/**
	 * Return a Date with its time set to midnight (i.e. zeroed out to the first instant of the day)
	 * @param date
	 * @return A new Date whose day matches that of <i>date</i>, and whose time is zeroed out.
	 */
	static public Date clearTime (Date date) { return setTime (date, 0, 0, 0, 0); }
	
	/**
	 * Sets all of the time fields of <i>cal</i> to zero.
	 * @param cal <code>Calendar</code> whose time should be zeroed out.
	 */
	public static void clearTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	/**
	 * Ensures that the given Strings each represent an ANSI datetime, and that
	 * <i>firstDate</i> occurs before <i>secondDate</i>
	 * @param firstDate The earlier of the two ANSI datetimes
	 * @param secondDate The later of the two ANSI datetimes
	 */
	public static void ensureDatesAreSequential(String firstDate, String secondDate) {
		if (!isAnsiDateTime(firstDate) || !isAnsiDateTime(secondDate)) {
			throw new RuntimeException( String.format("%s and %s must both be ANSI datetimes", firstDate, secondDate) );
		}
        Date first = fromAnsi(firstDate);
        Date second = fromAnsi(secondDate);
        if (first.compareTo(second) >= 0) {
            throw new RuntimeException( String.format(
                    "%s must be earlier than %s", firstDate, secondDate) );
        }
	}
	
	/**
	 * Ensures that the given Strings each represent an ANSI date (no time), and that
	 * <i>firstDate</i> occurs before <i>secondDate</i>
	 * @param firstDay The earlier of the two ANSI dates
	 * @param secondDay The later of the two ANSI dates
	 */
	public static void ensureDaysAreSequential(String firstDay, String secondDay) {
		ensureDaysAreSequential(firstDay, secondDay, false);
	}

	
	/**
	 * Ensures that the given Strings each represent an ANSI date (no time), and that
	 * <i>firstDate</i> occurs before (or at the same time as, if <i>allowEquals</i> == true) <i>secondDate</i>
	 * @param firstDay The earlier of the two ANSI dates
	 * @param secondDay The later of the two ANSI dates
	 */
	public static void ensureDaysAreSequential(String firstDay, String secondDay, boolean allowEqual) {
		if (!isAnsiDateOnly(firstDay) || !isAnsiDateOnly(secondDay)) {
			throw new RuntimeException( String.format("%s and %s must both be ANSI dates (without time info)", firstDay, secondDay) );
		}
        Date first = fromYmd(firstDay);
        Date second = fromYmd(secondDay);
        if (allowEqual) {
        	if (first.compareTo(second) > 0) {
        		throw new RuntimeException( String.format(
        				"%s must be earlier than %s", firstDay, secondDay) );
        	}
        } else {
        	if (first.compareTo(second) >= 0) {
        		throw new RuntimeException( String.format(
        				"%s must be earlier than %s", firstDay, secondDay) );
        	}
        }
	}
	
	/**
	 * Returns an ANSI date-only string (YYYY-MM-DD) representing 
	 * <i>days</i> days away from the given ANSI day.
	 * @param ansiDay Base ANSI day string
	 * @param days Number of days to add to <i>ansiDay</i>. Can be negative.
	 * @return ANSI date string
	 */
	public static String incrementAnsiDayBy(String ansiDay, int days) {
		Date d = fromYmd(ansiDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, days);
		d = cal.getTime();
		return toYmd(d);
	}
	
	/**
	 * Transforms in-place the given <code>Calendar</code> into the next instance of a Friday.
	 * It does this by adding the correct number value of the DATE field
	 * @param cal Calendar to be updated.
	 * @param currFridayRemains boolean indicating what to do if cal currently
	 * represents a Friday. If <i>true</i>, then no change is made. If <i>false</i>,
	 * then <i>cal</i> is increased to the following Friday.
	 */
	public static void makeNextFriday(Calendar cal, boolean currFridayRemains) {
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			cal.add(Calendar.DATE, 4);
			break;
		case Calendar.TUESDAY:
			cal.add(Calendar.DATE, 3);
			break;
		case Calendar.WEDNESDAY:
			cal.add(Calendar.DATE, 2);
			break;
		case Calendar.THURSDAY:
			cal.add(Calendar.DATE, 1);
			break;
		case Calendar.FRIDAY:
			if (!currFridayRemains)
				cal.add(Calendar.DATE, 7);
			break;
		case Calendar.SATURDAY:
			cal.add(Calendar.DATE, 6);
			break;
		case Calendar.SUNDAY:
			cal.add(Calendar.DATE, 5);
			break;
		}
		
	}
	
	/**
	 * Return a Date offset by the number of days specified.
	 * @param d starting Date
	 * @param numDays number of days +/-
	 * @return resulting Date
	 */
	public static Date offsetUsingDays (Date d, int numDays) {
		Calendar c = Calendar.getInstance ();
		c.setTime (d);
		c.add (Calendar.DATE, numDays);

		Date rv = c.getTime ();
		return rv;
	}
	
	public static Date beforeUsingDays (Date optDateToday, int numDays) {
		if (optDateToday == null)
			optDateToday = clearTime (new Date ());
		
		return offsetUsingDays (optDateToday, -1 * numDays);	
	}
	
	/**
	 * Gets the field Calendar field @see Calendar.get ()
	 * @param d date
	 * @param field Calendar field to return (e.g. Calendar.HOUR, Calendar.YEAR)
	 * @return
	 */
	static public int getField (Date d, int field) {
		Calendar c = Calendar.getInstance ();
		c.setTime (d);

		int rv = c.get (field);
		return rv;
	}
	
	/**
	 * Gets the numeric day of week (in terms of a Calendar.*DAY constant)
	 * represented by <i>d</i>
	 * @param d Date whose day-of-week we are seeking
	 * @return One of the Calendar constants (e.g. Calendar.SUNDAY)
	 */
	static public int getDayOfWeek (Date d) {
		return getField (d, Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Signed number of days between the two dates
	 * @param start
	 * @param end
	 * @return
	 */
	public static int daysBetween (Date start, Date end) {
		Calendar c = Calendar.getInstance ();
		
		c.setTime (start);
		long startMillis = c.getTimeInMillis ();
		
		c.setTime (end);
		long endMillis = c.getTimeInMillis ();
		
		return (int) TimeUnit.MILLISECONDS.toDays (endMillis - startMillis);
	}
	
	/**
	 * Given two ANSI day Strings (without time information), returns the 
	 * number of days in the sequence created by the two. For example, if 
	 * <i>startDay</i> and <i>endDay</i> are equal, then <i>1</i> would
	 * be returned. If <i>startDay</i> was 2014-03-05 and <i>endDay</i>
	 * was 2014-03-06, then <i>2</i> would be returned. If <i>startDay</i> 
	 * was 2014-03-05 and <i>endDay</i> was 2014-03-07, then <i>3</i> 
	 * would be returned (because 2014-03-06 would be implicitly included).
	 * @param startDay ANSI day String (without time information)
	 * @param endDay ANSI day String (without time information)
	 * @return The number of days in the sequence created by
	 * <i>startDay</i> and <i>endDay</i>.
	 */
	public static int daysInSequence(String startDay, String endDay) {
		if (!isAnsiDateOnly(startDay) || !isAnsiDateOnly(endDay)) {
			throw new RuntimeException( String.format("%s and %s must both be ANSI dates (without time info)", startDay, endDay) );
		}
//		if (startDay.equals( endDay )) {
//			return 1;
//		}
		
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime( fromYmd(startDay) );
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime( fromYmd(endDay) );
        int daysBetween = 1;
        while (cal1.before(cal2)) {
        	cal1.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
	}
	
	public static String toYmd (Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
		return sdf.format (d);
	}
	
	public static String toAnsi (Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat (ANSI_DATE_TIME_FORMAT);
		return sdf.format (d);
	}
	
	public static String toFriendlyTime (Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("h:mm a");
		return sdf.format (d);
	}
	
	public static String toAnsiFull (Date d) {
		// N.B. "T" is allowed separator between date and time (eliminates single
		//      whitespace for parsing).  Trailing Z is ISO time zone (e.g. -08)
		
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");
		return sdf.format (d);
	}
	
	/**
	 * Formats a Date 
	 * @param d Not-null Date to be formatted
	 * @return Formatted date e.g. "Saturday, Jun 07"
	 */
	public static String toFullWeekdayMonthName (Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("EEEE, MMMM dd");
		return sdf.format (d);
	}

	/**
	 * Formats a Date 
	 * @param d Not-null Date to be formatted
	 * @return Formatted date e.g. "Saturday, Jun 07 2:30 PM"
	 */
	public static String toFullWeekdayMonthNameFriendlyTime (Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("EEEE, MMMM dd h:mm a");
		return sdf.format (d);
	}

	/**
	 * Formats a Date 
	 * @param d Not-null Date to be formatted
	 * @return Formatted date e.g. "June 07, 2014"
	 */
	public static String toFullMonthNameDate (Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("MMMMM dd, yyyy");
		return sdf.format (d);
	}
	
	/**
	 * Transforms a <code>Date</code> into a String formatted like 
	 * <i>Day-of-week, Mon day</i>; for example, <i>Mon, March 24</i>.
	 * @param d <code>Date</code> to be converted to a string
	 * @return Formatted version of <i>d</i>.
	 */
	public static String toDowMonthDay(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("EEE, MMM d");
		return sdf.format (d);
	}
	
	/**
	 * Returns the descriptive day-of-week represented by <i>d</i>; e.g. <i>Friday</i>.
	 * @param d Date to convert to day-of-week.
	 * @return The full, human-readable day of week represented by <i>d</i>.
	 */
	public static String toDayOfWeek(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat ("EEEE");
		return sdf.format (d);
	}

	/**
	 * Returns the descriptive day-of-week represented by <i>d</i>;
	 * e.g. <i>Friday</i> (or <i>friday</i>, depending on the value of <i>lowercase</i>).
	 * @param d Date to convert to day-of-week.
	 * @param lowercase boolean indicating whether the returned value should be
	 * all lowercase (if true) or initial-capped (if false).
	 * @return The full, human-readable day of week represented by <i>d</i>.
	 */
	public static String toDayOfWeek(Date d, boolean lowercase) {
		String dow = toDayOfWeek(d);
		return (lowercase) ? dow.toLowerCase() : dow;
	}
	
	/**
	 * Returns the current UNIX timestamp (i.e. representing in seconds)
	 * as an int. This is a convenience function to satisfy a common
	 * legacy database requirement.
	 * @return The current UNIX timestamp (which represents seconds
	 * since the epoch).
	 */
	public static int unixTsInt() {
		return (int)getUnixTimestamp();
	}
	
	public static long getUnixTimestamp () {
		return System.currentTimeMillis() / 1000;
	}
	
	public static long getUnixTimestamp (Date d) {
		long ts = d.getTime () / 1000;
		return ts;
	}
	
	// -- ANSI representations of specific relative dates
	
	/**
	 * Returns an ANSI datetime String representing now
	 * @return
	 */

	public static String nowAnsi () {
		return toAnsi (new Date());
	}
	
	/**
	 * Returns an ANSI datetime String representing the first instant (i.e midnight) of today
	 * @return
	 */
	public static String firstInstantTodayAnsi() {
		Date d = clearTime( new Date() );
		return toAnsi(d);
	}

    /**
     * Add hours
     * @param date
     * @param hour
     * @return
     */
    public static Date addHours(Date date, int hour) {

        if (date == null) {
            date = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hour);

        return cal.getTime();
    }
	
	/**
	 * Returns the ANSI date representation of the day that is
	 * <i>numDaysPastToday</i> in the future.
	 * @param numDaysPastToday number of days in the future (or past,
	 * if a negative value is provided) for which we want to create
	 * and ANSI date representation
	 * @return ANSI date string in the form of <i>YYYY-MM-DD</i>
	 */
	public static String ansiDayInTheFuture(int numDaysPastToday) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, numDaysPastToday);
		return toYmd(cal.getTime());
	}
}
