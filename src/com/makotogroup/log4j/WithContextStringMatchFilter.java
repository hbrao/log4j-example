package com.makotogroup.log4j;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * TODO: Need Javadoc
 * @author steve
 *
 */
public class WithContextStringMatchFilter extends StringMatchFilter {
	
	private String contextPropertyToMatch;
	private String contextPropertyValueToMatch;
	public String getContextPropertyToMatch() {
		return contextPropertyToMatch;
	}
	public String getContextPropertyValueToMatch() {
		return contextPropertyValueToMatch;
	}
	public void setContextPropertyToMatch(String value) {
		this.contextPropertyToMatch = value;
	}
	public void setContextPropertyValueToMatch(String value) {
		this.contextPropertyValueToMatch = value;
	}
	
	@Override
	public int decide(LoggingEvent event) {
		int ret = super.decide(event);
		
		// Only check if StringMatchFilter result is DENY or NEUTRAL
		if (ret != Filter.ACCEPT) {
			Object mappedObject = event.getMDC(contextPropertyToMatch);
			if (mappedObject instanceof String) {
				String propertyValue = (String)mappedObject;
				if (propertyValue.equals(contextPropertyValueToMatch)) {
					ret = Filter.ACCEPT;
				}
			}
		}
		
		return ret;
	}

}
