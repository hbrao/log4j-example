package com.makotogroup.log4j;

import org.apache.log4j.or.ObjectRenderer;

public class WorkUnitRenderer implements ObjectRenderer {

	@Override
	public String doRender(Object o) {
		if (o instanceof WorkUnit) {
			WorkUnit workUnit = (WorkUnit)o;
			return "WORKUNIT: Last prime# -> " + workUnit.getLastPrimeNumberCalculated(); 
		}
		return o.toString();
	}

}
