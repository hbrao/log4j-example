package com.makotogroup.log4j;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.or.ObjectRenderer;

public class Queue<E extends WorkUnit> extends ArrayBlockingQueue<E> {

	private static final Logger log = Logger.getLogger(Queue.class);

	public Logger getLogger() {
		return log;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String OBJECT_NAME = "Queue";

	public Queue(int capacity) {
		super(capacity);
		log.info(this);// Queue renders itself as a message
	}

	public void put(E e) {
		if (log.isTraceEnabled()) {
			log.trace("Entering method put(" + e.getClass().getName() + ")");
		}
		if (log.isDebugEnabled()) {
			log.debug("Putting object into the queue...");
//			e.log(); // Object, log thyself!
		}

		// Start the clock running...
		long startTime = System.currentTimeMillis();
		try {
			super.put(e);
		} catch (InterruptedException ex) {
			// What to do?...
			log.warn("InterruptedException occurred!", ex);
		}
		// Stop the clock
		putWaitTime += System.currentTimeMillis() - startTime;

		if (log.isTraceEnabled()) {
			log.trace("Leaving method put(" + e.getClass().getName() + ")");
		}
	}

	public E take() {
		E ret = null;
		if (log.isTraceEnabled()) {
			log.trace("Entering method take()...");
		}
		if (log.isDebugEnabled()) {
			log.debug("Attempting to retrieve an object from the queue...");
		}

		// Start the clock running...
		long startTime = System.currentTimeMillis();
		try {
			ret = super.take();
		} catch (InterruptedException ex) {
			// What to do?...
			log.warn("InterruptedException occurred!", ex);
		}
		// Stop the clock
		takeWaitTime += System.currentTimeMillis() - startTime;

		if (log.isDebugEnabled()) {
			ret.activate();// this is a bit of a hack, but makes the MDC consistently spit out information so it can all be correlated
			log.debug("Got object from the queue...");
//			ret.log();// Object, log thyself...
		}
		if (log.isTraceEnabled()) {
			log.trace("Leaving method take()...");
		}
		return ret;
	}

	// ////////
	// MANAGEMENT INTERFACE
	// ////////
	private long numberOfItemsProcessed;

	private long putWaitTime;

	private long takeWaitTime;

	private int queueSize;

	public long getNumberOfItemsProcessed() {
		return numberOfItemsProcessed;
	}

	public long getPutWaitTime() {
		return putWaitTime;
	}

	public long getTakeWaitTime() {
		return takeWaitTime;
	}

	public String getCurrentLogLevel() {
		return getLogger().getEffectiveLevel().toString();
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setLoggerLevel(String levelName) {
		getLogger().setLevel(Basic.getLoggerLevelForName(getLogger(), levelName));
	}
	
	public void logError(String message) {
		getLogger().error(message);
	}

	public static class QueueRenderer implements ObjectRenderer {
		@SuppressWarnings("unchecked")
		@Override
		public String doRender(Object o) {
			if (o instanceof Queue) {
				StringBuilder sb = new StringBuilder();
	
				Queue q = (Queue)o;
				sb.append("QUEUE (Rendered): -> " + q.toString() + " <- ");
				sb.append("\n"); sb.append("\tNumberOfItemsProcessed -> "); sb.append(q.getNumberOfItemsProcessed()); 
				sb.append("\n"); sb.append("\tPutWaitTime            -> "); sb.append(q.getPutWaitTime()); 
				sb.append("\n"); sb.append("\tTakeWaitTime           -> "); sb.append(q.getTakeWaitTime()); 
				sb.append("\n"); sb.append("\tQueueSize              -> "); sb.append(q.getQueueSize()); 
				
				return sb.toString();
			}
			return o.toString();
		}
	}

}
