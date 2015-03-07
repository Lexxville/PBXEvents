package com.jjinterna.pbxevents._3cx.cdrsocket.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents._3cx.cdrsocket.model.Call;

public class CdrSocketRoute extends RouteBuilder {

	private Integer port;
	
	@Override
	public void configure() throws Exception {
		checkProperties();
		
		JaxbDataFormat jaxb = new JaxbDataFormat();
		jaxb.setContextPath(Call.class.getPackage().getName());
		jaxb.setPartClass(Call.class.getName());
		
		from("netty4:tcp://0.0.0.0:{{port}}?sync=false&textline=true")
		.filter(body().startsWith("<Call"))
		.unmarshal(jaxb)
		.to("log:3cx");
	}

	private void checkProperties() {
		Validate.notNull(port, "port property is not set");
	}
}
