/*
 * DynamicMBeanFacadeException.java
 *
 * Created on August 16, 2002, 11:34 AM
 */

package com.makotogroup.log4j;

/**
 *
 * @author  J Steven Perry
 */
public class DynamicMBeanFacadeException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


		/**
     * Creates a new instance of <code>DynamicMBeanFacadeException</code> without detail message.
     */
    public DynamicMBeanFacadeException() {
    }
    
    
    /**
     * Constructs an instance of <code>DynamicMBeanFacadeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DynamicMBeanFacadeException(String msg) {
        super(msg);
    }
}
