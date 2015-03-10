package com.jjinterna.pbxevents.discovery.lldp.internal;

import org.apache.camel.builder.RouteBuilder;

public class SnmpTrapRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("snmp:127.0.0.1:1620?protocol=udp&type=TRAP")
		.setBody(xpath("//entry/agent-addr/text()"))
		.to("direct:start");					
	}

}
