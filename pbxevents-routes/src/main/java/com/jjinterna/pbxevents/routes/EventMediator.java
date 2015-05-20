package com.jjinterna.pbxevents.routes;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

public interface EventMediator {

	RouteBuilder publisher();
	RouteBuilder subscriber(List<EventSelector> selectors);
	RouteBuilder subscriber(List<EventSelector> selectors, String[] destUris);
	
	String queueUri(String queueName);
}
