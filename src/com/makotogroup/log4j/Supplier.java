package com.makotogroup.log4j;

import org.apache.log4j.Logger;

/**
 * Represents the supplier component of the sample application. Does some work,
 * and places the results into a queue.
 */
public class Supplier extends Worker {

	public static final String ROLE = "Supplier";

	public static final int DEFAULT_WORK_FACTOR = 450;

	private static final Logger log = Logger.getLogger(Supplier.class);
	
	public Supplier(Queue<WorkUnit> queue) {
		this(queue, DEFAULT_WORK_FACTOR);
	}

	public Supplier(Queue<WorkUnit> queue, int workFactor) {
		super(queue, workFactor);
	}

	public void run() {
		getLogger().trace("Entering method run()...");
		// **********
		// This is where the "work" takes place. In a real-world
		// / application that uses this pattern, this logic would
		// / be replaced by the real application logic.
		// **********
		while (!isStopCalled()) {
			while (isSuspended()) {
				if (nap()) {
					getLogger().warn("Got interrupted!");
				}
			}
			nap();
			WorkUnit unitOfWork = new WorkUnit();
			unitOfWork.activate();
			unitOfWork.log();// Object, log thyself!
			// Burn some cycles...
			incrementTotalProcessingTime(doWork(unitOfWork));
			// Now place a WorkUnit in the Queue
			getQueue().put(unitOfWork);
			incrementNumberOfUnitsProcessed(1);
		}
		getLogger().trace("Exiting method run()");
	}
	
	public boolean nap() {
		boolean ret = false;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			ret = true;// got interrupted!
		}
		return ret;
	}

	public long doWork(WorkUnit workUnit) {
		if (getLogger().isTraceEnabled()) {
			getLogger().trace("Entering doWork()...");
		}
		if (getLogger().isDebugEnabled()) {
			workUnit.log();
		}

		long totalElapsedTime = workUnit.calculate(getWorkFactor());
		
		if (getLogger().isTraceEnabled()) {
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
