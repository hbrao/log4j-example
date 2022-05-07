package com.makotogroup.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.or.ObjectRenderer;

/**
 * Represents a unit of work in the example application.
 */
public class WorkUnit {

	public WorkUnit() {
		requestingUser = User.getRandomUser();
		requestId = hashCode();
	}
	
	public void activate() {
		MDC.put(RequestRenderer.USER_ID, getRequestingUser().getUserId());
		MDC.put(RequestRenderer.USER_NAME, getRequestingUser().getUserName());
		MDC.put(RequestRenderer.REQUEST_ID, getRequestId());
		requestStartTime = System.currentTimeMillis();
	}
	
	public void deactivate() {
		MDC.remove(RequestRenderer.USER_ID);
		MDC.remove(RequestRenderer.USER_NAME);
		MDC.remove(RequestRenderer.REQUEST_ID);
	}
	
	public String toString() {
		return "WORKUNIT #" + hashCode();
	}
	

	public void log() {
		log.log(log.getEffectiveLevel(), this);
	}

	private static final Logger log = Logger.getLogger(WorkUnit.class);
	
	private long lastPrimeNumberCalculated;
	private long requestStartTime;
	private int requestId;
	private User requestingUser;

	public int getRequestId() {
		return requestId;
	}

	public long getRequestStartTime() {
		return requestStartTime;
	}

	public User getRequestingUser() {
		return requestingUser;
	}

	public long getLastPrimeNumberCalculated() {
		return lastPrimeNumberCalculated;
	}

	/**
	 * In this method is where the "work" takes place. We need a way to simulate
	 * the application doing something, so we calculate the first workFactor
	 * primes, starting with zero. So, the work factor is equal to the number of
	 * primes that are calculated.
	 * 
	 * @return long - the number of milliseconds elapsed while doing work
	 */
	public long calculate(int numberOfPrimesToCalculate) {
		activate();
		log.trace("Entering calculate() method...");
		// There is probably an easier way to do this,
		/// but it seemed a good way to chew up some CPU
		long startTime = System.currentTimeMillis();
		long number = 1;
		int numberOfPrimesCalculated = 0;
		while (numberOfPrimesCalculated < numberOfPrimesToCalculate) {
			long currentNumber = 1; // start with 1
			long numberOfFactors = 0;
			while (currentNumber <= number) {
				if ((number % currentNumber) == 0) {
					// This is *definitely* the long way around, but
					/// remember, we *want* to eat up lots of CPU...
					numberOfFactors++;
				}
				currentNumber++;
			}
			// The number is prime if it only has two factors
			/// (i.e., itself and 1)
			if (numberOfFactors == 2) {
				numberOfPrimesCalculated++;
				lastPrimeNumberCalculated = number;
			}
			number++;
		}
		log.trace("Exiting calculate() method...");
		log.debug(this);
		return (System.currentTimeMillis() - startTime);
	}
	
	public static class RequestRenderer implements ObjectRenderer {
		public static final String USER_ID = "user.id";
		public static final String USER_NAME = "user.name";
		public static final String REQUEST_ID = "request.id";
		public static final String REQUEST_ELAPSED_TIME = "request.elapsed.time";
		@Override
		public String doRender(Object o) {
			String ret;
			if (o instanceof WorkUnit) {
				WorkUnit workUnit = (WorkUnit)o;
				StringBuilder sb = new StringBuilder();
				sb.append(REQUEST_ELAPSED_TIME);sb.append("=");sb.append(System.currentTimeMillis()-workUnit.getRequestStartTime());sb.append(",");
				sb.append(REQUEST_ID);sb.append("=");sb.append(MDC.get(REQUEST_ID));sb.append(",");
				sb.append(USER_ID);sb.append("=");sb.append(MDC.get(USER_ID));sb.append(",");
				sb.append(USER_NAME);sb.append("=");sb.append(MDC.get(USER_NAME));
				ret = sb.toString();
			} else {
				ret = o.toString();
			}
			return ret;
		}
	}

}
