package com.jjinterna.pbxevents.tcx.dnmonitor.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

public class DNMonitorRoute extends RouteBuilder {

	private String executable;
	private String args;
	private Integer timeout;
	private Integer period;

	@Override
	public void configure() throws Exception {
		checkProperties();

		from("timer:3cx-dnmonitor?period={{period}}")
			.to("exec:{{executable}}?timeout={{timeout}}&args={{args}}")
			.process(new Processor() {

				@Override
				public void process(Exchange exchange) throws Exception {
			        InputStream in = exchange.getIn().getBody(InputStream.class);
			        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			        String s;
			        while ((s = reader.readLine()) != null) {
			        	System.out.println(s);
			        }
					in.close();
				}
				
			});
	}

	private void checkProperties() {
		Validate.notNull(executable, "executable property is not set");
		Validate.notNull(args, "args property is not set");
		Validate.notNull(timeout, "timeout property is not set");
		Validate.notNull(period, "period property is not set");
	}

}
