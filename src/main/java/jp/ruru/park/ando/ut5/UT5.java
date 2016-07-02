package jp.ruru.park.ando.ut5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * TimeSetting
 * @author t-ando
 *
 */
public abstract class UT5 {
	
	/** time format */
	private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	/** date format */
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	/** Success */
	protected static final String ST_SUCCES = "Success";
	
	/** Error */
	protected static final String ST_ERROR = "Error";
	
	/** Failure */
	protected static final String ST_FAIILURE = "Failure";
	
	/** Skipped */
	protected static final String ST_SKIPPED = "Skipped";

	/**
	 * Constructor 
	 * @param name name
	 * @param time time
	 * @param timestamp timestamp
	 */
	public UT5(String name,double time,long timestamp) {
		this(name,time,timestamp,timestamp);
	}
	/**
	 * Constructor
	 * @param name name
	 * @param time time
	 * @param timestamp time stamp
	 * @param oldTimestamp old time stamp
	 */
	public UT5(String name,double time,long timestamp,long oldTimestamp) {
		this.name = name;
		this.time = time;
		this.timestamp = timestamp;
		this.oldTimestamp = oldTimestamp;
	}
	
	/**
	 * create string to date
	 * @param timestampString time stamp string
	 * @return time stamp
	 */
	public static long createStringToDate(String timestampString) {
		Date timestamp;
		try {
			 timestamp = (new SimpleDateFormat(TIME_FORMAT)).parse(timestampString);
		} catch (ParseException e1) {
			System.err.println("ParseException e1=" + e1.getMessage());
			 timestamp = new Date();
		}
		return timestamp.getTime();
	}
	
	/**
	 * get time
	 * @return time
	 */
	public final double getTime() {
		return time;
	}
	
	/**
	 * time to string
	 * @return time
	 */
	public final String getTimeString() {
		if (this.getTime() < 0) {
			return "---";
		}
		DecimalFormat dt = new DecimalFormat("0.000");
		return dt.format(this.getTime());
	}
	/**
	 * get name
	 * @return name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * class for HTML style sheet
	 * @return class for HTML
	 */
	public abstract String getHtmlClass();

	/**
	 * raw message to message for HTML.
	 * @param message raw message 
	 * @return message for HTML.
	 */
	public static String createMultiMessage(String message) {
		if (message == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		try (  StringReader reader = new StringReader(message.trim());
				BufferedReader br = new BufferedReader(reader);) {
			while (true) {
				String str = br.readLine();
				if (str == null) {
					break;
				}
				buffer.append(str);
				buffer.append("<br />\r\n");
			}
		} catch (IOException e) {
			buffer.delete(0, buffer.length());
			buffer.append(message);
		}
		return buffer.toString();
	}
	/**
	 * set message
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * get message
	 * @return message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * get time stamp
	 * @return time stamp
	 */
	public String getTimestamp() {
		return (new SimpleDateFormat(TIME_FORMAT)).format(timestamp);
	}
	/**
	 * get small time stamp
	 * @return time stamp
	 */
	public String getSmallTimestamp() {
		return (new SimpleDateFormat(DATE_FORMAT)).format(timestamp);
	}

	/**
	 * get small time stamp
	 * @return time stamp
	 */
	public String getOldTimestamp() {
		return (new SimpleDateFormat(TIME_FORMAT)).format(oldTimestamp);
	}
	
	/**
	 * get small time stamp
	 * @return time stamp
	 */
	public String getSmallOldTimestamp() {
		return (new SimpleDateFormat(DATE_FORMAT)).format(oldTimestamp);
	}

	/**
	 * set old time stamp
	 * @param oldTimestamp old time stamp
	 */
	public void setOldTimesamp(long oldTimestamp) {
		if (oldTimestamp < this.oldTimestamp) {
			this.oldTimestamp = oldTimestamp;
		}
	}
	
	/**
	 * get time stamp
	 * @return time stamp
	 */
	public long getRawTimestamp() {
		return this.timestamp;
	}

	/**
	 * get raw old time stamp
	 * @return old time stamp
	 */
	public long getRawOldTimesamp() {
		return this.oldTimestamp;
	}
	
	/** name */
	private final String name;
		
	/** message */
	private String message;

	/** time */
	private final double time;
	
	/** time stamp */
	private long timestamp;
	
	/** time stamp */
	private long oldTimestamp;
}
