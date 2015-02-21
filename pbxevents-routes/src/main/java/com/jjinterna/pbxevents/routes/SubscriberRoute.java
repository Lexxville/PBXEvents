package com.jjinterna.pbxevents.routes;

import java.util.Collection;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;

import com.jjinterna.pbxevents.model.PBXEvent;

public class SubscriberRoute extends RouteBuilder {

	private String camelRouteId;
	Collection<String> eventSelectors;
	private String eventSource;

	@Override
	public void configure() throws Exception {

		JaxbDataFormat jaxb = new JaxbDataFormat();
		jaxb.setContextPath(PBXEvent.class.getPackage().getName());

		from("activemq:topic:PBXEvents?username=karaf&password=karaf%s", getSelectorString())
			.id(camelRouteId + ".subscribe")
			.unmarshal(jaxb)
			.to("direct:start");
	}

	private String getSelectorString() {
		String selector = null;
		if (eventSelectors != null) {
			for (String ev : eventSelectors) {
				if (selector == null) {
					selector = "";
				} else {
					selector += ",";
				}
				selector += "'" + ev + "'";
			}
			selector = "PBXEvent IN(" + selector + ")";
		}
		if (eventSource != null && eventSource.length() > 0) {
			if (selector == null) {
				selector = "";
			} else {
				selector += " AND ";
			}
			selector += "PBXEventSource='" + eventSource + "'";
		}
		if (selector == null)
			return "";
		return "&selector=" + selector;
	}
}
