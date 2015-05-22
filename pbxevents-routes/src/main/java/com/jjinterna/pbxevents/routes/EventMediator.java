package com.jjinterna.pbxevents.routes;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ModelCamelContext;

public interface EventMediator {

	RouteBuilder publisher();
	RouteBuilder subscriber(List<EventSelector> selectors);
	RouteBuilder subscriber(List<EventSelector> selectors, String[] destUris);
	RouteBuilder subscriber(ModelCamelContext context);

	String queueUri(String queueName);
}
