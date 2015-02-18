package com.jjinterna.pbxevents.routes.manage;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjinterna.pbxevents.routes.PBXEventsRoute;
import com.jjinterna.pbxevents.routes.PBXEventsRouteFactory;

public class PBXEventsManagedService implements ManagedServiceFactory {

	private CamelContext camelContext;
	private BundleContext bundleContext;

	private String configPid;
	private ServiceRegistration managedServiceReg;

	private Map<String, PBXEventsRoute> routes = new HashMap<>();
	private List<PBXEventsRouteFactory> factories;

	private static final Logger LOG = LoggerFactory
			.getLogger(PBXEventsManagedService.class);
	
	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public void setContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	public void setConfigPid(String configPid) {
		this.configPid = configPid;
	}
	
	public void init() {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, configPid);
		managedServiceReg = bundleContext.registerService(ManagedServiceFactory.class.getName(), this, properties);
	}
	
	public void destroy() {
		managedServiceReg.unregister();
	}

	@Override
	public String getName() {
		return "Factory for " + configPid;
	}

	@Override
	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		LOG.debug("Updating...");

		Boolean routeEnable = false;
		Object obj = properties.get("Enable");
		if (obj != null) {
			routeEnable = (Boolean) obj;
		}
		String routeName = (String) properties.get("Name");
		String factoryClass = (String) properties.get("Class");

		PBXEventsRoute route = routes.get(pid);
		if (route == null) {
			LOG.info("Building new route");
			if (routeEnable) {
				addRoute(pid, createRoute(factoryClass, routeName, properties));
			}
		} else {
			LOG.info("Updating existing route");			
			removeRoute(pid, route);
			if (routeEnable) {
				addRoute(pid, createRoute(factoryClass, routeName, properties));
			}
		}
	}

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
		if (route == null) {
			LOG.error("Failed to add empty route");
			return;
		}
		try {
			camelContext.addRoutes(route);
		} catch (Exception e) {
			LOG.error("Failed to add route", e);
		}
		routes.put(pid, route);
	}

	private PBXEventsRoute createRoute(String factoryClass, String id, Dictionary properties) {
		for (PBXEventsRouteFactory f : factories) {
			if (f.getClass().getSimpleName().equals(factoryClass))
				return f.createRoute(id, properties);
		}
		return null;
	}
}
