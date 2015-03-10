package com.jjinterna.pbxevents.routes;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

public interface EventMediator {

	public RouteBuilder publisher();
	public RouteBuilder toQueue(String queueName);
	public RouteBuilder subscriber(List<EventSelector> selectors);
	public RouteBuilder fromQueue(String queueName);
}
