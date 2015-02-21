package com.jjinterna.pbxevents.mediator.activemq.internal;

import org.apache.camel.builder.RouteBuilder;

public class PublisherRouteBuilder extends RouteBuilder {
	
	private String destinationName;
	private String destinationType;
	
	public PublisherRouteBuilder(String destinationName, String destinationType) {
		this.destinationName = destinationName;
		this.destinationType = destinationType;
	}

	@Override
	public void configure() throws Exception {
		from("direct:publish")
			.id("publish")
			.startupOrder(1)
			.toF("activemq:%s:%s?deliveryPersistent=false&username=karaf&password=karaf",
					destinationType,
					destinationName);
	}

}
