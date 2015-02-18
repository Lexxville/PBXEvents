package com.jjinterna.pbxevents.routes;

import java.util.Dictionary;

public interface PBXEventsRouteFactory {

	public PBXEventsRoute createRoute(String id, Dictionary properties);

}
