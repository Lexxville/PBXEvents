package com.jjinterna.pbxevents.tcx.callrouter.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

public class CallRouterResourceRoute extends RouteBuilder {

	private String httpUri;

	@Override
	public void configure() throws Exception {

		checkProperties();

		fromF("cxfrs:///3cx-callrouter?bindingStyle=SimpleConsumer&resourceClasses=%s", CallRouterResource.class.getName())
		.removeHeader("CamelHttpPath")
		.setHeader("CamelCxfRsResponseClass", constant(RoutingResponse.class.getName()))
		.to("cxfrs://{{httpUri}}")
		.to("velocity:routingResponse.tt");

	}

	private void checkProperties() {
		Validate.notNull(httpUri, "httpUri property is not set");
	}

}
