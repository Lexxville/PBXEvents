package com.jjinterna.pbxevents.routes;

import org.apache.camel.builder.RouteBuilder;

public abstract class PBXEventsRoute extends RouteBuilder {

	private String routeId;
	private String domain = "default";
	
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	public String getRouteId() {
		return routeId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
