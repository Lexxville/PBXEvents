package com.jjinterna.pbxevents.routes;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

public interface EventMediator {

	RouteBuilder publisher();
	RouteBuilder subscriber(List<EventSelector> selectors);
	RouteBuilder subscriber(List<EventSelector> selectors, String destUri);
	
	String queueUri(String queueName);
}
