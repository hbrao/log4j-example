package com.makotogroup.log4j;

import org.apache.log4j.Logger;

/**
 * This class pulls WorkUnit instances from a Queue and performs a certain
 * amount of "work", specified by workFactor.
 * 
 * @author J Steven Perry
 */
public class Consumer extends Worker {
	public static final String ROLE = "Consumer";

	public static final int DEFAULT_WORK_FACTOR = 750;

	private static final Logger log = Logger.getLogger(Consumer.class);

	public Consumer(Queue<WorkUnit> queue) {
		this(queue, DEFAULT_WORK_FACTOR);
	}
	
	public Consumer(Queue<WorkUnit> queue, int workFactor) {
		super(queue, workFactor);
	}

	public void run() {
		getLogger().trace("Entering method run()...");
		while (!isStopCalled()) {
			while (isSuspended()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			WorkUnit unitOfWork = null;
			while (unitOfWork == null) {
				unitOfWork = getQueue().take();
				unitOfWork.activate();
			}
			// Burn some cycles...
			incrementTotalProcessingTime(doWork(unitOfWork));
			incrementNumberOfUnitsProcessed(1);
			// Log the WorkUnit
			unitOfWork.log();// Object, log thyself!
			unitOfWork.deactivate();			
		}
		getLogger().trace("Exiting method run()");
	}

	public long doWork(WorkUnit workUnit) {
		if (getLogger().isTraceEnabled()) {
			getLogger().debug("Entering doWork()...");
		}
		if (getLogger().isDebugEnabled()) {
			workUnit.log();
		}

		long totalElapsedTime = workUnit.calculate(getWorkFactor());
		
		if (getLogger().isDebugEnabled()) {
			getLogger().trace("Exiting doWork()");
		}
		
		return totalElapsedTime;
	}

	public Logger getLogger() {
		return log;
	}

	@Override
	public void logError(String message) {
		getLogger().error(message);
	}
}
