package com.innsync.menu.tenant;

public class TenantContext {
	// A ThreadLocal variable to hold the database identifier for the current request.
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    // Sets the tenant identifier for the current thread.
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    // Gets the tenant identifier for the current thread.
    public static String getCurrentTenant() {
        return currentTenant.get();
    }
    
    // Clears the tenant identifier for the current thread to prevent memory leaks.
    public static void clear() {
        currentTenant.remove();
    }
}


/*
 * To achieve database-per-tenant, we need to solve a puzzle: How do we tell our application which tenant's database to 
 * connect to for a specific request?

AbstractRoutingDataSource: We will use a special class from Spring called AbstractRoutingDataSource. Think of it as a smart 
traffic controller for database connections. It holds a map of all the different tenant database connections and has one 
crucial method, determineCurrentLookupKey(), which decides which connection to use.

ThreadLocal (The Tenant Context): We need a way to pass the current tenant's database name from our JwtAuthFilter 
(where we'll get it) to our RoutingDataSource. Since these are separate components, we use a special Java variable 
called a ThreadLocal.

A ThreadLocal variable acts as a temporary, private storage for a single request thread. The flow will be:

The JwtAuthFilter intercepts a request.

It looks up the tenant's database name.

It sets this name into our static ThreadLocal variable.

When a database operation happens, our RoutingDataSource reads the name from the ThreadLocal to choose the correct connection.

After the request is finished, the filter clears the ThreadLocal variable.

Action: Create the TenantContext Holder
Our first step is to create the class that will manage this ThreadLocal variable.

In your menu-service project, create a new package named com.innsync.menu.tenant.

Inside this new package, create a new class file named TenantContext.java.

Add the following code:
 * 
 */