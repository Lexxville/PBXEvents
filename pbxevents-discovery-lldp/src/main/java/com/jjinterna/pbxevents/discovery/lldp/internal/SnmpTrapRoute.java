package com.jjinterna.pbxevents.discovery.lldp.internal;

import org.apache.camel.builder.RouteBuilder;

public class SnmpTrapRoute extends RouteBuilder {

	private String queueUri;
	
	public SnmpTrapRoute(String queueUri) {
		this.queueUri = queueUri;
	}
	
	@Override
	public void configure() throws Exception {

		from("direct:snmptrap")
		  .transform(simple("${body.agentAddress}"))
		  .to(queueUri);

		from(queueUri)
		  .to("direct:start");
	
	}

}
