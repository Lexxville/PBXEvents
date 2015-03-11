package com.jjinterna.pbxevents.snmptrap.internal;

import org.apache.camel.builder.RouteBuilder;

import com.jjinterna.pbxevents.model.PBXEvent;
import com.jjinterna.pbxevents.model.SnmpTrapEvent;

public class SnmpTrapRoute extends RouteBuilder {
	
	@SuppressWarnings("unused")
	private String host;
	@SuppressWarnings("unused")
	private Integer port;

	@Override
	public void configure() throws Exception {
//		from("snmp:{{host}}:{{port}}?protocol=udp&type=TRAP&snmpCommunity={{snmpCommunity}}")
//		.filter().xpath("//entry/enterprise='1.3.6.1.4.1.311.1.1.3.1.1'")
//		.setBody(xpath("//entry/agent-addr/text()"))
		from("netty4:tcp://{{host}}:{{port}}?sync=false&textline=true")
		  .filter(body().startsWith("SNMP-COMMUNITY-MIB::snmpTrapAddress.0"))
		  .transform(simple("${body.substring(38)}"))
		  .bean(this, "toEvent")
		  .setHeader("PBXEvent", constant("SnmpTrapEvent"))
		  .to("direct:publish");
	}

	public PBXEvent toEvent(String agentAddress) {
		SnmpTrapEvent event = new SnmpTrapEvent();
		event.setAgentAddress(agentAddress);
		return event;
	}

}
