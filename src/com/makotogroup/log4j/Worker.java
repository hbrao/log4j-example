package com.makotogroup.log4j;

/**
 * Base class for the workers in the sample application.
 */
public abstract class Worker extends Basic implements Runnable {
    public static final String OBJECT_NAME = "Worker";
    
    private Queue<WorkUnit> queue;
    private int workFactor;
    private long numberOfUnitsProcessed;
    private long totalProcessingTime;
    private boolean stopCalled;
    private boolean suspended;

    public Worker (Queue<WorkUnit> queue, int workFactor) {
        super();
        this.queue = queue;
        this.workFactor = workFactor;
    }

    public float getAverageUnitProcessingTime () {
        return  (numberOfUnitsProcessed > 0) ? (float)totalProcessingTime/(float)numberOfUnitsProcessed :
                0.0f;
    }

    public abstract void run ();

    public int getWorkFactor () {
        return  workFactor;
    }

    public boolean isSuspended () {
        return  suspended;
    }

    public synchronized void stop () {
        stopCalled = true;
    }

    public synchronized void suspend () {
        suspended = true;
    }

    public synchronized void resume () {
        suspended = false;
        notifyAll();
    }

    public void reset() {
		  setNumberOfUnitsProcessed(0);
	  }
    
    public abstract long doWork(WorkUnit workUnit);

		public void incrementNumberOfUnitsProcessed(long increment) {
			numberOfUnitsProcessed += increment;
		}
		
		public void incrementTotalProcessingTime(long increment) {
			totalProcessingTime += increment;
		}

    public long getNumberOfUnitsProcessed () {
      return  numberOfUnitsProcessed;
	  }
    
	  public void setNumberOfUnitsProcessed (long value) {
	      numberOfUnitsProcessed = value;
	  }
	
		public long getTotalProcessingTime() {
			return totalProcessingTime;
		}

		public void setTotalProcessingTime(long totalProcessingTime) {
			this.totalProcessingTime = totalProcessingTime;
		}

		public boolean isStopCalled() {
			return stopCalled;
		}

		public void setStopCalled(boolean stopCalled) {
			this.stopCalled = stopCalled;
		}

		public void setWorkFactor(int workFactor) {
			this.workFactor = workFactor;
		}

		public void setSuspended(boolean suspended) {
			this.suspended = suspended;
		}

		public Queue<WorkUnit> getQueue() {
			return queue;
		}

}



