package com.jjinterna.pbxevents.asterisk.queue;

import java.util.Dictionary;

import com.jjinterna.pbxevents.routes.PBXEventsRoute;
import com.jjinterna.pbxevents.routes.PBXEventsRouteFactory;

public class QueueLogRouteFactory extends PBXEventsRouteFactory {

	public PBXEventsRoute createRoute(String pid, Dictionary properties) {
		PBXEventsRoute route = new QueueLogRoute();
		route.setRouteId(pid);
		return route;
	}

	@Override
	public void postInit() throws Exception {
		camelContext.addRoutes(new QueueLogPublisherRoute());
	}
}
