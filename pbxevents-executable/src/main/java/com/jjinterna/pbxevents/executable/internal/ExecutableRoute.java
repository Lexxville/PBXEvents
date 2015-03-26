package com.jjinterna.pbxevents.executable.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.commons.lang.Validate;

import com.jjinterna.pbxevents.model.PBXEvent;

public class ExecutableRoute extends RouteBuilder {

	private String executable;
	private String args;
	private Integer timeout;
	private Integer period;

	@Override
	public void configure() throws Exception {
		checkProperties();

		JaxbDataFormat jaxb = new JaxbDataFormat();
		jaxb.setContextPath(PBXEvent.class.getPackage().getName());
		
		from("timer:timer-exec?period={{period}}")
			.to("exec:{{executable}}?timeout={{timeout}}&args={{args}}")
			.split().tokenize("\n").streaming().unmarshal(jaxb).to("direct:publish");
	}

	private void checkProperties() {
		Validate.notNull(executable, "executable property is not set");
		Validate.notNull(args, "args property is not set");
		Validate.notNull(timeout, "timeout property is not set");
		Validate.notNull(period, "period property is not set");
	}

}
