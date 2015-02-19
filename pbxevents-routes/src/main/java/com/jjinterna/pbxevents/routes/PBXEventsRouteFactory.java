package com.jjinterna.pbxevents.routes;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PBXEventsRouteFactory implements ManagedServiceFactory {

	protected CamelContext camelContext;
	private BundleContext bundleContext;

	private String configurationPid;
	private ServiceRegistration managedServiceReg;

	private Map<String, PBXEventsRoute> routes = Collections.synchronizedMap(new HashMap<String, PBXEventsRoute>());

	protected static final Logger LOG = LoggerFactory
			.getLogger(PBXEventsRouteFactory.class);
	
	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	public void setConfigurationPid(String configurationPid) {
		this.configurationPid = configurationPid;
	}
	
	public void init() throws Exception {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, configurationPid);
		managedServiceReg = bundleContext.registerService(ManagedServiceFactory.class.getName(), this, properties);
		postInit();
	}
	
	public void postInit() throws Exception {}
	
	public void destroy() {
		managedServiceReg.unregister();
	}

	@Override
	public String getName() {
		return configurationPid;
	}

	@Override
	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		LOG.debug("Updating...");

		Boolean routeEnable = true;
		Object obj = properties.get("Enable");
		if (obj != null) {
			routeEnable = (Boolean) obj;
		}

		PBXEventsRoute route = routes.get(pid);
		if (route == null) {
			LOG.info("Building new route");
			if (routeEnable) {
				addRoute(pid, createRoute(pid, properties));
			}
		} else {
			LOG.info("Updating existing route");			
			removeRoute(pid, route);
			if (routeEnable) {
				addRoute(pid, createRoute(pid, properties));
			}
		}
	}

	public abstract PBXEventsRoute createRoute(String pid, Dictionary properties);

	@Override
	public void deleted(String pid) {
		LOG.debug("Deleting...");
		if (routes.get(pid) != null)
			removeRoute(pid, routes.get(pid));
	}

	private void removeRoute(final String pid, final PBXEventsRoute route) {
		try {
			camelContext.stopRoute(route.getRouteId());
			camelContext.removeRoute(route.getRouteId());
		} catch (Exception e) {
			LOG.error("Failed to stop and remove route " + route.getRouteId());
		}
		routes.remove(pid);
	}

	private void addRoute(final String pid, final PBXEventsRoute route) {
		try {
			camelContext.addRoutes(route);
		} catch (Exception e) {
			LOG.error("Failed to add route", e);
		}
		routes.put(pid, route);
	}

}
