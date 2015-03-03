package com.jjinterna.pbxevents.snmptrap.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents.model.ColdBootEvent;

public class SnmpTrapRoute extends RouteBuilder {

	private String camelRouteId;
	private String host;
	private Integer port;

	@Override
	public void configure() throws Exception {
		from("snmp:{{host}}:{{port}}?protocol=udp&type=TRAP&snmpCommunity={{snmpCommunity}}")
		.id(camelRouteId)
		.filter().xpath("//entry/enterprise='1.3.6.1.4.1.311.1.1.3.1.1'")
		.setBody(xpath("//entry/agent-addr/text()"))
		.bean(this, "toEvent")
		.to("direct:publish");
	}

	public ColdBootEvent toEvent(String agentAddress) {
		ColdBootEvent event = new ColdBootEvent();
		event.setAgentAddress(agentAddress);
		return event;
	}

	public void checkProperties() {
		Validate.notNull(host, "host property is not set");
		Validate.notNull(port, "port property is not set");		
	    Validate.notNull(camelRouteId, "camelRouteId property is not set");
	}

}
