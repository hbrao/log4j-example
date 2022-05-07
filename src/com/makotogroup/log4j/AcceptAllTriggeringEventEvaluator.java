package com.makotogroup.log4j;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/**
 * TriggeringEventEvaluator that accepts all events
 * 
 * @author J Steven Perry
 *
 */
public class AcceptAllTriggeringEventEvaluator implements TriggeringEventEvaluator {

	public boolean isTriggeringEvent(LoggingEvent event) {
		// This TEV accepts all events
		return true;
	}

}
