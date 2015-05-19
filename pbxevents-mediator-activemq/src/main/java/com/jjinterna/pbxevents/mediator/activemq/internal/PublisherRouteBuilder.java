package com.jjinterna.pbxevents.mediator.activemq.internal;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import com.jjinterna.pbxevents.model.PBXEvent;

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
			.process(new Processor() {

				@Override
				public void process(Exchange exchange) throws Exception {
					Object obj = exchange.getIn().getBody();
					if (obj instanceof PBXEvent) {
						PBXEvent event = (PBXEvent) obj;
						if (event.getEventDateTimestamp() == 0) {
							event.setEventDateTimestamp(System.currentTimeMillis());
						}
						if (event.getEventId() == null) {
							event.setEventId(UUID.randomUUID().toString());
						}
						exchange.getIn().setHeader("PBXEventId", event.getEventId());
						exchange.getIn().setHeader("PBXEvent", event.getClass().getSimpleName());
					}
				}
				
			})
			.toF("activemq:%s:%s?username=karaf&password=karaf",
					destinationType,
					destinationName);
	}

}
