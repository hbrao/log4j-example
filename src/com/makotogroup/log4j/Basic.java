package com.makotogroup.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Base class for the sample application.
 * 
 * @author J Steven Perry
 */
public abstract class Basic extends DynamicMBeanFacade {

	public Basic() {
		super();
	}
	
	private static final Logger log = Logger.getLogger(Basic.class);

	// Public accessor, allows for lazy initialization of Logger for subclasses
	public abstract Logger getLogger();

	public abstract void reset();
	
	public abstract void logError(String message);
	
	public void log() {
		getLogger().log(getLogger().getEffectiveLevel(), this);
	}

	public static Level getLoggerLevelForName(Logger logger, String levelName) {
		log.trace("Entering method getLoggerLevelForName(" + logger.toString() + ", " + levelName + ")...");
		log.debug("Parameters:");
		log.debug("\tLogger -> " + logger.toString());
		log.debug("\tLevel -> " + levelName);

		Level level = logger.getLevel();// default to current level

		// If the new level matches one of these, then set it accordingly,
		// / otherwise leave current logger level unchanged
		if (levelName.equalsIgnoreCase("debug")) {
			level = Level.DEBUG;
		} else if (levelName.equalsIgnoreCase("trace")) {
			level = Level.TRACE;
		} else if (levelName.equalsIgnoreCase("info")) {
			level = Level.INFO;
		} else if (levelName.equalsIgnoreCase("warn")) {
			level = Level.WARN;
		} else if (levelName.equalsIgnoreCase("error")) {
			level = Level.ERROR;
		} else if (levelName.equalsIgnoreCase("fatal")) {
			level = Level.FATAL;
		} else if (levelName.equalsIgnoreCase("all")) {
			level = Level.ALL;
		} else if (levelName.equalsIgnoreCase("off")) {
			level = Level.OFF;
		}

		log.debug("Returning value '" + level + "'");
		log.trace("Leaving method getLoggerLevelForName()...");
		return level;
	}

	public void setLoggerLevel(String levelName) {
		log.trace("Entering setLoggerLevel...");
		log.debug("Setting level to '" + levelName + "'");
		
		getLogger().setLevel(getLoggerLevelForName(getLogger(), levelName));
		
		log.trace("Leaving setLoggerLevel...");
	}

	// ////////
	// MANAGEMENT INTERFACE
	// ////////
	private long numberOfResets;

	public String getCurrentLogLevel() {
		return getLogger().getEffectiveLevel().toString();
	}

	public long getNumberOfResets() {
		return numberOfResets;
	}

}
