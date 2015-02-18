package com.jjinterna.pbxevents.routes;

import org.apache.camel.builder.RouteBuilder;

public abstract class PBXEventsRoute extends RouteBuilder {

	private String routeId;
	
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	public String getRouteId() {
		return routeId;
	}

}
