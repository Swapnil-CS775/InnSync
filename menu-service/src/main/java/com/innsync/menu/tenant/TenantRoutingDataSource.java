package com.innsync.menu.tenant;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource{

	/**
     * This method is called by Spring to determine which database connection to use
     * for the current operation. It returns the key (the database name) that we
     * stored in our ThreadLocal TenantContext.
     */
	
	
	@Override
	protected Object determineCurrentLookupKey() {
		// TODO Auto-generated method stub
		return TenantContext.getCurrentTenant();
	}
	
}

/*
 * Concept: Implementing AbstractRoutingDataSource 🔀
We will create a class that extends Spring's AbstractRoutingDataSource. This class has one crucial abstract method we 
must implement:

determineCurrentLookupKey(): This method's only job is to return a "lookup key." Spring then uses this key to find the 
correct database connection from a map of available connections.

In our case, the lookup key is simply the name of the tenant's database (e.g., "innsync_nagpur_coffee_house_1"). Our 
implementation of this method will just retrieve this name from the TenantContext that our security filter will set for 
each request.

Action: Implement the Routing Logic
1. Create the TenantRoutingDataSource Class
In your com.innsync.menu.tenant package, create a new class file named TenantRoutingDataSource.java.
 * */
