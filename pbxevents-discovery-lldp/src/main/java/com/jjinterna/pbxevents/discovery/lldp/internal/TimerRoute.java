package com.jjinterna.pbxevents.discovery.lldp.internal;

import org.apache.camel.builder.RouteBuilder;

public class TimerRoute extends RouteBuilder {

	@SuppressWarnings("unused")
	private String period;
	@SuppressWarnings("unused")	
	private String host;
	
	@Override
	public void configure() throws Exception {
		from("timer:discovery-lldp?period={{period}}")
			.setBody(simple("{{host}}"))
			.to("direct:start");					
	}

}
